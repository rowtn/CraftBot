package in.parapengu.craftbot.protocol.stream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class PacketOutputArray extends PacketOutputStream {

	private static final Charset UTF8 = Charset.forName("UTF-8");

	private ByteArrayOutputStream output;

	public PacketOutputArray() {
		super(new ByteArrayOutputStream());
		this.output = new ByteArrayOutputStream();
	}

	@Override
	public void write(int b) throws IOException {
		super.write(b);
		output.write(b);
		// BotHandler.getHandler().getLogger().debug("Writing " + b + " to the byte list: " + toByteList());
	}

	public List<Byte> toByteList() {
		List<Byte> bytes = new ArrayList<>();
		for(byte b : toByteArray()) {
			bytes.add(b);
		}
		return bytes;
	}

	public byte[] toByteArray() {
		return output.toByteArray();
	}

}
