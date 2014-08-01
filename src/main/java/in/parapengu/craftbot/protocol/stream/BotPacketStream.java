package in.parapengu.craftbot.protocol.stream;

import in.parapengu.craftbot.bot.CraftBot;
import in.parapengu.craftbot.event.EventManager;
import in.parapengu.craftbot.logging.Logger;
import in.parapengu.craftbot.protocol.Packet;
import in.parapengu.craftbot.protocol.Protocol;
import in.parapengu.craftbot.protocol.State;

import java.net.Socket;
import java.util.Map;

public class BotPacketStream extends PacketStream {

	private Protocol protocol;
	private CraftBot bot;

	public BotPacketStream(Protocol protocol, String address, int port, CraftBot bot) {
		super(protocol.getPackets(), address, port);
		this.protocol = protocol;
		this.bot = bot;
	}

	public Protocol getProtocol() {
		return protocol;
	}

	public CraftBot getBot() {
		return bot;
	}

	@Override
	public State getState() {
		return bot.getState();
	}

	@Override
	public EventManager getManager() {
		return bot.getEventManager();
	}

	@Override
	public Logger getLogger() {
		return bot.getLogger();
	}

	@Override
	public boolean validate(int id) {
		if(id > protocol.getMaxPacketId(getState())) {
			getLogger().debug("Invalid packet #" + id + ": Too high!");
			return false;
		}

		return super.validate(id);
	}

	@Override
	public void disconnect(String reason) {
		super.disconnect(reason);
		bot.getEventManager().unregister(protocol);
	}

}
