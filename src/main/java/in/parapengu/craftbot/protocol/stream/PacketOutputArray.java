package in.parapengu.craftbot.protocol.stream;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.apache.commons.io.output.NullOutputStream;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class PacketOutputArray extends PacketOutputStream {

	private static final Charset UTF8 = Charset.forName("UTF-8");

	private ByteArrayDataOutput output;

	public PacketOutputArray() {
		super(new NullOutputStream());
		this.output = ByteStreams.newDataOutput();
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
