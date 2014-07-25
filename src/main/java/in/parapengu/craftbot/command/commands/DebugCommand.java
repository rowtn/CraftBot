package in.parapengu.craftbot.command.commands;

import in.parapengu.craftbot.bot.BotHandler;
import in.parapengu.craftbot.command.CommandContext;
import in.parapengu.craftbot.command.CommandException;
import in.parapengu.craftbot.command.CommandHandler;
import in.parapengu.craftbot.logging.Logger;
import in.parapengu.craftbot.util.PaginatedResult;

import java.util.ArrayList;
import java.util.List;

public class DebugCommand extends CommandHandler {

	private String[] aliases;

	public DebugCommand() {
		this.aliases = new String[]{"debug"};
	}

	@Override
	public String[] getAliases() {
		return aliases;
	}

	@Override
	public String getDescription() {
		return "Toggles or sets debug mode";
	}

	@Override
	public String getHelp() {
		return "[on/off]";
	}

	@Override
	public boolean execute(CommandContext context) throws CommandException {
		if(context.getArguments().length > 1) {
			return false;
		}

		Logger logger = BotHandler.getHandler().getLogger();
		boolean set = !logger.isDebug();
		String status = set ? "enabled" : "disabled";
		if(context.getArguments().length == 1) {
			boolean update = context.getBoolean(0);
			status = update ? "enabled" : "disabled";
			if(update == !set) {
				logger.info("Debug mode is already " + status);
				return true;
			}

			set = update;
		}

		logger.setDebug(set);
		logger.info("Debug mode has been " + status);
		return true;
	}

}
