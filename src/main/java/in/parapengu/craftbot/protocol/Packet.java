package in.parapengu.craftbot.protocol;

import in.parapengu.craftbot.protocol.stream.PacketInputStream;
import in.parapengu.craftbot.protocol.stream.PacketOutputStream;
import in.parapengu.craftbot.util.ClassUtils;

import java.io.IOException;

public abstract class Packet {

	private byte id;
	private Destination destination;

	protected Packet(byte id) {
		this.id = id;
	}

	protected Packet(int id) {
		this.id = (byte) id;
	}

	public byte getId() {
		return id;
	}

	public Destination getDestination() {
		return destination;
	}

	public abstract void build(PacketInputStream input) throws IOException;

	public abstract void send(PacketOutputStream buffer) throws IOException;

	@Override
	public String toString() {
		return ClassUtils.build(getClass(), this, true);
	}

}
