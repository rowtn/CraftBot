package in.parapengu.craftbot.protocol.stream;

import org.apache.commons.io.output.NullOutputStream;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class PacketOutputArray extends PacketOutputStream {

	private static final Charset UTF8 = Charset.forName("UTF-8");

	private List<Byte> bytes;

	public PacketOutputArray() {
		super(new NullOutputStream());
		this.bytes = new ArrayList<>();
	}

	@Override
	public void write(int b) throws IOException {
		super.write(b);
		bytes.add((byte) b);
	}

	public byte[] toByteArray() {
		byte[] array = new byte[bytes.size()];
		for(int i = 0; i < array.length; i++) {
			array[i] = bytes.get(i);
		}
		return array;
	}

}
