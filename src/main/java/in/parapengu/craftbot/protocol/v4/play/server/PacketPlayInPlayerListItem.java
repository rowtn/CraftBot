package in.parapengu.craftbot.protocol.v4.play.server;

import in.parapengu.craftbot.protocol.Destination;
import in.parapengu.craftbot.protocol.Packet;
import in.parapengu.craftbot.protocol.PacketException;
import in.parapengu.craftbot.protocol.stream.PacketInputStream;
import in.parapengu.craftbot.protocol.stream.PacketOutputStream;

import java.io.IOException;

public class PacketPlayInPlayerListItem extends Packet {

	private String name;
	private boolean online;
	private short ping;

	public PacketPlayInPlayerListItem() {
		super(0x38);
	}

	@Override
	public void build(PacketInputStream input) throws IOException {
		this.name = input.readString();
		this.online = input.readBoolean();
		this.ping = input.readShort();
	}

	@Override
	public void send(PacketOutputStream output) throws IOException {
		throw new PacketException("Can not send an inbound packet", getClass(), Destination.SERVER);
	}

	public String getName() {
		return name;
	}

	public boolean isOnline() {
		return online;
	}

	public short getPing() {
		return ping;
	}

}
