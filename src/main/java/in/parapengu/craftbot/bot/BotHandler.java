package in.parapengu.craftbot.bot;

import com.google.common.base.Charsets;
import in.parapengu.commons.reflection.SimpleObject;
import in.parapengu.commons.utils.file.TextFile;
import in.parapengu.craftbot.command.CommandContext;
import in.parapengu.craftbot.command.CommandHandler;
import in.parapengu.craftbot.command.commands.DebugCommand;
import in.parapengu.craftbot.command.commands.HelpCommand;
import in.parapengu.craftbot.command.commands.PingCommand;
import in.parapengu.craftbot.command.commands.StopCommand;
import in.parapengu.craftbot.logging.Logger;
import in.parapengu.craftbot.logging.Logging;
import in.parapengu.craftbot.plugin.BotPlugin;
import in.parapengu.craftbot.plugin.PluginDescription;
import joptsimple.OptionSet;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class BotHandler {

	private static BotHandler handler;

	private Logger logger;
	private Map<String, CraftBot> bots;

	private List<CommandHandler> commands;

	private File pluginsFolder;
	private List<BotPlugin> plugins;

	public BotHandler(OptionSet options, SimpleDateFormat format) {
		handler = this;
		plugins = new ArrayList<>();
		this.logger = Logging.getLogger().setFormat(format);
		this.bots = new HashMap<>();
		this.commands = new ArrayList<>();
		register(new StopCommand());
		register(new PingCommand());
		register(new HelpCommand());
		register(new DebugCommand());

		pluginsFolder = (File) options.valueOf("p");
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
				JarFile jar;
				try {
					jar = new JarFile(file, true);
				} catch(IOException ex) {
					logger.log("Could not load plugin at " + file.getPath() + ": ", ex);
					continue;
				}

				ZipEntry entry = jar.getEntry("plugin.json");
				String string;
				try {
					InputStream stream = jar.getInputStream(entry);
					string = IOUtils.toString(stream);
				} catch(IOException ex) {
					logger.log("Could not load plugin at " + file.getPath() + ": ", ex);
					continue;
				}

				JSONObject manifest;
				try {
					manifest = new JSONObject(string);
				} catch(JSONException ex) {
					logger.log("Could not load " + file.getName() + " because it's plugin.json was not valid: ", ex);
					continue;
				}

				String name;
				try {
					name = manifest.getString("name");
					if(name == null) {
						throw new NullPointerException("name");
					}
				} catch(Exception ex) {
					logger.warning("Could not load " + file.getName() + " because it's plugin.json does not contain a valid name");
					continue;
				}

				String version;
				try {
					version = manifest.getString("version");
					if(version == null) {
						throw new NullPointerException("version");
					}
				} catch(Exception ex) {
					logger.warning("Could not load " + name + " because it's plugin.json does not contain a valid version");
					continue;
				}

				String main;
				try {
					main = manifest.getString("main");
					if(main == null) {
						throw new NullPointerException("main");
					}
				} catch(Exception ex) {
					logger.warning("Could not load " + name + " because it's plugin.json does not contain a valid main class");
					continue;
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

				Class<?> plugin;
				try {
					plugin = Class.forName(main);
				} catch(ClassNotFoundException ex) {
					logger.info("Invalid plugin.json for " + name);
					continue;
				}

				if(!BotPlugin.class.isAssignableFrom(plugin)) {
					logger.info("Invalid plugin.json for " + name);
					continue;
				}

				PluginDescription description = new PluginDescription(name, version, authors);
				try {
					BotPlugin loaded = load((Class<BotPlugin>) plugin, description);
					plugins.add(loaded);
				} catch(Exception ex) {
					logger.log("Failed to load " + name + ": ", ex);
				}
			}
		}

		File accountsFile = (File) options.valueOf("a");
		if(!accountsFile.exists()) {
			JSONArray array = new JSONArray();
			JSONObject account = new JSONObject();
			account.put("username", "Steve");
			account.put("password", "password");
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
			JSONObject object = accounts.getJSONObject(i);
			String username;
			String password;
			try {
				username = object.getString("username");
				password = object.getString("password");
			} catch(JSONException ex) {
				logger.info("Error while parsing " + accountsFile.toPath() + ": " + ex.getMessage());
				continue;
			}

			CraftBot bot;
			try {
				bot = new CraftBot(username, password);
			} catch(Exception ex) {
				logger.log("Error while authenticating " + username + ": ", ex);
				continue;
			}

			String uuid = bot.getUUID();
			if(bots.get(uuid) != null) {
				logger.warning(bot.getUsername() + " (" + uuid + ") is already a registered bot");
				continue;
			}

			bots.put(uuid, bot);
		}

		logger.info("Loaded " + bots.size() + " account" + (bots.size() != 0 ? "s" : ""));

		for(BotPlugin plugin : plugins) {
			plugin.getLogger().info("Enabling " + plugin.getDescription().getName() + " v" + plugin.getDescription().getVersion());
			try {
				plugin.onEnable();
			} catch(Exception ex) {
				ex.printStackTrace();
				logger.warning("An error occurred while enabling " + plugin.getDescription().getName() + ", is it up to date?");
			}
			plugin.getLogger().info(plugin.getDescription().getName() + " v" + plugin.getDescription().getVersion() + " has been enabled");
		}
	}

	public Logger getLogger() {
		return logger;
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
		handler.run(context);
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
		for(BotPlugin plugin : plugins) {
			if(plugin.getDescription().getName().equals(name)) {
				return plugin;
			}
		}

		return null;
	}

	public BotPlugin load(Class<BotPlugin> clazz, PluginDescription description) throws Exception {
		BotPlugin plugin = clazz.newInstance();

		SimpleObject object = new SimpleObject(plugin, BotPlugin.class);
		object.field("handler").set(this);
		object.field("description").set(description);
		object.field("logger").set(Logging.getLogger(description.getName(), handler.getLogger()));
		object.field("dataFolder").set(new File(handler.getPluginsFolder(), description.getName()));
		object.field("config").set(new JSONObject());
		plugin.readConfig();

		plugin.getLogger().info("Loaded " + description.getName() + " v" + description.getVersion());
		return plugin;
	}

	public void shutdown() {
		for(BotPlugin plugin : plugins) {
			plugin.getLogger().info("Disabling " + plugin.getDescription().getName() + " v" + plugin.getDescription().getVersion());
			try {
				plugin.onEnable();
			} catch(Exception ex) {
				ex.printStackTrace();
			}
			plugin.getLogger().info(plugin.getDescription().getName() + " v" + plugin.getDescription().getVersion() + " has been disabled");
		}
		logger.warning("Shutting down CraftBot");
		System.exit(0);
	}

	public static BotHandler getHandler() {
		return handler;
	}

}
