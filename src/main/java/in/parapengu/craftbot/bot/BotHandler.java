package in.parapengu.craftbot.bot;

import com.google.common.base.Charsets;
import in.parapengu.commons.reflection.SimpleObject;
import in.parapengu.commons.utils.file.TextFile;
import in.parapengu.craftbot.command.CommandContext;
import in.parapengu.craftbot.command.CommandHandler;
import in.parapengu.craftbot.command.commands.*;
import in.parapengu.craftbot.event.EventManager;
import in.parapengu.craftbot.logging.Logger;
import in.parapengu.craftbot.logging.Logging;
import in.parapengu.craftbot.plugin.BotPlugin;
import in.parapengu.craftbot.plugin.ClassPathLoader;
import in.parapengu.craftbot.plugin.PluginDescription;
import joptsimple.OptionSet;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

public class BotHandler {

	private static BotHandler handler;

	private Logger logger;
	private Map<String, CraftBot> bots;

	private int loaded = 0;
	private List<CommandHandler> commands;

	private File pluginsFolder;
	private List<BotPlugin> plugins;
	private EventManager manager;

	public BotHandler(OptionSet options, SimpleDateFormat format) {
		handler = this;
		logger = Logging.getLogger().setFormat(format);
		logger.setDebug((boolean) options.valueOf("debug"));
		bots = new HashMap<>();
		commands = new ArrayList<>();
		pluginsFolder = (File) options.valueOf("p");
		plugins = new ArrayList<>();
		manager = new EventManager();

		register(new ConnectCommand());
		register(new DebugCommand());
		register(new HelpCommand());
		register(new PingCommand());
		register(new PluginsCommand());
		register(new ReloadCommand());
		register(new StopCommand());
		register(new ChatCommands.LocalChat());
		register(new ChatCommands.GlobalChat());

		long start = System.currentTimeMillis();
		reload(false);

		File accountsFile = (File) options.valueOf("a");
		if(!accountsFile.exists()) {
			JSONArray array = new JSONArray();
			JSONObject account = new JSONObject();
			account.put("password", "password");
			account.put("username", "Steve");
			array.put(account);

			try {
				TextFile file = new TextFile(accountsFile);
				file.line(array.toString(2));
				file.save();
				logger.info("Created a new " + accountsFile.getPath() + " - this needs to be updated.");
			} catch(IOException ex) {
				logger.log("Could not create a new " + accountsFile.getPath() + ": ", ex);
			}

			shutdown();
			return;
		}

		boolean offline = (boolean) options.valueOf("o");
		if(!offline || !options.has("b")) {
			List<String> lines;
			try {
				lines = Files.readAllLines(accountsFile.toPath(), Charsets.UTF_8);
			} catch(IOException ex) {
				throw new IllegalArgumentException("Could not read accounts from " + accountsFile.getPath());
			}

			StringBuilder builder = new StringBuilder();
			lines.forEach(builder::append);

			JSONArray accounts = new JSONArray(builder.toString());
			for(int i = 0; i < accounts.length(); i++) {
				final JSONObject object = accounts.getJSONObject(i);
				/*
				new Thread(() -> {
					String username;
					String password;
					try {
						username = object.getString("username");
						password = object.getString("password");
					} catch(JSONException ex) {
						logger.info("Error while parsing " + accountsFile.toPath() + ": " + ex.getMessage());
						loaded++;
						return;
					}

					register(username, password);
					loaded++;
				}).start();
				*/
				String username;
				String password;
				try {
					username = object.getString("username");
					password = object.getString("password");
				} catch(JSONException ex) {
					logger.info("Error while parsing " + accountsFile.toPath() + ": " + ex.getMessage());
					loaded++;
					return;
				}

				register(username, password, !offline);
				loaded++;
			}

			while(accounts.length() > loaded) {
				logger.debug("Loaded " + loaded + " out of " + accounts.length() + " bots");
			}
		} else if(options.has("b")) {
			int count = (int) options.valueOf("b");
			for(int i = 1; i <= count; i++) {
				register("bot" + i, null, false);
			}
		}

		logger.info("Loaded " + bots.size() + " account" + (bots.size() != 0 ? "s" : ""));

		for(BotPlugin plugin : plugins) {
			plugin.setEnabled(true);
		}

		long finish = System.currentTimeMillis();
		double seconds = (double) (finish - start) / 1000;
		logger.info("Done (" + seconds + "s)! For help, type \"help\" or \"?\"");
	}

	public static BotHandler getHandler() {
		return handler;
	}

	public Logger getLogger() {
		return logger;
	}

	public Map<String, CraftBot> getBots() {
		return bots;
	}

