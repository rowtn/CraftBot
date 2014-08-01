package in.parapengu.craftbot.protocol.v4.play;

import in.parapengu.craftbot.protocol.stream.PacketInputStream;

import java.io.IOException;

public class PacketPlayInEntityRelativeMove extends PacketPlayInEntity {

	private double x;
	private double y;
	private double z;

	public PacketPlayInEntityRelativeMove() {
		super(0x15);
	}

	@Override
	public void build(PacketInputStream input) throws IOException {
		super.build(input);
		this.x = input.readByte() / 32D;
		this.y = input.readByte() / 32D;
		this.z = input.readByte() / 32D;
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

}
