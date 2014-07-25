package in.parapengu.craftbot.bot;

import com.google.common.base.Charsets;
import in.parapengu.commons.utils.file.TextFile;
import in.parapengu.craftbot.command.CommandContext;
import in.parapengu.craftbot.command.CommandHandler;
import in.parapengu.craftbot.command.commands.DebugCommand;
import in.parapengu.craftbot.command.commands.HelpCommand;
import in.parapengu.craftbot.command.commands.PingCommand;
import in.parapengu.craftbot.command.commands.StopCommand;
import in.parapengu.craftbot.logging.Logger;
import in.parapengu.craftbot.logging.Logging;
import joptsimple.OptionSet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BotHandler {

	private static BotHandler handler;

	private Logger logger;
	private Map<String, CraftBot> bots;

	private List<CommandHandler> commands;

	public BotHandler(OptionSet options, SimpleDateFormat format) {
		handler = this;
		this.logger = Logging.getLogger().setFormat(format);
		this.bots = new HashMap<>();
		this.commands = new ArrayList<>();
		register(new StopCommand());
		register(new PingCommand());
		register(new HelpCommand());
		register(new DebugCommand());

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
				logger.info("Created a new " + accountsFile.toPath() + " - this needs to be updated.");
			} catch(IOException ex) {
				ex.printStackTrace();
			}

			shutdown();
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

	public void shutdown() {
		logger.warning("Shutting down CraftBot");
		System.exit(0);
	}

	public static BotHandler getHandler() {
		return handler;
	}

}
