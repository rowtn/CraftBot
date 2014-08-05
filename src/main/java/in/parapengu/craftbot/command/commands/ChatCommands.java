package in.parapengu.craftbot.command.commands;

import in.parapengu.craftbot.bot.BotHandler;
import in.parapengu.craftbot.bot.CraftBot;
import in.parapengu.craftbot.command.CommandContext;
import in.parapengu.craftbot.command.CommandException;
import in.parapengu.craftbot.command.CommandHandler;
import in.parapengu.craftbot.logging.Logger;

import java.util.List;

/**
 * Created by August on 8/4/14.
 */
public class ChatCommands {

	private ChatCommands() {}

	public static class LocalChat extends CommandHandler {

		@Override
		public String[] getAliases() {
			return new String[]{"say", "chat", "localchat"};
		}

		@Override
		public String getDescription() {
			return "Send a message through the specified bot";
		}

		@Override
		public String getHelp() {
			return "<username> <message>";
		}

		@Override
		public boolean execute(String label, CommandContext context, Logger sender) throws CommandException {
			if(context.getArguments().length < 2) throw new CommandException("Too few arguments.");

			String username = context.getArguments()[0];
			List<CraftBot> bots = BotHandler.getHandler().getBots(username);
			if(bots.isEmpty()) throw new CommandException("Bot not found: " + username);
			if(bots.get(0).getPacketStream() == null) throw new CommandException("Bot is not connected to server.");
			bots.get(0).sendMessage(context.getJoinedStrings(1));

			return true;
		}
	}

	public static class GlobalChat extends CommandHandler {

		@Override
		public String[] getAliases() {
			return new String[]{"g", "gchat", "globalchat"};
		}

		@Override
		public String getDescription() {
			return "Send a message through all bots";
		}

		@Override
		public String getHelp() {
			return "<message>";
		}

		@Override
		public boolean execute(String label, CommandContext context, Logger sender) throws CommandException {
			if(context.getArguments().length < 1) throw new CommandException("Too few arguments.");

			String message = context.getJoinedStrings(0);
			for(CraftBot bot : BotHandler.getHandler().getBots().values()) {
				if(bot.getOutput() == null) continue;
				bot.sendMessage(message);
			}
			return true;
		}
	}

}
