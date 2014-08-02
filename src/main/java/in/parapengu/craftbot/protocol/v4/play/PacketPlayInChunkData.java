package in.parapengu.craftbot.protocol.v4.play;

import in.parapengu.craftbot.protocol.Destination;
import in.parapengu.craftbot.protocol.Packet;
import in.parapengu.craftbot.protocol.PacketException;
import in.parapengu.craftbot.protocol.stream.PacketInputStream;
import in.parapengu.craftbot.protocol.stream.PacketOutputStream;

import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class PacketPlayInChunkData extends Packet {

	private int attempts;
	private boolean biomes;
	private ChunkData data;

	public PacketPlayInChunkData() {
		super(0x21);
	}

	@Override
	public void build(PacketInputStream input) throws IOException {
		int x = input.readInt();
		int z = input.readInt();
		this.biomes = input.readBoolean();
		int primaryBitmask = input.readShort();
		int secondaryBitmask = input.readShort();

		int length = input.readInt();
		byte[] compressedChunkData = new byte[length];
		input.readFully(compressedChunkData, 0, length);
		int i = 0;

		for(int j = 0; j < 16; j++)
			i += primaryBitmask >> j & 1;

		int k = 12288 * i;

		if(biomes)
			k += 256;

		byte[] chunkData = new byte[k];
		Inflater inflater = new Inflater();
		inflater.setInput(compressedChunkData, 0, length);
		inflate(inflater, chunkData, compressedChunkData, length);
		this.data = new ChunkData(x, z, primaryBitmask, secondaryBitmask, chunkData);
	}

	@Override
	public void send(PacketOutputStream output) throws IOException {
		throw new PacketException("Can not send an inbound packet", getClass(), Destination.SERVER);
	}

	public void inflate(Inflater inflater, byte[] chunkData, byte[] compressedChunkData, int length) throws PacketException {
		try {
			inflater.inflate(chunkData);
		} catch(OutOfMemoryError error) {
			System.gc();
			attempts++;
			if(attempts >= 5) {
				throw new PacketException("Gave up attempting to decompress chunk data", getClass(), Destination.CLIENT);
			}

			inflater.end();
			inflater = new Inflater();
			inflater.setInput(compressedChunkData, 0, length);
			inflate(inflater, chunkData, compressedChunkData, length);
		} catch(DataFormatException ex) {
			throw new PacketException("Bad compressed data format", getClass(), Destination.CLIENT);
		} finally {
			inflater.end();
		}
	}

	public ChunkData getChunk() {
		return data;
	}

	public boolean hasBiomes() {
		return biomes;
	}

	public static final class ChunkData {

		private int x;
		private int z;

		private int primaryBitmask, secondaryBitmask;
		private byte[] data;

		protected ChunkData(int x, int z, int primaryBitmask, int secondaryBitmask, byte[] data) {
			this.x = x;
			this.z = z;
			this.primaryBitmask = primaryBitmask;
			this.secondaryBitmask = secondaryBitmask;
			this.data = data;
		}

		public int getX() {
			return x;
		}

		public int getZ() {
			return z;
		}

		public int getPrimaryBitmask() {
			return primaryBitmask;
		}

		public int getSecondaryBitmask() {
			return secondaryBitmask;
		}

		public byte[] getData() {
			return data;
		}

	}

}
