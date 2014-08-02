package in.parapengu.craftbot.protocol.v4.play.server;

import in.parapengu.craftbot.inventory.ItemStack;
import in.parapengu.craftbot.protocol.Destination;
import in.parapengu.craftbot.protocol.Packet;
import in.parapengu.craftbot.protocol.PacketException;
import in.parapengu.craftbot.protocol.stream.PacketInputStream;
import in.parapengu.craftbot.protocol.stream.PacketOutputStream;

import java.io.IOException;

public class PacketPlayInSetSlot extends Packet {

	private int window;
	private int slot;
	private ItemStack stack;

	public PacketPlayInSetSlot() {
		super(0x2F);
	}

	@Override
	public void build(PacketInputStream input) throws IOException {
		this.window = input.readByte();
		this.slot = input.readShort();
		this.stack = input.readItemStack();
	}

	@Override
	public void send(PacketOutputStream output) throws IOException {
		throw new PacketException("Can not send an inbound packet", getClass(), Destination.SERVER);
	}

	public int getWindow() {
		return window;
	}

	public int getSlot() {
		return slot;
	}

	public ItemStack getStack() {
		return stack;
	}

}
