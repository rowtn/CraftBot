package in.parapengu.craftbot.logging;

import in.parapengu.craftbot.bot.ChatColor;

public enum LogLevel {

	DEBUG(ChatColor.WHITE),
	INFO(ChatColor.AQUA),
	WARNING(ChatColor.GOLD),
	SEVERE(ChatColor.RED);

	private ChatColor color;

	LogLevel(ChatColor color) {
		this.color = color;
	}

	public ChatColor getColor() {
		return color;
	}

}