	public List<CraftBot> getBots(String string) {
		final String search = string.toLowerCase();
		List<CraftBot> results = new ArrayList<>();
		results.addAll(bots.values().stream().filter(bot -> bot.getUsername().equalsIgnoreCase(search)).collect(Collectors.toList()));
		if(results.size() > 0) {
			return results;
		}

		results.addAll(bots.values().stream().filter(bot -> bot.getUUID().equalsIgnoreCase(search)).collect(Collectors.toList()));
		if(results.size() > 0) {
			return results;
		}

		results.addAll(bots.values().stream().filter(bot -> bot.getUsername().toLowerCase().startsWith(search)).collect(Collectors.toList()));
		if(results.size() > 0) {
			return results;
		}

		results.addAll(bots.values().stream().filter(bot -> bot.getUUID().toLowerCase().startsWith(search)).collect(Collectors.toList()));
		return results;
	}

	public void register(String username, String password, boolean authenticate) {
		CraftBot bot;
		try {
			bot = new CraftBot(username, password, authenticate);
		} catch(Exception ex) {
			logger.log("Error while authenticating " + username + ": ", ex);
			return;
		}

		String identifier = authenticate ? bot.getUUID() : bot.getUsername();
		if(bots.get(identifier) != null) {
			if(authenticate) {
				logger.warning(bot.getUsername() + " (" + bot.getUUID() + ") is already a registered bot");
			} else {
				logger.warning(bot.getUsername() + " is already a registered bot");
			}
			return;
		}

		bots.put(identifier, bot);
	}

	public void command(String[] params) {
		String label = params[0];
		String[] args = new String[params.length - 1];
		for(int i = 1; i < params.length; i++) {
			args[i - 1] = params[i];
		}

		// logger.info("Handled command '" + label + "' (args: " + Lists.newArrayList(args) + ")");
		CommandHandler handler = getHandler(label);
		if(handler == null) {
			logger.info("Unknown command. Use \"help\" for help.");
			return;
		}

		CommandContext context = new CommandContext(label).setArguments(args);
		handler.run(label, context, logger);
	}

	public List<CommandHandler> getCommands() {
		return commands;
	}

	public CommandHandler getHandler(String alias) {
		alias = alias.toLowerCase();

		for(CommandHandler handler : commands) {
			List<String> aliases = new ArrayList<>();
			for(String string : handler.getAliases()) {
				aliases.add(string.toLowerCase());
			}

			if(aliases.contains(alias)) {
				return handler;
			}
		}

		return null;
	}

	public void register(CommandHandler handler) {
		for(String alias : handler.getAliases()) {
			if(getHandler(alias) != null) {
				throw new IllegalArgumentException("'" + alias + "' is already registered");
			}
		}

		commands.add(handler);
	}

	public void unregister(String alias) {
		CommandHandler handler = getHandler(alias);
		if(handler != null) {
			commands.remove(handler);
		}
	}

	public File getPluginsFolder() {
		return pluginsFolder;
	}

	public List<BotPlugin> getPlugins() {
		return plugins;
	}

	public BotPlugin getPlugin(Class<? extends BotPlugin> clazz) {
		for(BotPlugin plugin : plugins) {
			if(plugin.getClass().equals(clazz)) {
				return plugin;
			}
		}

		return null;
	}

	public BotPlugin getPlugin(String name) {
		return getPlugin(name, true);
	}

	public BotPlugin getPlugin(String name, boolean sensitive) {
		for(BotPlugin plugin : plugins) {
			String pluginName = plugin.getDescription().getName();
			if(!sensitive) {
				pluginName = pluginName.toLowerCase();
				name = name.toLowerCase();
			}

			if(pluginName.equals(name)) {
				return plugin;
			}
		}

		return null;
	}
	
