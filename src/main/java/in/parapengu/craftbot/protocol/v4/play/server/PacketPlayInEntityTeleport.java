package in.parapengu.craftbot.protocol.v4.play.server;

import in.parapengu.craftbot.protocol.stream.PacketInputStream;

import java.io.IOException;

public class PacketPlayInEntityTeleport extends PacketPlayInEntity {

	private double x;
	private double y;
	private double z;
	private double yaw;
	private double pitch;

	public PacketPlayInEntityTeleport() {
		super(0x18);
	}

	@Override
	public void build(PacketInputStream input) throws IOException {
		super.build(input);
		this.x = input.readInt() / 32D;
		this.y = input.readInt() / 32D;
		this.z = input.readInt() / 32D;
		this.yaw = (input.readByte() * 360) / 256D;
		this.pitch = (input.readByte() * 360) / 256D;
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

	public double getYaw() {
		return yaw;
	}

	public double getPitch() {
		return pitch;
	}

}
