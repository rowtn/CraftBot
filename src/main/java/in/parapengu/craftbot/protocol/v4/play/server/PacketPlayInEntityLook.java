package in.parapengu.craftbot.protocol.v4.play.server;

import in.parapengu.craftbot.protocol.stream.PacketInputStream;

import java.io.IOException;

public class PacketPlayInEntityLook extends PacketPlayInEntityRotate {

	private double pitch;

	public PacketPlayInEntityLook() {
		super(0x16);
	}

	@Override
	public void build(PacketInputStream input) throws IOException {
		super.build(input);
		this.pitch = (input.readByte() * 360) / 256D;
	}

	public double getPitch() {
		return pitch;
	}

}
