package in.parapengu.craftbot.command.commands;

import in.parapengu.craftbot.bot.BotHandler;
import in.parapengu.craftbot.command.CommandContext;
import in.parapengu.craftbot.command.CommandHandler;

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
		return "";
	}

	@Override
	public boolean execute(CommandContext context) {
		BotHandler.getHandler().getLogger().warning("Shutting down CraftBot");
		System.exit(0);
		return true;
	}

}
