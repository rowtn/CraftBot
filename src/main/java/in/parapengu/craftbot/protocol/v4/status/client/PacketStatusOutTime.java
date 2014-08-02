package in.parapengu.craftbot.protocol.v4.status.client;

import in.parapengu.craftbot.protocol.Destination;
import in.parapengu.craftbot.protocol.Packet;
import in.parapengu.craftbot.protocol.PacketException;
import in.parapengu.craftbot.protocol.stream.PacketInputStream;
import in.parapengu.craftbot.protocol.stream.PacketOutputStream;

import java.io.IOException;

public class PacketStatusOutTime extends Packet {

	private long time;

	public PacketStatusOutTime(long time) {
		super(0x01);
		this.time = time;
	}

	@Override
	public void build(PacketInputStream input) throws IOException {
		throw new PacketException("Can not receive an outbound packet", getClass(), Destination.CLIENT);
	}

	@Override
	public void send(PacketOutputStream output) throws IOException {
		output.writeLong(time);
	}

	public long getTime() {
		return time;
	}

}
