package in.parapengu.craftbot.protocol.v4.play.server;

import in.parapengu.craftbot.protocol.Destination;
import in.parapengu.craftbot.protocol.Packet;
import in.parapengu.craftbot.protocol.PacketException;
import in.parapengu.craftbot.protocol.stream.PacketInputStream;
import in.parapengu.craftbot.protocol.stream.PacketOutputStream;

import java.io.IOException;

public class PacketPlayInPlayerPosition extends Packet {

	private double x;
	private double y;
	private double z;
	private float yaw;
	private float pitch;
	private boolean onGround;

	public PacketPlayInPlayerPosition() {
		super(0x08);
	}

	@Override
	public void build(PacketInputStream input) throws IOException {
		this.x = input.readDouble();
		this.y = input.readDouble();
		this.z = input.readDouble();
		this.yaw = input.readFloat();
		this.pitch = input.readFloat();
		this.onGround = input.readBoolean();
	}

	@Override
	public void send(PacketOutputStream output) throws IOException {
		throw new PacketException("Can not send an inbound packet", getClass(), Destination.SERVER);
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

	public float getYaw() {
		return yaw;
	}

	public float getPitch() {
		return pitch;
	}

	public boolean isOnGround() {
		return onGround;
	}

}
