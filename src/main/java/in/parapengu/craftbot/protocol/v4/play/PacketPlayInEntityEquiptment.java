package in.parapengu.craftbot.protocol.v4.play;

import in.parapengu.craftbot.protocol.Destination;
import in.parapengu.craftbot.protocol.Packet;
import in.parapengu.craftbot.protocol.PacketException;
import in.parapengu.craftbot.protocol.stream.PacketInputStream;
import in.parapengu.craftbot.protocol.stream.PacketOutputStream;

import java.io.IOException;

public class PacketPlayInEntityEquiptment extends Packet {

	private int entity;
	private EquipmentSlot slot;

	public PacketPlayInEntityEquiptment() {
		super(0x04);
	}

	@Override
	public void build(PacketInputStream input) throws IOException {
		this.entity = input.readInt();
		this.slot = EquipmentSlot.values()[input.readShort()];
	}

	@Override
	public void send(PacketOutputStream output) throws IOException {
		throw new PacketException("Can not send an inbound packet", getClass(), Destination.SERVER);
	}

	public static enum EquipmentSlot {

		HAND,
		HELMET,
		CHESTPLATE,
		LEGGINGS,
		BOOTS

	}

}
