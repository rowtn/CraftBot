package in.parapengu.craftbot.protocol.stream;

import in.parapengu.craftbot.bot.BotHandler;
import in.parapengu.craftbot.event.EventManager;
import in.parapengu.craftbot.logging.Logger;
import in.parapengu.craftbot.protocol.Packet;
import in.parapengu.craftbot.protocol.State;

import java.net.Socket;
import java.util.Map;

public class GlobalPacketStream extends PacketStream {

	private State state;

	public GlobalPacketStream(Map<State, Map<Integer, Class<? extends Packet>>> packets, String address, int port) {
		super(packets, address, port);
		this.state = State.STATUS;
	}

	@Override
	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	@Override
	public EventManager getManager() {
		return BotHandler.getHandler().getEventManager();
	}

	@Override
	public Logger getLogger() {
		return BotHandler.getHandler().getLogger();
	}

}
