package in.parapengu.craftbot.protocol.v4.handshaking;

import in.parapengu.craftbot.protocol.Destination;
import in.parapengu.craftbot.protocol.Packet;
import in.parapengu.craftbot.protocol.PacketException;
import in.parapengu.craftbot.protocol.Protocol;
import in.parapengu.craftbot.protocol.State;
import in.parapengu.craftbot.protocol.stream.PacketInputStream;
import in.parapengu.craftbot.protocol.stream.PacketOutputStream;

import java.io.IOException;

public class PacketHandshakeOutStatus extends Packet {

	private int protocol;
	private String address;
	private int port;
	private State state;

	public PacketHandshakeOutStatus(int protocol, String address, int port, State state) {
		super(0x00);
		this.protocol = protocol;
		this.address = address;
		this.port = port;
		this.state = state;
	}

	public PacketHandshakeOutStatus(Protocol protocol, String address, int port, State state) {
		this(protocol.getVersion(), address, port, state);
	}

	@Override
	public void build(PacketInputStream input) throws IOException {
		throw new PacketException("Can not receive an output packet", getClass(), Destination.CLIENT);
	}

	@Override
	public void send(PacketOutputStream buffer) throws IOException {
		buffer.writeVarInt(protocol);
		buffer.writeString(address);
		buffer.writeShort(port);
		buffer.writeVarInt(state.getId());
	}

}
