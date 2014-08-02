package in.parapengu.craftbot.protocol.v4.play.server;

import in.parapengu.craftbot.protocol.Destination;
import in.parapengu.craftbot.protocol.Packet;
import in.parapengu.craftbot.protocol.PacketException;
import in.parapengu.craftbot.protocol.stream.PacketInputStream;
import in.parapengu.craftbot.protocol.stream.PacketOutputStream;

import java.io.IOException;

public class PacketPlayInEntityDestroy extends Packet {

	private int[] entities;

	public PacketPlayInEntityDestroy() {
		super(0x13);
	}

	@Override
	public void build(PacketInputStream input) throws IOException {
		this.entities = new int[input.readByte()];
		for(int i = 0; i < entities.length; i++) {
			entities[i] = input.readInt();
		}
	}

	@Override
	public void send(PacketOutputStream output) throws IOException {
		throw new PacketException("Can not send an inbound packet", getClass(), Destination.SERVER);
	}

	public int[] getEntities() {
		return entities.clone();
	}

}
