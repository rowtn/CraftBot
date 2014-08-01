package in.parapengu.craftbot.protocol.v4.play;

import in.parapengu.craftbot.protocol.stream.PacketInputStream;

import java.io.IOException;

public class PacketPlayInEntityRotate extends PacketPlayInEntity {

	private double yaw;

	public PacketPlayInEntityRotate() {
		super(0x16);
	}

	protected PacketPlayInEntityRotate(int id) {
		super(id);
	}

	@Override
	public void build(PacketInputStream input) throws IOException {
		super.build(input);
		this.yaw = (input.readByte() * 360) / 256D;
	}

	public double getYaw() {
		return yaw;
	}

}
