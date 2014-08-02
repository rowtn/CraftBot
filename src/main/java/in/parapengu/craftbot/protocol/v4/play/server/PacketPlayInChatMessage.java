package in.parapengu.craftbot.protocol.v4.play.server;

import in.parapengu.craftbot.protocol.Destination;
import in.parapengu.craftbot.protocol.Packet;
import in.parapengu.craftbot.protocol.PacketException;
import in.parapengu.craftbot.protocol.stream.PacketInputStream;
import in.parapengu.craftbot.protocol.stream.PacketOutputStream;
import org.json.JSONObject;

import java.io.IOException;

public class PacketPlayInChatMessage extends Packet {

	private JSONObject json;

	public PacketPlayInChatMessage() {
		super(0x02);
	}

	@Override
	public void build(PacketInputStream input) throws IOException {
		this.json = new JSONObject(input.readString());
	}

	@Override
	public void send(PacketOutputStream output) throws IOException {
		throw new PacketException("Can not send an inbound packet", getClass(), Destination.SERVER);
	}

	public JSONObject getJSON() {
		return json;
	}

}
