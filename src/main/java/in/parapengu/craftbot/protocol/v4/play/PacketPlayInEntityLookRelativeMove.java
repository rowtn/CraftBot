package in.parapengu.craftbot.protocol.v4.play;

import in.parapengu.craftbot.protocol.stream.PacketInputStream;

import java.io.IOException;

public class PacketPlayInEntityLookRelativeMove extends PacketPlayInEntity {

	private double x;
	private double y;
	private double z;
	private double yaw;
	private double pitch;

	public PacketPlayInEntityLookRelativeMove() {
		super(0x17);
	}

	@Override
	public void build(PacketInputStream input) throws IOException {
		super.build(input);
		this.x = input.readByte() / 32D;
		this.y = input.readByte() / 32D;
		this.z = input.readByte() / 32D;
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
