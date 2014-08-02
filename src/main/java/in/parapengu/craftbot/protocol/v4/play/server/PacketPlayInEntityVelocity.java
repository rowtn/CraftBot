package in.parapengu.craftbot.protocol.v4.play.server;

import in.parapengu.craftbot.location.Vector;
import in.parapengu.craftbot.protocol.stream.PacketInputStream;

import java.io.IOException;

public class PacketPlayInEntityVelocity extends PacketPlayInEntity {

	private double x;
	private double y;
	private double z;
	private Vector velocity;

	public PacketPlayInEntityVelocity() {
		super(0x12);
	}

	@Override
	public void build(PacketInputStream input) throws IOException {
		super.build(input);
		this.x = input.readShort() / 8000D;
		this.y = input.readShort() / 8000D;
		this.z = input.readShort() / 8000D;
		this.velocity = new Vector(x, y, z);
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

	public Vector getVelocity() {
		return velocity;
	}

}
