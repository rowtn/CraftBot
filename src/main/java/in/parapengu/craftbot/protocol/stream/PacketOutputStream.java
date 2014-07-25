package in.parapengu.craftbot.protocol.stream;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

public class PacketOutputStream extends DataOutputStream {

	private static final Charset UTF8 = Charset.forName("UTF-8");

	public PacketOutputStream(OutputStream out) {
		super(out);
	}

	public void writeString(String string) throws IOException {
		writeVarInt(string.length());
		write(string.getBytes(UTF8));
	}

	public void writeVarInt(int paramInt) throws IOException {
		while(true) {
			if((paramInt & 0xFFFFFF80) == 0) {
				writeByte((byte) paramInt);
				return;
			}

			writeByte((byte) (paramInt & 0x7F | 0x80));
			paramInt >>>= 7;
		}
	}

	public void writeVarInt64(long varInt) throws IOException {
		int length = 10;
		for(int i = 9; i >= 0; i--)
			if(((varInt >> (i * 7)) & (i != 9 ? 0x7F : 0x01)) == 0)
				length--;
		for(int i = 0; i < length; i++)
			writeByte((int) ((i == length - 1 ? 0x00 : 0x80) | ((varInt >> (i * 7)) & (i != 9 ? 0x7F : 0x01))));
	}

	public void writeByteArray(byte[] data) throws IOException {
		writeShort(data.length);
		write(data);
	}

}
