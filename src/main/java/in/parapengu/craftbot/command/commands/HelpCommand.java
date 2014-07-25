package in.parapengu.craftbot.command.commands;

import in.parapengu.craftbot.bot.BotHandler;
import in.parapengu.craftbot.command.CommandContext;
import in.parapengu.craftbot.command.CommandHandler;

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
	public boolean execute(CommandContext context) {
		BotHandler.getHandler().getLogger().info("#effort");
		return true;
	}

}
