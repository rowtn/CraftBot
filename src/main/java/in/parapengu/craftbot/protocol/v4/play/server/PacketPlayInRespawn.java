package in.parapengu.craftbot.protocol.v4.play.server;

import in.parapengu.craftbot.protocol.Destination;
import in.parapengu.craftbot.protocol.Packet;
import in.parapengu.craftbot.protocol.PacketException;
import in.parapengu.craftbot.protocol.stream.PacketInputStream;
import in.parapengu.craftbot.protocol.stream.PacketOutputStream;

import java.io.IOException;

public class PacketPlayInRespawn extends Packet {

	private int dimension;
	private int gamemode;
	private int difficulty;
	private String level;

	public PacketPlayInRespawn() {
		super(0x07);
	}

	@Override
	public void build(PacketInputStream input) throws IOException {
		this.dimension = input.readInt();
		if(dimension < -1 || 1 < dimension) {
			throw new PacketException("Invalid dimension (" + dimension + ") supplied", getClass(), Destination.CLIENT);
		}

		this.gamemode = input.readUnsignedByte();
		this.difficulty = input.readUnsignedByte();
		this.level = input.readString();
	}

	@Override
	public void send(PacketOutputStream output) throws IOException {
		throw new PacketException("Can not send an inbound packet", getClass(), Destination.SERVER);
	}

	public int getDimension() {
		return dimension;
	}

	public int getGamemode() {
		return gamemode;
	}

	public int getDifficulty() {
		return difficulty;
	}

	public String getLevel() {
		return level;
	}

}
