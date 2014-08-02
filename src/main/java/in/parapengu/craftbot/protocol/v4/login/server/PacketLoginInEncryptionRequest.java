package in.parapengu.craftbot.protocol.v4.login.server;

import in.parapengu.craftbot.bot.BotHandler;
import in.parapengu.craftbot.protocol.Destination;
import in.parapengu.craftbot.protocol.Packet;
import in.parapengu.craftbot.protocol.PacketException;
import in.parapengu.craftbot.protocol.stream.PacketInputStream;
import in.parapengu.craftbot.protocol.stream.PacketOutputStream;

import java.io.IOException;

public class PacketLoginInEncryptionRequest extends Packet {

	private String server;
	private byte[] key;
	private byte[] token;

	public PacketLoginInEncryptionRequest() {
		super(0x01);
	}

	@Override
	public void build(PacketInputStream input) throws IOException {
		this.server = input.readString();
		this.key = input.readByteArray();
		this.token = input.readByteArray();

		BotHandler.getHandler().getLogger().info("Read public key length(" + key.length + ")");
		BotHandler.getHandler().getLogger().info("Read verify token length(" + token.length + ")");
	}

	@Override
	public void send(PacketOutputStream output) throws IOException {
		throw new PacketException("Can not send an inbound packet", getClass(), Destination.SERVER);
	}

	public String getServer() {
		return server;
	}

	public byte[] getKey() {
		return key;
	}

	public byte[] getToken() {
		return token;
	}

	private String getString(byte[] data) {
		StringBuilder buffer = new StringBuilder().append("new byte[] { ");
		for(byte b : data) {
			if(buffer.length() != 0)
				buffer.append(", ");
			buffer.append("0x");
			if(b <= 0xF)
				buffer.append(0);
			buffer.append(Integer.toHexString(b & 0xFF).toUpperCase());
		}
		return buffer.append(" };").toString();
	}

}
