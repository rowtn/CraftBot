package in.parapengu.craftbot.protocol.v4.play.server;

import in.parapengu.craftbot.protocol.Destination;
import in.parapengu.craftbot.protocol.Packet;
import in.parapengu.craftbot.protocol.PacketException;
import in.parapengu.craftbot.protocol.stream.PacketInputStream;
import in.parapengu.craftbot.protocol.stream.PacketOutputStream;

import java.io.IOException;

public class PacketPlayInChunkBulk extends Packet {

	private PacketPlayInChunkData.ChunkData[] chunks;
	private boolean skylight;

	public PacketPlayInChunkBulk() {
		super(0x26);
	}

	@Override
	public void build(PacketInputStream input) throws IOException {
		PacketPlayInChunkData.ChunkData[] chunks = new PacketPlayInChunkData.ChunkData[input.readShort()];
		int length = input.readInt();
		this.skylight = input.readBoolean();

		byte[] compressed = new byte[length];
		input.readFully(compressed, 0, length);
		byte[] data = new byte[196864 * chunks.length];
		PacketPlayInChunkData.ChunkInflater inflater = new PacketPlayInChunkData.ChunkInflater(getClass(), data, compressed, length);
		inflater.inflate();

		int dataPosition = 0;
		for(int i = 0; i < chunks.length; i++) {
			int x = input.readInt();
			int z = input.readInt();
			int primaryBitmask = input.readShort();
			int secondaryBitmask = input.readShort();

			int primarySize = 0, secondarySize = 0;
			for(int chunkIndex = 0; chunkIndex < 16; ++chunkIndex) {
				primarySize += primaryBitmask >> chunkIndex & 1;
				secondarySize += secondaryBitmask >> chunkIndex & 1;
			}

			int dataLength = 2048 * 4 * primarySize + 256;
			dataLength += 2048 * secondarySize;

			if(skylight)
				dataLength += 2048 * primarySize;

			byte[] chunkData = new byte[dataLength];
			System.arraycopy(data, dataPosition, chunkData, 0, dataLength);
			dataPosition += dataLength;

			chunks[i] = new PacketPlayInChunkData.ChunkData(x, z, primaryBitmask, secondaryBitmask, chunkData);
		}
		this.chunks = chunks;
	}

	@Override
	public void send(PacketOutputStream output) throws IOException {
		throw new PacketException("Can not send an inbound packet", getClass(), Destination.SERVER);
	}

	public PacketPlayInChunkData.ChunkData[] getChunks() {
		return chunks;
	}

	public boolean hasSkylight() {
		return skylight;
	}

}
