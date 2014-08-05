package in.parapengu.craftbot.world;

import in.parapengu.craftbot.protocol.v4.play.server.PacketPlayInChunkData;

/**
 * Created by August on 8/5/14.
 */
public interface World {

	Dimension getDimension();
	Difficulty getDifficulty();

	Block highestBlockAt(int x, int z);
	int highestYAt(int x, int z);
	Block getBlockAt(int x, int y, int z);

	long getAge();
	long getTime();
	void setAge(long age);
	void setTime(long time);

	void loadChunk(PacketPlayInChunkData.ChunkData data);

	public static enum Dimension {
		OVERWORLD(0), NETHER(-1), END(1);
		private int id;

		Dimension(int id) {
			this.id = id;
		}

		public int getId() {
			return id;
		}

		public static Dimension byId(int id) {
			switch(id) {
				case 0: return OVERWORLD;
				case -1: return NETHER;
				case 1: return END;
				default: return null;
			}
		}
	}

	public static enum Difficulty {
		PEACEFUL(0), EASY(1), NORMAL(2), HARD(3);

		private int id;

		Difficulty(int id) {
			this.id = id;
		}

		public int getId() {
			return id;
		}

		public static Difficulty byId(int id) {
			switch(id) {
				case 0: return PEACEFUL;
				case 1: return EASY;
				case 2: return NORMAL;
				case 3: return HARD;
				default: return null;
			}
		}
	}
}
