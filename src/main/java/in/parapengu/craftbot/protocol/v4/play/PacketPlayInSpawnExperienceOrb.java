package in.parapengu.craftbot.protocol.v4.play;

import in.parapengu.craftbot.protocol.Destination;
import in.parapengu.craftbot.protocol.Packet;
import in.parapengu.craftbot.protocol.PacketException;
import in.parapengu.craftbot.protocol.stream.PacketInputStream;
import in.parapengu.craftbot.protocol.stream.PacketOutputStream;

import java.io.IOException;

public class PacketPlayInSpawnExperienceOrb extends Packet {

	private int entity;
	private double x;
	private double y;
	private double z;
	private short count;

	public PacketPlayInSpawnExperienceOrb() {
		super(0x11);
	}

	@Override
	public void build(PacketInputStream input) throws IOException {
		this.entity = input.readVarInt();
		this.x = input.readInt() / 32D;
		this.y = input.readInt() / 32D;
		this.z = input.readInt() / 32D;
		this.count = input.readShort();
	}

	@Override
	public void send(PacketOutputStream output) throws IOException {
		throw new PacketException("Can not send an inbound packet", getClass(), Destination.SERVER);
	}

	public int getEntity() {
		return entity;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}

	public short getCount() {
		return count;
	}

}
