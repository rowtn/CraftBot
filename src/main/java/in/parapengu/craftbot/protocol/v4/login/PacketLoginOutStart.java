package in.parapengu.craftbot.protocol.v4.login;

import in.parapengu.craftbot.protocol.Destination;
import in.parapengu.craftbot.protocol.Packet;
import in.parapengu.craftbot.protocol.PacketException;
import in.parapengu.craftbot.protocol.stream.PacketInputStream;
import in.parapengu.craftbot.protocol.stream.PacketOutputStream;

import java.io.IOException;

public class PacketLoginOutStart extends Packet {

	private String username;

	public PacketLoginOutStart(String username) {
		super(0x00);
		this.username = username;
	}

	@Override
	public void build(PacketInputStream input) throws IOException {
		throw new PacketException("Can not receive an outbound packet", getClass(), Destination.CLIENT);
	}

	@Override
	public void send(PacketOutputStream output) throws IOException {
		output.writeString(username);
	}

	public String getUsername() {
		return username;
	}

}
