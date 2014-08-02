package in.parapengu.craftbot.protocol.v4.play;

import in.parapengu.craftbot.material.MaterialData;
import in.parapengu.craftbot.protocol.Destination;
import in.parapengu.craftbot.protocol.Packet;
import in.parapengu.craftbot.protocol.PacketException;
import in.parapengu.craftbot.protocol.stream.PacketInputStream;
import in.parapengu.craftbot.protocol.stream.PacketOutputStream;

import java.io.IOException;

public class PacketPlayInBlockChange extends Packet {

	private int x;
	private int y;
	private int z;
	private MaterialData data;

	public PacketPlayInBlockChange() {
		super(0x23);
	}

	protected PacketPlayInBlockChange(int x, int y, int z, int id, int meta) {
		this();
		this.x = x;
		this.y = y;
		this.z = z;
		this.data = new MaterialData(id, (byte) meta);
	}

	@Override
	public void build(PacketInputStream input) throws IOException {
		this.x = input.readInt();
		this.y = input.read();
		this.z = input.readInt();

		int id = input.readVarInt();
		int meta = input.readUnsignedByte();
		this.data = new MaterialData(id, (byte) meta);
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

	public MaterialData getData() {
		return data;
	}

}
