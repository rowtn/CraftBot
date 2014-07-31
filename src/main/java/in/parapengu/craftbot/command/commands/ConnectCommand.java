package in.parapengu.craftbot.command.commands;

import in.parapengu.commons.utils.OtherUtil;
import in.parapengu.craftbot.bot.BotHandler;
import in.parapengu.craftbot.bot.CraftBot;
import in.parapengu.craftbot.command.CommandContext;
import in.parapengu.craftbot.command.CommandException;
import in.parapengu.craftbot.command.CommandHandler;
import in.parapengu.craftbot.logging.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ConnectCommand extends CommandHandler {

	private String[] aliases;

	public ConnectCommand() {
		this.aliases = new String[]{"connect"};
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
		if(context.getArguments().length < 1) {
			return false;
		}

		BotHandler handler = BotHandler.getHandler();
		List<CraftBot> bots = new ArrayList<>();
		if(context.getArguments().length == 1) {
			bots.addAll(handler.getBots().values());
		} else {
			List<String> args = new ArrayList<>();
			for(String arg : context.getArguments()) {
				args.add(arg);
			}
			args = args.subList(1, context.getArguments().length);
			sender.info(args);

			for(String name : args) {
				List<CraftBot> list = handler.getBots(name);
				for(CraftBot bot : list) {
					if(bot != null && !bots.contains(bot)) {
						bots.add(bot);
					}
				}
			}
		}

		if(bots.size() < 1) {
			throw new CommandException("No bots were specified");
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

		List<String> names = bots.stream().map(CraftBot::getUsername).collect(Collectors.toList());
		String connecting = "Connecting " + OtherUtil.listToEnglishCompound(names) + " to " + address + (port != 25565 ? ":" + port : "");
		sender.info(connecting);

		for(CraftBot bot : bots) {
			bot.connect(address, port);
		}
		return true;
	}

}
