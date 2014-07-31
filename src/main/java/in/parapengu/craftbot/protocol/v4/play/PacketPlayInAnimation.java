package in.parapengu.craftbot.protocol.v4.play;

import in.parapengu.craftbot.protocol.Destination;
import in.parapengu.craftbot.protocol.Packet;
import in.parapengu.craftbot.protocol.PacketException;
import in.parapengu.craftbot.protocol.stream.PacketInputStream;
import in.parapengu.craftbot.protocol.stream.PacketOutputStream;

import java.io.IOException;

public class PacketPlayInAnimation extends Packet {

	private int entity;
	private Animation animation;

	public PacketPlayInAnimation() {
		super(0x0B);
	}

	@Override
	public void build(PacketInputStream input) throws IOException {
		this.entity = input.readVarInt();
		this.animation = Animation.getAnimation(input.readUnsignedByte());
	}

	@Override
	public void send(PacketOutputStream output) throws IOException {
		throw new PacketException("Can not send an inbound packet", getClass(), Destination.SERVER);
	}

	public int getEntity() {
		return entity;
	}

	public Animation getAnimation() {
		return animation;
	}

	public static enum Animation {

		SWING_ARM(0),
		DAMAGE_ANIMATION(1),
		LEAVE_BED(2),
		EAT_FOOD(3),
		CRITICAL_EFFECT(4),
		MAGICAL_CRITICAL_EFFECT(5),
		UNKNOWN(102),
		CROUCH(104),
		UNCROUCH(105);

		private int id;

		Animation(int id) {
			this.id = id;
		}

		public int getId() {
			return id;
		}

		public static Animation getAnimation(int id) {
			for(Animation animation : values()) {
				if(id == animation.getId()) {
					return animation;
				}
			}

			return UNKNOWN;
		}

	}

}
