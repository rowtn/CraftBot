package in.parapengu.craftbot.bot;

import com.google.common.collect.Lists;
import in.parapengu.craftbot.command.CommandContext;
import in.parapengu.craftbot.command.CommandHandler;
import in.parapengu.craftbot.command.commands.StopCommand;
import in.parapengu.craftbot.logging.Logger;
import in.parapengu.craftbot.logging.Logging;
import joptsimple.OptionSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class BotHandler {

	private static BotHandler handler;

	private Logger logger;
	private List<CraftBot> bots;

	private List<CommandHandler> commands;

	public BotHandler(OptionSet options, SimpleDateFormat format) {
		handler = this;
		this.logger = Logging.getLogger().setFormat(format);
		this.bots = new ArrayList<>();
		this.commands = new ArrayList<>();
		register(new StopCommand());
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

		CommandContext context = new CommandContext().setArguments(args);
		handler.run(context);
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

	public static BotHandler getHandler() {
		return handler;
	}

}
