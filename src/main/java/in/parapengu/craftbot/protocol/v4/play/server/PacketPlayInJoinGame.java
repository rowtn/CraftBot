package in.parapengu.craftbot.protocol.v4.play.server;

import in.parapengu.craftbot.protocol.Destination;
import in.parapengu.craftbot.protocol.Packet;
import in.parapengu.craftbot.protocol.PacketException;
import in.parapengu.craftbot.protocol.stream.PacketInputStream;
import in.parapengu.craftbot.protocol.stream.PacketOutputStream;

import java.io.IOException;

public class PacketPlayInJoinGame extends Packet {

	private int entity;
	private int gamemode;
	private byte dimension;
	private int difficulty;
	private int players;
	private String level;

	public PacketPlayInJoinGame() {
		super(0x01);
	}

	@Override
	public void build(PacketInputStream input) throws IOException {
		this.entity = input.readInt();
		this.gamemode = input.readUnsignedByte();
		this.dimension = input.readByte();
		this.difficulty = input.readUnsignedByte();
		this.players = input.readUnsignedByte();
		this.level = input.readString();
	}

	@Override
	public void send(PacketOutputStream output) throws IOException {
		throw new PacketException("Can not send an inbound packet", getClass(), Destination.SERVER);
	}

	public int getEntity() {
		return entity;
	}

	public int getGamemode() {
		return gamemode;
	}

	public byte getDimension() {
		return dimension;
	}

	public int getDifficulty() {
		return difficulty;
	}

	public int getPlayers() {
		return players;
	}

	public String getLevel() {
		return level;
	}

}
