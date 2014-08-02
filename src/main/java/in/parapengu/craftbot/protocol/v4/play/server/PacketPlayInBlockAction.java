package in.parapengu.craftbot.protocol.v4.play.server;

import in.parapengu.craftbot.protocol.Destination;
import in.parapengu.craftbot.protocol.Packet;
import in.parapengu.craftbot.protocol.PacketException;
import in.parapengu.craftbot.protocol.stream.PacketInputStream;
import in.parapengu.craftbot.protocol.stream.PacketOutputStream;

import java.io.IOException;

public class PacketPlayInBlockAction extends Packet {

	private int x;
	private int y;
	private int z;
	private int data1;
	private int data2;
	private int type;

	public PacketPlayInBlockAction() {
		super(0x24);
	}

	@Override
	public void build(PacketInputStream input) throws IOException {
		this.x = input.readInt();
		this.y = input.readShort();
		this.z = input.readInt();
		this.data1 = input.readUnsignedByte();
		this.data2 = input.readUnsignedByte();
		this.type = input.readVarInt();
	}

	@Override
	public void send(PacketOutputStream output) throws IOException {
		throw new PacketException("Can not send an inbound packet", getClass(), Destination.SERVER);
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	public int getData1() {
		return data1;
	}

	public int getData2() {
		return data2;
	}

	public int getType() {
		return type;
	}

}
