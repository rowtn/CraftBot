package in.parapengu.craftbot.command.commands;

import in.parapengu.craftbot.bot.BotHandler;
import in.parapengu.craftbot.command.CommandContext;
import in.parapengu.craftbot.command.CommandException;
import in.parapengu.craftbot.command.CommandHandler;
import in.parapengu.craftbot.logging.Logger;
import in.parapengu.craftbot.util.PaginatedResult;

import java.util.ArrayList;
import java.util.List;

public class HelpCommand extends CommandHandler {

	private String[] aliases;

	public HelpCommand() {
		this.aliases = new String[]{"help", "?"};
	}

	@Override
	public String[] getAliases() {
		return aliases;
	}

	@Override
	public String getDescription() {
		return "Displays this help message";
	}

	@Override
	public String getHelp() {
		return "[page]";
	}

	@Override
	public boolean execute(CommandContext context) throws CommandException {
		if(context.getArguments().length > 1) {
			return false;
		}

		Logger logger = BotHandler.getHandler().getLogger();
		List<String> rows = new ArrayList<>();
		List<CommandHandler> commands = BotHandler.getHandler().getCommands();
		for(CommandHandler handler : commands) {
			String help = handler.getHelp();
			String message = handler.getAliases()[0] + (help != null ? " " + help : "") + " - " + handler.getDescription();
			rows.add(message);
		}

		PaginatedResult result = new PaginatedResult("=========[ [page] of [pages] ]=========", rows, 10, false);
		int page = 1;
		if(context.getArguments().length == 1) {
			page = context.getInteger(0);
		}

		result.display(logger, page);
		return true;
	}

}
