package in.parapengu.craftbot.protocol.stream;

import java.io.IOException;

public class PacketInputArray extends PacketInputStream {

	private PacketInputStream input;
	private int length;
	private int packetId;

	private int current;
	private int[] bytes;

	public PacketInputArray(PacketInputStream input) {
		super(input);
		this.input = input;
	}

	public void build() throws IOException {
		this.length = input.readVarInt();
		if(length <= 0) {
			throw new IOException("Illegal packet length received: " + length);
		}

		int[] packet = getVarInt();
		this.packetId = packet[0];

		this.bytes = new int[length - packet[1]];
		for(int i = 0; i < bytes.length; i++) {
			bytes[i] = input.read();
		}
	}

	public int getLength() {
		return length;
	}

	public int getPacketId() {
		return packetId;
	}

	public int[] getVarInt() throws IOException {
		int[] result = new int[2];

		int length = 0;
		int i = 0;
		int j = 0;
		while(true) {
			int k = read();
			if(k == -1)
				throw new IOException("End of stream");
			length++;

			i |= (k & 0x7F) << j++ * 7;

			if(j > 5)
				throw new IOException("VarInt too big");

			if((k & 0x80) != 128)
				break;
		}

		result[0] = i;
		result[1] = length;
		return result;
	}

	public int read() {
		int result = bytes[current];
		current++;
		return result;
	}

}
