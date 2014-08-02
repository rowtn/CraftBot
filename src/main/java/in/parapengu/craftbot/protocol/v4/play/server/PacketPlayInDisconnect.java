package in.parapengu.craftbot.protocol.v4.play.server;

import in.parapengu.craftbot.protocol.Destination;
import in.parapengu.craftbot.protocol.Packet;
import in.parapengu.craftbot.protocol.PacketException;
import in.parapengu.craftbot.protocol.stream.PacketInputStream;
import in.parapengu.craftbot.protocol.stream.PacketOutputStream;
import org.json.JSONObject;

import java.io.IOException;

public class PacketPlayInDisconnect extends Packet {

	private JSONObject reason;

	public PacketPlayInDisconnect() {
		super(0x40);
	}

	@Override
	public void build(PacketInputStream input) throws IOException {
		this.reason = new JSONObject(input.readString());
	}

	@Override
	public void send(PacketOutputStream output) throws IOException {
		throw new PacketException("Can not send an inbound packet", getClass(), Destination.SERVER);
	}

	public JSONObject getReason() {
		return reason;
	}

}
