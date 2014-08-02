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
		byte[] compressed = new byte[length];
		input.readFully(compressed, 0, length);
		int i = 0;

		for(int j = 0; j < 16; j++)
			i += primaryBitmask >> j & 1;

		int k = 12288 * i;

		if(biomes)
			k += 256;

		byte[] data = new byte[k];
		ChunkInflater inflater = new ChunkInflater(getClass(), data, compressed, length);
		inflater.inflate();
		this.data = new ChunkData(x, z, primaryBitmask, secondaryBitmask, data);
	}

	@Override
	public void send(PacketOutputStream output) throws IOException {
		throw new PacketException("Can not send an inbound packet", getClass(), Destination.SERVER);
	}



	public ChunkData getChunk() {
		return data;
	}

	public boolean hasBiomes() {
		return biomes;
	}

	public static class ChunkData {

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

	public static class ChunkInflater {

		private Class<? extends Packet> packet;
		private Inflater inflater;
		private byte[] data;
		private byte[] compressed;
		private int length;
		private int attempts;

		public ChunkInflater(Class<? extends Packet> packet, byte[] data, byte[] compressed, int length) {
			this.packet = packet;
			this.inflater = new Inflater();
			this.data = data;
			this.compressed = compressed;
			this.length = length;
			this.inflater.setInput(compressed, 0, length);
		}

		public void inflate() throws PacketException {
			try {
				inflater.inflate(data);
			} catch(OutOfMemoryError error) {
				System.gc();
				attempts++;
				if(attempts >= 5) {
					throw new PacketException("Gave up attempting to decompress chunk data", packet, Destination.CLIENT);
				}

				inflater.end();
				inflater = new Inflater();
				inflater.setInput(compressed, 0, length);
				inflate();
			} catch(DataFormatException ex) {
				throw new PacketException("Bad compressed data format", packet, Destination.CLIENT);
			} finally {
				inflater.end();
			}
		}

		public Inflater getInflater() {
			return inflater;
		}

		public byte[] getData() {
			return data;
		}

		public byte[] getCompressed() {
			return compressed;
		}

		public int getLength() {
			return length;
		}

	}

}
