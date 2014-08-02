package in.parapengu.craftbot.protocol.v4.play;

import in.parapengu.craftbot.protocol.Destination;
import in.parapengu.craftbot.protocol.Packet;
import in.parapengu.craftbot.protocol.PacketException;
import in.parapengu.craftbot.protocol.stream.PacketInputStream;
import in.parapengu.craftbot.protocol.stream.PacketOutputStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class PacketPlayInMultipleBlockChange extends Packet {

	private List<PacketPlayInBlockChange> changes;

	public PacketPlayInMultipleBlockChange() {
		super(0x22);
	}

	@Override
	public void build(PacketInputStream input) throws IOException {
		int chunkX = input.readInt() * 16;
		int chunkZ = input.readInt() * 16;

		short records = input.readShort();
		int length = input.readInt();
		if(length != records * 4) {
			throw new PacketException("Length of records did not match the amount of records supplied", getClass(), Destination.CLIENT);
		}

		this.changes = new ArrayList<>();
		for(int i = 0; i < records; i++) {
			int data = input.readInt();

			int id = (data >> 4) & 0xFFF;
			int meta = data & 0xF;
			int x = chunkX + (data >> 28) & 0xF;
			int y = (data >> 16) & 0xFF;
			int z = chunkZ + (data >> 24) & 0xF;
			changes.add(new PacketPlayInBlockChange(x, y, z, id, meta));
		}
	}

	@Override
	public void send(PacketOutputStream output) throws IOException {
		throw new PacketException("Can not send an inbound packet", getClass(), Destination.SERVER);
	}

	public List<PacketPlayInBlockChange> getChanges() {
		return changes;
	}

}
