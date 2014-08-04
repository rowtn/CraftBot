package in.parapengu.craftbot.protocol.v4.play.server;

import in.parapengu.craftbot.inventory.ItemStack;
import in.parapengu.craftbot.protocol.stream.PacketInputStream;

import java.io.IOException;

public class PacketPlayInEntityEquipment extends PacketPlayInEntity {

	private EquipmentSlot slot;
	private ItemStack item;

	public PacketPlayInEntityEquipment() {
		super(0x04);
	}

	@Override
	public void build(PacketInputStream input) throws IOException {
		super.build(input);
		this.slot = EquipmentSlot.values()[input.readShort()];
		this.item = input.readItemStack();
	}

	public EquipmentSlot getSlot() {
		return slot;
	}

	public ItemStack getItem() {
		return item;
	}

	public static enum EquipmentSlot {

		HAND,
		HELMET,
		CHESTPLATE,
		LEGGINGS,
		BOOTS

	}

}
