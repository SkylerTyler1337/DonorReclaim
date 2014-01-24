package main.java.me.avastprods.donorreclaim;

import java.util.HashMap;

import main.java.me.avastprods.donorreclaim.io.PlayerFile;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.api.ExperienceAPI;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.sky8the2flies.signenchant.TokensAPI;
import com.sky8the2flies.signenchant.util.Tokens;
public class Main extends JavaPlugin {

	mcMMO p2 = null;
	Economy p3 = null;

	public void onEnable() {
		p2 = (mcMMO) Bukkit.getServer().getPluginManager().getPlugin("mcMMO");

		if (!setupEconomy()) {
			getLogger().info("Vault not found - disabling economy saves.");
		}
	}

	private boolean setupEconomy() {
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);

		if (economyProvider != null) {
			p3 = economyProvider.getProvider();
		}

		return (p3 != null);
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		String pname = sender.getName();
		if (cmd.getName().equalsIgnoreCase("reclaim")) {
			if (args.length == 0) {
				sender.sendMessage("Syntax: /reclaim god");
			}

			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("god")) {
					if (sender instanceof Player) {
						PlayerFile dataStorage = new PlayerFile(this, sender.getName());

						if (dataStorage.exists()) {
							double money = dataStorage.getConfig().getDouble("vault.money");
							int tokens = dataStorage.getConfig().getInt("senchant.tokens");

							HashMap<SkillType, Integer> level = new HashMap<SkillType, Integer>();
							for (SkillType skill : SkillType.values()) {
								level.put(skill, dataStorage.getConfig().getInt("mcmmo.skill." + skill.getSkillName() + ".level"));
							}

							HashMap<SkillType, Integer> xp = new HashMap<SkillType, Integer>();
							for (SkillType skill : SkillType.values()) {
								xp.put(skill, dataStorage.getConfig().getInt("mcmmo.skill." + skill.getSkillName() + ".xp"));
							}

							p3.depositPlayer(sender.getName(), money);

							TokensAPI.getTokens(pname);
							Tokens.getInstance().saveTokens();
							
							for (SkillType skill : SkillType.values()) {
								ExperienceAPI.setLevel((Player) sender, skill.getSkillName(), level.get(skill));
								ExperienceAPI.setXP((Player) sender, skill.getSkillName(), xp.get(skill));
							}

							dataStorage.delete();
							
							sender.sendMessage("Stuff claimed!");
						} else {
							sender.sendMessage("You have no stuff to claim!");
						}
					}
				}
			}

			if (args.length == 2) {
				if (args[1].equalsIgnoreCase("save")) {
					for (OfflinePlayer p : Bukkit.getServer().getOfflinePlayers()) {
						saveData(p.getName());
					}
					
					for(Player p : Bukkit.getOnlinePlayers()) {
						saveData(p.getName());
					}
					
					sender.sendMessage("Data saved!");
				}
			}
		}

		return false;
	}

	public void saveData(String name) {
		PlayerFile dataStorage = new PlayerFile(this, name);

		double money = p3.getBalance(name);
		int tokens = TokensAPI.getTokens(name);

		dataStorage.getConfig().set("vault.money", money);
		dataStorage.getConfig().set("senchant.tokens", tokens);

		for (SkillType skill : SkillType.values()) {
			dataStorage.getConfig().set("mcmmo.skill." + skill.getSkillName() + ".level", ExperienceAPI.getLevel(Bukkit.getPlayer(name), skill.getSkillName()));
			dataStorage.getConfig().set("mcmmo.skill." + skill.getSkillName() + ".xp", ExperienceAPI.getXP(Bukkit.getPlayer(name), skill.getSkillName()));
		}

		dataStorage.save();
	}
}
