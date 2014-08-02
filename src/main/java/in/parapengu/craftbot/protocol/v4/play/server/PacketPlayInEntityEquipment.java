package in.parapengu.craftbot.protocol.v4.play.server;

import in.parapengu.craftbot.protocol.stream.PacketInputStream;

import java.io.IOException;

public class PacketPlayInEntityEquipment extends PacketPlayInEntity {

	private EquipmentSlot slot;

	public PacketPlayInEntityEquipment() {
		super(0x04);
	}

	@Override
	public void build(PacketInputStream input) throws IOException {
		super.build(input);
		this.slot = EquipmentSlot.values()[input.readShort()];
	}

	public static enum EquipmentSlot {

		HAND,
		HELMET,
		CHESTPLATE,
		LEGGINGS,
		BOOTS

	}

}
