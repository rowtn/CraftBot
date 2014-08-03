package in.parapengu.craftbot.protocol.v4.play.server;

import in.parapengu.craftbot.inventory.nbt.NBTTagCompound;
import in.parapengu.craftbot.material.MaterialData;
import in.parapengu.craftbot.protocol.Destination;
import in.parapengu.craftbot.protocol.Packet;
import in.parapengu.craftbot.protocol.PacketException;
import in.parapengu.craftbot.protocol.stream.PacketInputStream;
import in.parapengu.craftbot.protocol.stream.PacketOutputStream;

import java.io.IOException;

public class PacketPlayInUpdateTileEntity extends Packet {

	private int x;
	private int y;
	private int z;
	private int action;
	private NBTTagCompound data;

	public PacketPlayInUpdateTileEntity() {
		super(0x35);
	}

	@Override
	public void build(PacketInputStream input) throws IOException {
		this.x = input.readInt();
		this.y = input.readShort();
		this.z = input.readInt();
		this.action = input.readUnsignedByte();
		this.data = input.readNBTTagCompound();
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

	public int getAction() {
		return action;
	}

	public NBTTagCompound getData() {
		return data;
	}

}
