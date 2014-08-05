package in.parapengu.craftbot.world.impl;

import in.parapengu.craftbot.location.Vector;
import in.parapengu.craftbot.material.Material;
import in.parapengu.craftbot.protocol.v4.play.server.PacketPlayInChunkData;
import in.parapengu.craftbot.world.Block;
import in.parapengu.craftbot.world.World;

/**
 * Created by August on 8/5/14.
 */
public class CraftBotWorld implements World {

	private PacketPlayInChunkData.ChunkData[][] chunks;
	private Dimension dimension;
	private Difficulty difficulty;

	private long age;
	private long time;

	public CraftBotWorld(PacketPlayInChunkData.ChunkData[][] chunks) {
		this.chunks = chunks;
	}

	@Override
	public Dimension getDimension() {
		return dimension;
	}

	public Difficulty getDifficulty() {
		return difficulty;
	}

	@Override
	public Block highestBlockAt(int x, int z) {
		int y = highestYAt(x, z);
		if(y == -1) return null;
		return getBlockAt(x, y, z);
	}

	@Override
	public int highestYAt(int x, int z) {
		PacketPlayInChunkData.ChunkData chunk;
		try {
			chunk = chunks[x >> 4][z >> 4];
		} catch(ArrayIndexOutOfBoundsException e) {
			return -1;
		}
		int cx = x % 16;
		int cz = z % 16;
		for(int y = 256; y > 0; y--) {
			if(chunk.getData()[(cx * 16 + cz) * 256 + y] != 0) return y;
		}
		return 0;
	}

	@Override
	public Block getBlockAt(int x, int y, int z) {
		PacketPlayInChunkData.ChunkData chunk = chunkAt(x >> 4, z >> 4);
		if(chunk == null) return null;
		int blockId = chunk.getData()[((x % 16) * 16 + (z % 16)) * 256 + y];
		return new Block(Material.getMaterial(blockId), new Vector(x, y, z), this);
	}

	private PacketPlayInChunkData.ChunkData chunkAt(int x, int z) {
		try {
			return chunks[x][z];
		} catch(ArrayIndexOutOfBoundsException e) {
			return null;
		}
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	@Override
	public void loadChunk(PacketPlayInChunkData.ChunkData data) {
		chunks[data.getX()][data.getZ()] = data;
	}

	public long getAge() {
		return age;
	}

	public void setAge(long age) {
		this.age = age;
	}

	public void setDimension(Dimension dimension) {
		this.dimension = dimension;
	}

	public void setDifficulty(Difficulty difficulty) {
		this.difficulty = difficulty;
	}

	@Override
	public String toString() {
		return "CraftBotWorld{" +
				"chunks_len=" + chunks.length +
				", dimension=" + dimension +
				", difficulty=" + difficulty +
				", age=" + age +
				", time=" + time +
				'}';
	}
}
