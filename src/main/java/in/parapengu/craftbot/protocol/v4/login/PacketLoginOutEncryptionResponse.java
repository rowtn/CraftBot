package in.parapengu.craftbot.protocol.v4.login;

import in.parapengu.craftbot.protocol.Destination;
import in.parapengu.craftbot.protocol.Packet;
import in.parapengu.craftbot.protocol.PacketException;
import in.parapengu.craftbot.protocol.stream.PacketInputStream;
import in.parapengu.craftbot.protocol.stream.PacketOutputStream;

import java.io.IOException;

public class PacketLoginOutEncryptionResponse extends Packet {

	private byte[] key;
	private byte[] token;

	public PacketLoginOutEncryptionResponse(byte[] key, byte[] token) {
		super(0x01);
		this.key = key;
		this.token = token;
	}

	@Override
	public void build(PacketInputStream input) throws IOException {
		throw new PacketException("Can not receive an outbound packet", getClass(), Destination.CLIENT);
	}

	@Override
	public void send(PacketOutputStream output) throws IOException {
		output.writeByteArray(key);
		output.writeByteArray(token);
	}

	public byte[] getKey() {
		return key;
	}

	public byte[] getToken() {
		return token;
	}

}
