package in.parapengu.craftbot.protocol.v4.play.server;

import in.parapengu.craftbot.protocol.Destination;
import in.parapengu.craftbot.protocol.Packet;
import in.parapengu.craftbot.protocol.PacketException;
import in.parapengu.craftbot.protocol.stream.PacketInputStream;
import in.parapengu.craftbot.protocol.stream.PacketOutputStream;

import java.io.IOException;

public class PacketPlayInSpawnObject extends Packet {

	private int entity;
	private int type;
	private double x;
	private double y;
	private double z;
	private int data;
	private double vX;
	private double vY;
	private double vZ;
	private double yaw;
	private double pitch;

	public PacketPlayInSpawnObject() {
		super(0x0F);
	}

	@Override
	public void build(PacketInputStream input) throws IOException {
		this.entity = input.readVarInt();
		this.type = input.readByte();
		this.x = input.readInt() / 32D;
		this.y = input.readInt() / 32D;
		this.z = input.readInt() / 32D;
		this.yaw = (input.readByte() * 360) / 256D;
		this.pitch = (input.readByte() * 360) / 256D;
		this.data = input.readInt();
		if(data != 0) {
			this.vX = input.readShort() / 8000D;
			this.vY = input.readShort() / 8000D;
			this.vZ = input.readShort() / 8000D;
		}
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

	public int getData() {
		return data;
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

}
