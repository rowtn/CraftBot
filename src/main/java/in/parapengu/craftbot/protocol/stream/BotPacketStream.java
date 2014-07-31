package in.parapengu.craftbot.protocol.stream;

import in.parapengu.craftbot.bot.CraftBot;
import in.parapengu.craftbot.event.EventManager;
import in.parapengu.craftbot.logging.Logger;
import in.parapengu.craftbot.protocol.Packet;
import in.parapengu.craftbot.protocol.State;

import java.net.Socket;
import java.util.Map;

public class BotPacketStream extends PacketStream {

	private CraftBot bot;

	public BotPacketStream(Map<State, Map<Integer, Class<? extends Packet>>> packets, Socket socket, PacketOutputStream output, PacketInputStream input, CraftBot bot) {
		super(packets, socket, output, input);
		this.bot = bot;
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
	public BotPacketStream start() {
		super.start();
		return this;
	}

}
