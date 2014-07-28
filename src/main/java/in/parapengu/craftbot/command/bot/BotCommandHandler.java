package in.parapengu.craftbot.command.bot;

import in.parapengu.craftbot.bot.CraftBot;
import in.parapengu.craftbot.command.CommandContext;
import in.parapengu.craftbot.command.CommandException;
import in.parapengu.craftbot.command.SubCommandHandler;
import in.parapengu.craftbot.logging.Logger;

import java.util.ArrayList;
import java.util.List;

public abstract class BotCommandHandler {

	public List<SubCommandHandler> subCommands = new ArrayList<>();

	public abstract String[] getAliases();

	public abstract String getDescription();

	public abstract String getHelp();

	public abstract boolean execute(String label, CommandContext context, Logger sender, CraftBot bot) throws CommandException;

	public void run(String label, CommandContext context, Logger sender, CraftBot bot) {
		if(subCommands.size() > 0) {
			// magic
		}
	}

}
