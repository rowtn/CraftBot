package in.parapengu.craftbot.command.commands;

import in.parapengu.craftbot.bot.BotHandler;
import in.parapengu.craftbot.command.CommandContext;
import in.parapengu.craftbot.command.CommandHandler;
import in.parapengu.craftbot.logging.Logger;

public class StopCommand extends CommandHandler {

	private String[] aliases;

	public StopCommand() {
		this.aliases = new String[]{"stop"};
	}

	@Override
	public String[] getAliases() {
		return aliases;
	}

	@Override
	public String getDescription() {
		return "Stops the server";
	}

	@Override
	public String getHelp() {
		return null;
	}

	@Override
	public boolean execute(String label, CommandContext context, Logger sender) {
		BotHandler.getHandler().shutdown();
		return true;
	}

}
