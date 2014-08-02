package in.parapengu.craftbot.protocol.v4.play.server;

import in.parapengu.craftbot.protocol.Destination;
import in.parapengu.craftbot.protocol.Packet;
import in.parapengu.craftbot.protocol.PacketException;
import in.parapengu.craftbot.protocol.stream.PacketInputStream;
import in.parapengu.craftbot.protocol.stream.PacketOutputStream;

import java.io.IOException;

public class PacketPlayInSpawnPainting extends Packet {

	private int entity;
	private String title;
	private int x;
	private int y;
	private int z;
	private int direction;

	public PacketPlayInSpawnPainting() {
		super(0x10);
	}

	@Override
	public void build(PacketInputStream input) throws IOException {
		this.entity = input.readVarInt();
		this.title = input.readString();
		this.x = input.readInt();
		this.y = input.readInt();
		this.z = input.readInt();
		this.direction = input.readInt();
	}

	@Override
	public void send(PacketOutputStream output) throws IOException {
		throw new PacketException("Can not send an inbound packet", getClass(), Destination.SERVER);
	}

	public int getEntity() {
		return entity;
	}

	public String getTitle() {
		return title;
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

	public int getDirection() {
		return direction;
	}

}
