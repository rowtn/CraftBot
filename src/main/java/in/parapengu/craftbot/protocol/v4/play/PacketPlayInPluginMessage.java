package in.parapengu.craftbot.protocol.v4.play;

import in.parapengu.craftbot.protocol.Destination;
import in.parapengu.craftbot.protocol.Packet;
import in.parapengu.craftbot.protocol.PacketException;
import in.parapengu.craftbot.protocol.stream.PacketInputStream;
import in.parapengu.craftbot.protocol.stream.PacketOutputStream;

import java.io.IOException;

public class PacketPlayInPluginMessage extends Packet {

	private String channel;
	private short length;
	private byte[] bytes;

	public PacketPlayInPluginMessage() {
		super(0x3F);
	}

	@Override
	public void build(PacketInputStream input) throws IOException {
		this.channel = input.readString();
		this.length = input.readShort();
		this.bytes = new byte[length];
		input.readFully(bytes);
	}

	@Override
	public void send(PacketOutputStream output) throws IOException {
		throw new PacketException("Can not send an inbound packet", getClass(), Destination.SERVER);
	}

	public String getChannel() {
		return channel;
	}

	public short getLength() {
		return length;
	}

	public byte[] getBytes() {
		return bytes;
	}

}
