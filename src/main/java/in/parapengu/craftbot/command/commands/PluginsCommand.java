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
	public boolean execute(String label, CommandContext context, Logger sender) throws CommandException {
		String search = null;
		if(context.getArguments().length > 1) {
			StringBuilder builder = new StringBuilder();
			for(int i = 0; i < context.getArguments().length; i++) {
				if(i != 0) {
					builder.append(" ");
				}

				builder.append(context.getArguments()[i]);
			}
			search = builder.toString();
		}

		final String fSearch = search;
		List<BotPlugin> plugins = new ArrayList<>(BotHandler.getHandler().getPlugins());
		BotHandler.getHandler().getPlugins().stream().filter(plugin -> fSearch != null && !plugin.getDescription().getName().toLowerCase().startsWith(fSearch)).forEach(plugins::remove);

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
