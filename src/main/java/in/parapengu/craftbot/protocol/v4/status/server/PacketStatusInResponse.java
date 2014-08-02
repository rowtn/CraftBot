package in.parapengu.craftbot.protocol.v4.status.server;

import in.parapengu.craftbot.protocol.Destination;
import in.parapengu.craftbot.protocol.Packet;
import in.parapengu.craftbot.protocol.PacketException;
import in.parapengu.craftbot.protocol.stream.PacketInputStream;
import in.parapengu.craftbot.protocol.stream.PacketOutputStream;

import java.io.IOException;

public class PacketStatusInResponse extends Packet {

	private String json;

	public PacketStatusInResponse() {
		super(0x00);
	}

	@Override
	public void build(PacketInputStream input) throws IOException {
		this.json = input.readString();
	}

	@Override
	public void send(PacketOutputStream output) throws IOException {
		throw new PacketException("Can not send an inbound packet", getClass(), Destination.SERVER);
	}

	public String getJSON() {
		return json;
	}

}
