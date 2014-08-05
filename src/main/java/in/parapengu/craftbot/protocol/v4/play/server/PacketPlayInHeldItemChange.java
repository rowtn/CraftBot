package in.parapengu.craftbot.protocol.v4.play.server;

import in.parapengu.craftbot.protocol.Destination;
import in.parapengu.craftbot.protocol.Packet;
import in.parapengu.craftbot.protocol.PacketException;
import in.parapengu.craftbot.protocol.stream.PacketInputStream;
import in.parapengu.craftbot.protocol.stream.PacketOutputStream;

import java.io.IOException;

public class PacketPlayInHeldItemChange extends Packet {

	private int slot;

	public PacketPlayInHeldItemChange() {
		super(0x09);
	}

	@Override
	public void build(PacketInputStream input) throws IOException {
		this.slot = input.readByte();
	}

	@Override
	public void send(PacketOutputStream output) throws IOException {
		throw new PacketException("Can not send an inbound packet", getClass(), Destination.SERVER);
	}

	public int getSlot() {
		return slot;
	}

}
