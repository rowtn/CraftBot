package in.parapengu.craftbot.protocol.v4.play.client;

import in.parapengu.craftbot.protocol.Destination;
import in.parapengu.craftbot.protocol.Packet;
import in.parapengu.craftbot.protocol.PacketException;
import in.parapengu.craftbot.protocol.stream.PacketInputStream;
import in.parapengu.craftbot.protocol.stream.PacketOutputStream;

import java.io.IOException;

public class PacketPlayOutKeepAlive extends Packet {

	private int aliveId;

	public PacketPlayOutKeepAlive(int aliveId) {
		super(0x00);
		this.aliveId = aliveId;
	}

	@Override
	public void build(PacketInputStream input) throws IOException {
		throw new PacketException("Can not receive an outbound packet", getClass(), Destination.CLIENT);
	}

	@Override
	public void send(PacketOutputStream output) throws IOException {
		output.writeInt(aliveId);
	}

	public int getAliveId() {
		return aliveId;
	}

}