	public BotPlugin load(File file) {
		JarFile jar;
		try {
			jar = new JarFile(file, true);
		} catch(IOException ex) {
			logger.log("Could not load plugin at " + file.getPath() + ": ", ex);
			return null;
		}

		ZipEntry entry = jar.getEntry("plugin.json");
		String string;
		try {
			InputStream stream = jar.getInputStream(entry);
			string = IOUtils.toString(stream);
		} catch(IOException ex) {
			logger.log("Could not load plugin at " + file.getPath() + ": ", ex);
			return null;
		}

		JSONObject manifest;
		try {
			manifest = new JSONObject(string);
		} catch(JSONException ex) {
			logger.log("Could not load " + file.getName() + " because it's plugin.json was not valid: ", ex);
			return null;
		}

		String name;
		try {
			name = manifest.getString("name");
			if(name == null) {
				throw new NullPointerException("name");
			}
		} catch(Exception ex) {
			logger.warning("Could not load " + file.getName() + " because it's plugin.json does not contain a valid name");
			return null;
		}

		String version;
		try {
			version = manifest.getString("version");
			if(version == null) {
				throw new NullPointerException("version");
			}
		} catch(Exception ex) {
			logger.warning("Could not load " + name + " because it's plugin.json does not contain a valid version");
			return null;
		}

		String main;
		try {
			main = manifest.getString("main");
			if(main == null) {
				throw new NullPointerException("main");
			}
		} catch(Exception ex) {
			logger.warning("Could not load " + name + " because it's plugin.json does not contain a valid main class");
			return null;
		}

		List<String> authors = new ArrayList<>();
		if(manifest.has("authors")) {
			try {
				JSONArray array = manifest.getJSONArray("authors");
				for(int i = 0; i < array.length(); i++) {
					String author = array.getString(i);
					if(author != null) {
						authors.add(author);
					}
				}
			} catch(Exception ex) {
				logger.warning("Ignoring the list of authors from " + name + " because the JSONArray was invalid");
			}
		}

		URLClassLoader loader;
		try {
			loader = ClassPathLoader.addFile(file);
		} catch(IOException ex) {
			logger.info("Invalid plugin.json for " + name + ": Could not load " + file.getName() + " into the classpath");
			return null;
		}

		Class<?> plugin;
		try {
			plugin = Class.forName(main);
		} catch(ClassNotFoundException ex) {
			logger.info("Invalid plugin.json for " + name + ": " + main + " does not exist");
			return null;
		}

		if(!BotPlugin.class.isAssignableFrom(plugin)) {
			logger.info("Invalid plugin.json for " + name + ": " + main + " is not assignable from " + BotPlugin.class.getSimpleName());
			return null;
		}

		PluginDescription description = new PluginDescription(name, version, authors);
		try {
			BotPlugin loaded = load((Class<BotPlugin>) plugin, description, loader, file);
			plugins.add(loaded);
			return loaded;
		} catch(Exception ex) {
			logger.log("Failed to load " + name + ": ", ex);
		}

		return null;
	}

	public BotPlugin load(Class<BotPlugin> clazz, PluginDescription description, URLClassLoader loader, File file) throws Exception {
		BotPlugin plugin = clazz.newInstance();

		SimpleObject object = new SimpleObject(plugin, BotPlugin.class);
		object.field("handler").set(this);
		object.field("loader").set(loader);
		object.field("file").set(file);
		object.field("description").set(description);
		object.field("logger").set(Logging.getLogger(description.getName(), handler.getLogger()));
		object.field("dataFolder").set(new File(handler.getPluginsFolder(), description.getName()));
		object.field("config").set(new JSONObject());
		plugin.readConfig();

		plugin.getLogger().info("Loaded " + description.getName() + " v" + description.getVersion());
		return plugin;
	}

	public boolean unload(BotPlugin plugin) {
		plugins.remove(plugin);
		plugin.setEnabled(false);
		try {
			((URLClassLoader) new SimpleObject(plugin, BotPlugin.class).field("loader").value()).close();
			return true;
		} catch(Exception ex) {
			logger.log("Could not unload " + plugin.getDescription().getName() + ": ", ex);
			return false;
		}
	}

	public void reload(BotPlugin plugin) {
		File file = (File) new SimpleObject(plugin, BotPlugin.class).field("file").value();
		// unload(plugin);
		plugin.setEnabled(false);
		plugin = load(file);
		if(plugin != null) {
			plugin.setEnabled(true);
		}
	}

	public void reload(boolean enable) {
		for(BotPlugin plugin : plugins) {
			plugin.setEnabled(false);
		}

		plugins = new ArrayList<>();

		if(!pluginsFolder.exists()) {
			if(pluginsFolder.mkdirs()) {
				logger.info("Plugins folder did not exist, created one at " + pluginsFolder.getPath());
			} else {
				logger.info("Failed to create plugins folder at " + pluginsFolder.getPath());
				shutdown();
				return;
			}
		}

		if(!pluginsFolder.isDirectory()) {
			logger.info("The plugins folder, " + pluginsFolder.getPath() + " is a directory");
			shutdown();
			return;
		}

		for(File file : pluginsFolder.listFiles()) {
			if(file.getName().toLowerCase().endsWith(".jar")) {
				load(file);
			}
		}

		if(enable) {
			for(BotPlugin plugin : plugins) {
				plugin.setEnabled(true);
			}
		}
	}

	public EventManager getEventManager() {
		return manager;
	}

	public void shutdown() {
		for(BotPlugin plugin : plugins) {
			plugin.setEnabled(false);
		}

		logger.warning("Shutting down CraftBot");
		System.exit(0);
	}

}
