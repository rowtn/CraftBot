package in.parapengu.craftbot.protocol.v4.play;

import in.parapengu.craftbot.protocol.Destination;
import in.parapengu.craftbot.protocol.Packet;
import in.parapengu.craftbot.protocol.PacketException;
import in.parapengu.craftbot.protocol.stream.PacketInputStream;
import in.parapengu.craftbot.protocol.stream.PacketOutputStream;

import java.io.IOException;

public class PacketPlayInUseBed extends Packet {

	private int entity;
	private int x;
	private int y;
	private int z;

	public PacketPlayInUseBed() {
		super(0x0A);
	}

	@Override
	public void build(PacketInputStream input) throws IOException {
		this.entity = input.readInt();
		this.x = input.readInt();
		this.y = input.readUnsignedByte();
		this.z = input.readInt();
	}

	@Override
	public void send(PacketOutputStream output) throws IOException {
		throw new PacketException("Can not send an inbound packet", getClass(), Destination.SERVER);
	}

	public int getEntity() {
		return entity;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

}
