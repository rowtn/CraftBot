package in.parapengu.craftbot.protocol;

import in.parapengu.craftbot.protocol.stream.PacketInputStream;
import in.parapengu.craftbot.protocol.stream.PacketOutputStream;

public abstract class Packet {

	private byte id;
	private Destination destination;

	protected Packet(byte id) {
		this.id = id;
	}

	public byte getId() {
		return id;
	}

	public Destination getDestination() {
		return destination;
	}

	public abstract void build(PacketInputStream input);

	public abstract void send(PacketOutputStream output);

}
