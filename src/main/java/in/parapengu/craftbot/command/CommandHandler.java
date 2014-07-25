package in.parapengu.craftbot.command;

import in.parapengu.craftbot.bot.BotHandler;

import java.util.ArrayList;
import java.util.List;

public abstract class CommandHandler {

	public List<SubCommandHandler> subCommands = new ArrayList<>();

	public abstract String[] getAliases();

	public abstract String getDescription();

	public abstract String getHelp();

	public abstract boolean execute(CommandContext context) throws CommandException;

	public void run(CommandContext context) {
		if(subCommands.size() > 0) {
			// magic
		}

		try {
			boolean success = execute(context);
			if(!success) {
				BotHandler.getHandler().getLogger().warning("Incorrect command usage:");
				BotHandler.getHandler().getLogger().warning(context.getLabel() + " " + getHelp());
			}
		} catch(CommandException ex) {
			BotHandler.getHandler().getLogger().warning(ex.getMessage());
		} catch(NumberFormatException ex) {
			BotHandler.getHandler().getLogger().warning("Number expected, string supplied...");
		}
	}



}
