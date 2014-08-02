package in.parapengu.craftbot.protocol.v4.play.server;

import in.parapengu.craftbot.protocol.Destination;
import in.parapengu.craftbot.protocol.Packet;
import in.parapengu.craftbot.protocol.PacketException;
import in.parapengu.craftbot.protocol.stream.PacketInputStream;
import in.parapengu.craftbot.protocol.stream.PacketOutputStream;

import java.io.IOException;

public class PacketPlayInPlayerAbilities extends Packet {

	private static int CREATIVE_MODE = 0x1, FLYING = 0x2, CAN_FLY = 0x4, GOD_MODE = 0x8;

	private boolean creative;
	private boolean flying;
	private boolean ableToFly;
	private boolean invincible;
	private float walkingSpeed;
	private float flyingSpeed;

	public PacketPlayInPlayerAbilities() {
		super(0x39);
	}

	@Override
	public void build(PacketInputStream input) throws IOException {
		int flags = input.read();
		this.creative = (flags & CREATIVE_MODE) != 0;
		this.flying = (flags & FLYING) != 0;
		this.ableToFly = (flags & CAN_FLY) != 0;
		this.invincible = (flags & GOD_MODE) != 0;
		this.flyingSpeed = input.readInt() / 250F;
		this.walkingSpeed = input.readInt() / 250F;
	}

	@Override
	public void send(PacketOutputStream output) throws IOException {
		throw new PacketException("Can not send an inbound packet", getClass(), Destination.SERVER);
	}

	public boolean isCreative() {
		return creative;
	}

	public boolean isFlying() {
		return flying;
	}

	public boolean isAbleToFly() {
		return ableToFly;
	}

	public boolean isInvincible() {
		return invincible;
	}

	public float getWalkingSpeed() {
		return walkingSpeed;
	}

	public float getFlyingSpeed() {
		return flyingSpeed;
	}

}
