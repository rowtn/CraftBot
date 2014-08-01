package in.parapengu.craftbot.event.packet;

import in.parapengu.craftbot.protocol.Packet;

public class SentPacketEvent extends PacketEvent {

	public SentPacketEvent(Packet packet) {
		super(packet);
	}

}
