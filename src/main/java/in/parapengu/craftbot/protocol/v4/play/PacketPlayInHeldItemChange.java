package in.parapengu.craftbot.protocol.v4.play;

import in.parapengu.craftbot.protocol.Destination;
import in.parapengu.craftbot.protocol.Packet;
import in.parapengu.craftbot.protocol.PacketException;
import in.parapengu.craftbot.protocol.stream.PacketInputStream;
import in.parapengu.craftbot.protocol.stream.PacketOutputStream;

import java.io.IOException;

public class PacketPlayInHeldItemChange extends Packet {

	private byte slot;

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

	public byte getSlot() {
		return slot;
	}

}
