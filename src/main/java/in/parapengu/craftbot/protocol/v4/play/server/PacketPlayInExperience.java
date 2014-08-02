package in.parapengu.craftbot.protocol.v4.play.server;

import in.parapengu.craftbot.protocol.Destination;
import in.parapengu.craftbot.protocol.Packet;
import in.parapengu.craftbot.protocol.PacketException;
import in.parapengu.craftbot.protocol.stream.PacketInputStream;
import in.parapengu.craftbot.protocol.stream.PacketOutputStream;

import java.io.IOException;

public class PacketPlayInExperience extends Packet {

	private float bar;
	private short level;
	private short experience;

	public PacketPlayInExperience() {
		super(0x1F);
	}

	@Override
	public void build(PacketInputStream input) throws IOException {
		this.bar = input.readFloat();
		this.level = input.readShort();
		this.experience = input.readShort();
	}

	@Override
	public void send(PacketOutputStream output) throws IOException {
		throw new PacketException("Can not send an inbound packet", getClass(), Destination.SERVER);
	}

	public float getBar() {
		return bar;
	}

	public short getLevel() {
		return level;
	}

	public short getExperience() {
		return experience;
	}

}
