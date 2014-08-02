package in.parapengu.craftbot.protocol.v4.play.server;

import in.parapengu.craftbot.entity.potion.PotionEffectType;
import in.parapengu.craftbot.protocol.stream.PacketInputStream;

import java.io.IOException;

public class PacketPlayInEntityRemoveEffect extends PacketPlayInEntity {

	private PotionEffectType type;

	public PacketPlayInEntityRemoveEffect() {
		super(0x1E);
	}

	@Override
	public void build(PacketInputStream input) throws IOException {
		super.build(input);
		this.type = PotionEffectType.getType(input.readByte());
	}

	public PotionEffectType getType() {
		return type;
	}

}
