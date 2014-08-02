package in.parapengu.craftbot.protocol.v4.play.server;

import in.parapengu.craftbot.inventory.ItemStack;
import in.parapengu.craftbot.protocol.Destination;
import in.parapengu.craftbot.protocol.Packet;
import in.parapengu.craftbot.protocol.PacketException;
import in.parapengu.craftbot.protocol.stream.PacketInputStream;
import in.parapengu.craftbot.protocol.stream.PacketOutputStream;

import java.io.IOException;

public class PacketPlayInWindowItems extends Packet {

	private int window;
	private ItemStack[] stacks;

	public PacketPlayInWindowItems() {
		super(0x30);
	}

	@Override
	public void build(PacketInputStream input) throws IOException {
		this.window = input.readUnsignedByte();

		short length = input.readShort();
		this.stacks = new ItemStack[length];
		for(int i = 0; i < length; i++) {
			stacks[i] = input.readItemStack();
		}
	}

	@Override
	public void send(PacketOutputStream output) throws IOException {
		throw new PacketException("Can not send an inbound packet", getClass(), Destination.SERVER);
	}

	public int getWindow() {
		return window;
	}

	public ItemStack[] getStacks() {
		return stacks;
	}

}
