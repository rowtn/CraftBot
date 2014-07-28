package in.parapengu.craftbot.command.commands;

import in.parapengu.craftbot.bot.BotHandler;
import in.parapengu.craftbot.bot.ChatColor;
import in.parapengu.craftbot.command.CommandContext;
import in.parapengu.craftbot.command.CommandException;
import in.parapengu.craftbot.command.CommandHandler;
import in.parapengu.craftbot.logging.Logger;
import in.parapengu.craftbot.plugin.BotPlugin;

import java.util.ArrayList;
import java.util.List;

public class PluginsCommand extends CommandHandler {

	private String[] aliases;

	public PluginsCommand() {
		this.aliases = new String[]{"plugins", "pl"};
	}

	@Override
	public String[] getAliases() {
		return aliases;
	}

	@Override
	public String getDescription() {
		return "Lists all the plugins";
	}

	@Override
	public String getHelp() {
		return "[search]";
	}

	@Override
	public boolean execute(String label, CommandContext context, Logger sender) throws CommandException {
		String search = null;
		if(context.getArguments().length > 0) {
			search = context.getJoinedStrings(0);
		}

		final String fSearch = search;
		List<BotPlugin> plugins = new ArrayList<>();
		for(BotPlugin plugin : BotHandler.getHandler().getPlugins()) {
			if(fSearch == null || plugin.getDescription().getName().toLowerCase().startsWith(fSearch)) {
				plugins.add(plugin);
			}
		}

		StringBuilder builder = new StringBuilder("Plugins (" + plugins.size() + "): ");
		for(int i = 0; i < plugins.size(); i++) {
			if(i != 0) {
				builder.append(", ");
			}

			BotPlugin plugin = plugins.get(i);
			String append = (plugin.isEnabled() ? ChatColor.GREEN : ChatColor.RED) + plugin.getDescription().getName() + ChatColor.RESET;
			builder.append(append);
		}

		sender.info(builder.toString());
		return true;
	}

}
