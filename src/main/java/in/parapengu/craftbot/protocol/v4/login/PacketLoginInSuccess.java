package in.parapengu.craftbot.protocol.v4.login;

import in.parapengu.craftbot.protocol.Destination;
import in.parapengu.craftbot.protocol.Packet;
import in.parapengu.craftbot.protocol.PacketException;
import in.parapengu.craftbot.protocol.stream.PacketInputStream;
import in.parapengu.craftbot.protocol.stream.PacketOutputStream;

import java.io.IOException;

public class PacketLoginInSuccess extends Packet {

	private String uuid;
	private String username;

	public PacketLoginInSuccess() {
		super(0x02);
	}

	@Override
	public void build(PacketInputStream input) throws IOException {
		this.uuid = input.readString();
		this.username = input.readString();
	}

	@Override
	public void send(PacketOutputStream output) throws IOException {
		throw new PacketException("Can not send an inbound packet", getClass(), Destination.SERVER);
	}

	public String getUUID() {
		return uuid;
	}

	public String getUsername() {
		return username;
	}

}
