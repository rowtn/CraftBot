package in.parapengu.craftbot.plugin;

import in.parapengu.commons.utils.file.TextFile;
import in.parapengu.craftbot.bot.BotHandler;
import in.parapengu.craftbot.logging.Logger;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public abstract class BotPlugin {

	private BotHandler handler;
	private PluginDescription description;
	private Logger logger;

	private File dataFolder;
	private JSONObject config;

	public BotHandler getHandler() {
		return handler;
	}

	public PluginDescription getDescription() {
		return description;
	}

	public Logger getLogger() {
		return logger;
	}

	public boolean readConfig() {
		File file = new File(dataFolder, "config.json");
		if(!file.exists()) {
			logger.debug("config.json does not exist");
			config = new JSONObject();
			return true;
		}

		if(!file.isDirectory()) {
			logger.warning("Could not load config.json because " + file.getPath() + " is a directory");
			return false;
		}

		String string;
		try {
			string = IOUtils.toString(new FileInputStream(file));
		} catch(IOException ex) {
			logger.log("Could not load config.json: ", ex);
			return false;
		}

		try {
			config = new JSONObject(string);
		} catch(JSONException ex) {
			logger.info("config.json is not a valid JSONObject, attempting to parse as JSONArray");
			try {
				config = new JSONObject(string);
			} catch(JSONException ex2) {
				logger.log("config.json is not a valid JSON file: ", ex2);
				return false;
			}
		}

		return true;
	}

	public boolean saveConfig() {
		if(!dataFolder.exists() && !dataFolder.mkdirs()) {
			logger.info("Could not save config.json because " + dataFolder.getPath() + " could not be created");
			return false;
		}

		File file = new File(dataFolder, "config.json");
		if(file.exists() && file.isDirectory()) {
			logger.info("Could not save config.json because " + file.getPath() + " is a directory");
			return false;
		}

		try {
			TextFile save = new TextFile(file);
			save.line(config.toString(2));
			save.save();
		} catch(IOException ex) {
			logger.log("Could not save " + file.getPath() + ": ", ex);
			return false;
		}

		return true;
	}

	public File getDataFolder() {
		return dataFolder;
	}

	public JSONObject getConfig() {
		return config;
	}

	public void onLoad() {

	}

	public abstract void onEnable();

	public abstract void onDisable();

}
