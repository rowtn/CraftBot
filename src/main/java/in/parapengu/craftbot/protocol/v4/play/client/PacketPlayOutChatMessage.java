package in.parapengu.craftbot.protocol.v4.play.client;

import in.parapengu.craftbot.protocol.Destination;
import in.parapengu.craftbot.protocol.Packet;
import in.parapengu.craftbot.protocol.PacketException;
import in.parapengu.craftbot.protocol.stream.PacketInputStream;
import in.parapengu.craftbot.protocol.stream.PacketOutputStream;

import java.io.IOException;

/**
 * Created by August on 8/4/14.
 */
public class PacketPlayOutChatMessage extends Packet {

	private String message;

	public PacketPlayOutChatMessage(String message) {
		super(0x01);
		this.message = message;
	}

	@Override
	public void build(PacketInputStream input) throws IOException {
		throw new PacketException("Cannot build server bound packet", getClass(), Destination.CLIENT);
	}

	@Override
	public void send(PacketOutputStream buffer) throws IOException {
		buffer.writeString(message);
	}

	public String getMessage() {
		return message;
	}
}