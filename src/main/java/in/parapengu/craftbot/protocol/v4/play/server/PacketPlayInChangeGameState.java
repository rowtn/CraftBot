package in.parapengu.craftbot.protocol.v4.play.server;

import in.parapengu.craftbot.protocol.Destination;
import in.parapengu.craftbot.protocol.Packet;
import in.parapengu.craftbot.protocol.PacketException;
import in.parapengu.craftbot.protocol.stream.PacketInputStream;
import in.parapengu.craftbot.protocol.stream.PacketOutputStream;

import java.io.IOException;

public class PacketPlayInChangeGameState extends Packet {

	private int reason;
	private float value;

	public PacketPlayInChangeGameState() {
		super(0x2B);
	}

	@Override
	public void build(PacketInputStream input) throws IOException {
		this.reason = input.readUnsignedByte();
		this.value = input.readFloat();
	}

	@Override
	public void send(PacketOutputStream output) throws IOException {
		throw new PacketException("Can not send an inbound packet", getClass(), Destination.SERVER);
	}

	public int getReason() {
		return reason;
	}

	public float getValue() {
		return value;
	}

}
