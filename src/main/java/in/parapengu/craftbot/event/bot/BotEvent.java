package in.parapengu.craftbot.event.bot;

import in.parapengu.craftbot.bot.CraftBot;
import in.parapengu.craftbot.event.Event;

public class BotEvent extends Event {

	private CraftBot bot;

	public BotEvent(CraftBot bot) {
		this.bot = bot;
	}

	public CraftBot getBot() {
		return bot;
	}

}
