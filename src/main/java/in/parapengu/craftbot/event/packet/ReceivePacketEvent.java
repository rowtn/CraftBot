package in.parapengu.craftbot.event.packet;

import in.parapengu.craftbot.protocol.Packet;

public class ReceivePacketEvent extends PacketEvent {

	public ReceivePacketEvent(Packet packet) {
		super(packet);
	}

}
