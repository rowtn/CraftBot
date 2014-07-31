package in.parapengu.craftbot.protocol.v4.play;

import in.parapengu.craftbot.protocol.Destination;
import in.parapengu.craftbot.protocol.Packet;
import in.parapengu.craftbot.protocol.PacketException;
import in.parapengu.craftbot.protocol.stream.PacketInputStream;
import in.parapengu.craftbot.protocol.stream.PacketOutputStream;

import java.io.IOException;

public class PacketPlayInUpdateHealth extends Packet {

	private float health;
	private short food;
	private float saturation;

	public PacketPlayInUpdateHealth() {
		super(0x06);
	}

	@Override
	public void build(PacketInputStream input) throws IOException {
		this.health = input.readFloat();
		this.food = input.readShort();
		this.saturation = input.readFloat();
	}

	@Override
	public void send(PacketOutputStream output) throws IOException {
		throw new PacketException("Can not send an inbound packet", getClass(), Destination.SERVER);
	}

	public float getHealth() {
		return health;
	}

	public short getFood() {
		return food;
	}

	public float getSaturation() {
		return saturation;
	}

}
