package in.parapengu.craftbot.event.packet;

import in.parapengu.craftbot.event.Event;
import in.parapengu.craftbot.protocol.Packet;

public class PacketEvent extends Event {

	private Packet packet;

	public PacketEvent(Packet packet) {
		this.packet = packet;
	}

	public Packet getPacket() {
		return packet;
	}

}
