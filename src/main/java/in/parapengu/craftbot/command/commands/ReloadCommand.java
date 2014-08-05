package in.parapengu.craftbot.command.commands;

import in.parapengu.craftbot.bot.BotHandler;
import in.parapengu.craftbot.command.CommandContext;
import in.parapengu.craftbot.command.CommandException;
import in.parapengu.craftbot.command.CommandHandler;
import in.parapengu.craftbot.logging.Logger;
import in.parapengu.craftbot.plugin.BotPlugin;

public class ReloadCommand extends CommandHandler {

	private String[] aliases;

	public ReloadCommand() {
		this.aliases = new String[]{"reload"};
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
	public boolean execute(String label, CommandContext context, Logger sender) throws CommandException {
		BotPlugin plugin = null;
		if(context.getArguments().length > 0) {
			String name = context.getJoinedStrings(0);
			plugin = BotHandler.getHandler().getPlugin(name, false);
			if(plugin == null) {
				throw new CommandException("\"" + name + "\" does not exist or is not loaded");
			}
		}

		BotHandler handler = BotHandler.getHandler();
		if(plugin != null) {
			handler.reload(plugin);
		} else {
			handler.reload(true);
		}
		return true;
	}

}
