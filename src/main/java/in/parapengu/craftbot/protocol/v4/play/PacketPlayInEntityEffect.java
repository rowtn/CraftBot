package in.parapengu.craftbot.protocol.v4.play;

import in.parapengu.craftbot.entity.potion.PotionEffectType;
import in.parapengu.craftbot.protocol.stream.PacketInputStream;

import java.io.IOException;

public class PacketPlayInEntityEffect extends PacketPlayInEntity {

	private PotionEffectType type;
	private int amplifier;
	private int duration;

	public PacketPlayInEntityEffect() {
		super(0x1D);
	}

	@Override
	public void build(PacketInputStream input) throws IOException {
		super.build(input);
		this.type = PotionEffectType.getType(input.readByte());
		this.amplifier = input.readByte();
		this.duration = input.readShort();
	}

	public PotionEffectType getType() {
		return type;
	}

	public int getAmplifier() {
		return amplifier;
	}

	public int getDuration() {
		return duration;
	}

}
