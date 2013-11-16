package main.java.me.avastprods.donorreclaim.io;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import main.java.me.avastprods.donorreclaim.Main;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class PlayerFile {

	private FileConfiguration playerConfig = null;
	private File playerConfigFile = null;

	Main clazz;
	String playerName;

	public PlayerFile(Main instance, String playerName) {
		clazz = instance;
		this.playerName = playerName;
	}
	
	public void create() {
		reload();
	}
	
	public void delete() {
		playerConfigFile.delete();
	}
	
	public boolean exists() {
		return new File(clazz.getDataFolder() + playerName + ".yml").exists();
	}

	public void reload() {
		if (playerConfigFile == null) {
			playerConfigFile = new File(clazz.getDataFolder(), playerName + ".yml");
		}

		playerConfig = YamlConfiguration.loadConfiguration(playerConfigFile);
		save();
	}

	public FileConfiguration getConfig() {
		if (playerConfig == null) {
			reload();
		}

		return playerConfig;
	}

	public void save() {
		if (playerConfig == null || playerConfigFile == null) {
			return;
		}

		try {
			getConfig().save(playerConfigFile);
		} catch (IOException ex) {
			clazz.getLogger().log(Level.SEVERE, "Could not save config to " + playerConfigFile, ex);
		}
	}
}
