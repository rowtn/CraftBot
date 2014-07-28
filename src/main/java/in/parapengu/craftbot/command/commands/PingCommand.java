package in.parapengu.craftbot.command.commands;

import in.parapengu.craftbot.bot.BotHandler;
import in.parapengu.craftbot.command.CommandContext;
import in.parapengu.craftbot.command.CommandException;
import in.parapengu.craftbot.command.CommandHandler;
import in.parapengu.craftbot.logging.Logger;
import in.parapengu.craftbot.server.ServerPinger;

import java.util.Map;

public class PingCommand extends CommandHandler {

	private String[] aliases;

	public PingCommand() {
		this.aliases = new String[]{"ping"};
	}

	@Override
	public String[] getAliases() {
		return aliases;
	}

	@Override
	public String getDescription() {
		return "Pings the supplied server";
	}

	@Override
	public String getHelp() {
		return "<address>[:port]";
	}

	@Override
	public boolean execute(String label, CommandContext context, Logger sender) throws CommandException {
		if(context.getArguments().length != 1) {
			return false;
		}

		String address = context.getArguments()[0];
		int port = 25565;
		if(address.contains(":")) {
			String[] split = address.split(":");
			if(split.length > 2) {
				throw new CommandException(address + " is not a valid server address!");
			}

			address = split[0];
			port = Integer.parseInt(split[1]);
		}

		Map<String, String> result = ServerPinger.ping(address, port);
		if(result == null) {
			throw new CommandException("Could not connect to " + address + ":" + port);
		}

		for(String key : result.keySet()) {
			String value = result.get(key);
			sender.info(key + ": " + value);
		}
		return true;
	}

}
