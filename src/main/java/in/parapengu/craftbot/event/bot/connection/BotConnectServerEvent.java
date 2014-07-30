package in.parapengu.craftbot.event.bot.connection;

import in.parapengu.craftbot.bot.CraftBot;
import in.parapengu.craftbot.event.Cancellable;
import in.parapengu.craftbot.event.bot.BotEvent;

public class BotConnectServerEvent extends BotEvent implements Cancellable {

	private boolean cancelled;
	private String address;
	private int port;

	public BotConnectServerEvent(CraftBot bot, String address, int port) {
		super(bot);
		this.address = address;
		this.port = port;
	}

	public String getAddress() {
		return address;
	}

	public int getPort() {
		return port;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

}
