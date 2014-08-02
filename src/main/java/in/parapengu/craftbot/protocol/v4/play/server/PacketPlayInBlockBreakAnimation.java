package in.parapengu.craftbot.protocol.v4.play.server;

import in.parapengu.craftbot.protocol.Destination;
import in.parapengu.craftbot.protocol.Packet;
import in.parapengu.craftbot.protocol.PacketException;
import in.parapengu.craftbot.protocol.stream.PacketInputStream;
import in.parapengu.craftbot.protocol.stream.PacketOutputStream;

import java.io.IOException;

public class PacketPlayInBlockBreakAnimation extends Packet {

	private int entity;
	private int x;
	private int y;
	private int z;
	private int stage;

	public PacketPlayInBlockBreakAnimation() {
		super(0x25);
	}

	@Override
	public void build(PacketInputStream input) throws IOException {
		this.entity = input.readVarInt();
		this.x = input.readInt();
		this.y = input.readInt();
		this.z = input.readInt();
		this.stage = input.readByte();
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

	public int getStage() {
		return stage;
	}

}
