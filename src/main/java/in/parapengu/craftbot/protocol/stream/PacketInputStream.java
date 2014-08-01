package in.parapengu.craftbot.protocol.stream;

import in.parapengu.craftbot.inventory.ItemStack;
import in.parapengu.craftbot.inventory.nbt.CompressedStreamTools;
import in.parapengu.craftbot.inventory.nbt.NBTTagCompound;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class PacketInputStream extends DataInputStream {

	private static final Charset UTF8 = Charset.forName("UTF-8");

	public PacketInputStream(InputStream in) {
		super(in);
	}

	public String readString() throws IOException {
		int length = readVarInt();
		byte[] data = new byte[length];
		readFully(data);
		return new String(data, UTF8);
	}

	public int getVarIntLength(int varInt) {
		int size = 0;
		while(true) {
			size++;
			if((varInt & 0xFFFFFF80) == 0)
				return size;
			varInt >>>= 7;
		}
	}

	public int readVarInt() throws IOException {
		int i = 0;
		int j = 0;
		while(true) {
			int k = read();
			if(k == -1)
				throw new IOException("End of stream");

			i |= (k & 0x7F) << j++ * 7;

			if(j > 5)
				throw new IOException("VarInt too big");

			if((k & 0x80) != 128)
				break;
		}

		return i;
	}

	public long readVarInt64() throws IOException {
		long varInt = 0;
		for(int i = 0; i < 10; i++) {
			byte b = readByte();
			varInt |= ((long) (b & (i != 9 ? 0x7F : 0x01))) << (i * 7);

			if(i == 9 && (((b & 0x80) == 0x80) || ((b & 0x7E) != 0)))
				throw new IOException("VarInt too big");
			if((b & 0x80) != 0x80)
				break;
		}

		return varInt;
	}

	public byte[] readByteArray() throws IOException {
		short length = readShort();
		if(length < 0)
			throw new IOException("Invalid array length");
		byte[] data = new byte[length];
		readFully(data);
		return data;
	}


	public ItemStack readItemStack() throws IOException {
		ItemStack item = null;
		short id = readShort();

		if(id >= 0) {
			byte stackSize = readByte();
			short damage = readShort();
			item = new ItemStack(id, stackSize, damage);
			item.setCompound(readNBTTagCompound());
		}

		return item;
	}

	public NBTTagCompound readNBTTagCompound() throws IOException {
		short length = readShort();

		if(length >= 0) {
			byte[] data = new byte[length];
			readFully(data);
			return CompressedStreamTools.decompress(data);
		} else
			return null;
	}

	public Map<Integer, DataObject<?>> readDataObjects() throws IOException {
		Map<Integer, DataObject<?>> map = new HashMap<>();
		for(byte b = readByte(); b != 127; b = readByte()) {
			int i = (b & 0xe0) >> 5;
			int j = b & 0x1f;
			DataObject<?> data = null;
			switch(i) {
				case 0:
					data = new DataObject<>(i, j, readByte());
					break;
				case 1:
					data = new DataObject<>(i, j, readShort());
					break;
				case 2:
					data = new DataObject<>(i, j, readInt());
					break;
				case 3:
					data = new DataObject<>(i, j, readFloat());
					break;
				case 4:
					data = new DataObject<>(i, j, readString());
					break;
				case 5:
					data = new DataObject<>(i, j, readItemStack());
					break;
				case 6:
					int k = readInt(); // x
					int l = readInt(); // y
					int i1 = readInt(); // z
					// watchableobject = new DataObject<>(i, j, new BlockLocation(k, l, i1));
					break;
			}
			map.put(i, data);
		}
		return map;
	}

	public InputStream getInput() {
		return in;
	}

}
