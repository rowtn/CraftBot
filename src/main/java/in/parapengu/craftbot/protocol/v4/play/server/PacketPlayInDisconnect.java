package in.parapengu.craftbot.protocol.v4.play.server;

import in.parapengu.craftbot.protocol.Destination;
import in.parapengu.craftbot.protocol.Packet;
import in.parapengu.craftbot.protocol.PacketException;
import in.parapengu.craftbot.protocol.stream.PacketInputStream;
import in.parapengu.craftbot.protocol.stream.PacketOutputStream;
import in.parapengu.craftbot.util.ChatFormatter;

import java.io.IOException;

public class PacketPlayInDisconnect extends Packet {

	private String reason;

	public PacketPlayInDisconnect() {
		super(0x40);
	}

	@Override
	public void build(PacketInputStream input) throws IOException {
		String string = input.readString();
		this.reason = ChatFormatter.parse(string);
	}

	@Override
	public void send(PacketOutputStream output) throws IOException {
		throw new PacketException("Can not send an inbound packet", getClass(), Destination.SERVER);
	}

	public String getReason() {
		return reason;
	}

}
