package in.parapengu.craftbot.protocol.v4.play;

import in.parapengu.craftbot.protocol.Destination;
import in.parapengu.craftbot.protocol.Packet;
import in.parapengu.craftbot.protocol.PacketException;
import in.parapengu.craftbot.protocol.stream.DataObject;
import in.parapengu.craftbot.protocol.stream.PacketInputStream;
import in.parapengu.craftbot.protocol.stream.PacketOutputStream;

import java.io.IOException;
import java.util.Map;

public class PacketPlayInSpawnMob extends Packet {

	private int entity;
	private int type;
	private double x;
	private double y;
	private double z;
	private double vX;
	private double vY;
	private double vZ;
	private double yaw;
	private double pitch;
	private Map<Integer, DataObject<?>> metadata;

	public PacketPlayInSpawnMob() {
		super(0x0F);
	}

	@Override
	public void build(PacketInputStream input) throws IOException {
		this.entity = input.readVarInt();
		this.type = input.read();
		this.x = input.readInt() / 32D;
		this.y = input.readInt() / 32D;
		this.z = input.readInt() / 32D;
		this.yaw = (input.readByte() * 360) / 256D;
		this.pitch = (input.readByte() * 360) / 256D;
		this.yaw = (input.readByte() * 360) / 256D;
		this.vX = input.readShort() / 8000D;
		this.vY = input.readShort() / 8000D;
		this.vZ = input.readShort() / 8000D;
		this.metadata = input.readDataObjects();
	}

	@Override
	public void send(PacketOutputStream output) throws IOException {
		throw new PacketException("Can not send an inbound packet", getClass(), Destination.SERVER);
	}

	public int getEntity() {
		return entity;
	}

	public int getType() {
		return type;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}

	public double getVelocityX() {
		return vX;
	}

	public double getVelocityY() {
		return vY;
	}

	public double getVelocityZ() {
		return vZ;
	}

	public double getYaw() {
		return yaw;
	}

	public double getPitch() {
		return pitch;
	}

	public Map<Integer, DataObject<?>> getMetadata() {
		return metadata;
	}

}
