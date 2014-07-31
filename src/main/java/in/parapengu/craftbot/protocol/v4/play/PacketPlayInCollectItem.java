package in.parapengu.craftbot.protocol.v4.play;

import in.parapengu.craftbot.protocol.Destination;
import in.parapengu.craftbot.protocol.Packet;
import in.parapengu.craftbot.protocol.PacketException;
import in.parapengu.craftbot.protocol.stream.PacketInputStream;
import in.parapengu.craftbot.protocol.stream.PacketOutputStream;

import java.io.IOException;

public class PacketPlayInCollectItem extends Packet {

	private int collected;
	private int collector;

	public PacketPlayInCollectItem() {
		super(0x0D);
	}

	@Override
	public void build(PacketInputStream input) throws IOException {
		this.collected = input.readInt();
		this.collector = input.readInt();
	}

	@Override
	public void send(PacketOutputStream output) throws IOException {
		throw new PacketException("Can not send an inbound packet", getClass(), Destination.SERVER);
	}

	public int getCollected() {
		return collected;
	}

	public int getCollector() {
		return collector;
	}

}
