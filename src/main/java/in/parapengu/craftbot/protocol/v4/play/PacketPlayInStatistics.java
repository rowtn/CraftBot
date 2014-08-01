package in.parapengu.craftbot.protocol.v4.play;

import in.parapengu.craftbot.protocol.Destination;
import in.parapengu.craftbot.protocol.Packet;
import in.parapengu.craftbot.protocol.PacketException;
import in.parapengu.craftbot.protocol.stream.PacketInputStream;
import in.parapengu.craftbot.protocol.stream.PacketOutputStream;

import static in.parapengu.craftbot.protocol.v4.play.PacketPlayInStatistics.StatisticType.*;

import java.io.IOException;

public class PacketPlayInStatistics extends Packet {

	private StatisticValue[] statistics;

	public PacketPlayInStatistics() {
		super(0x37);
	}

	@Override
	public void build(PacketInputStream input) throws IOException {
		int count = input.readVarInt();
		statistics = new StatisticValue[count];
		for(int i = 0; i < count; i++) {
			String name = input.readString();
			int value = input.readVarInt();
			statistics[i] = new StatisticValue(name, value);
		}
	}

	@Override
	public void send(PacketOutputStream output) throws IOException {
		throw new PacketException("Can not send an inbound packet", getClass(), Destination.SERVER);
	}

	public StatisticValue[] getStatistics() {
		return statistics.clone();
	}

	public static class StatisticValue {

		private Statistic statistic;
		private int value;

		private StatisticValue(String name, int value) {
			this.statistic = Statistic.getStatistic(name);
			this.value = value;
		}

		public Statistic getStatistic() {
			return statistic;
		}

		public int getValue() {
			return value;
		}

	}

	public static enum StatisticType {

		STATISTIC("stat"),
		ACHIEVEMENT("achievement");

		private String prefix;

		StatisticType(String prefix) {
			this.prefix = prefix;
		}

		public String getPrefix() {
			return prefix;
		}

	}

	public static enum Statistic {

		OPEN_INVENTORY(ACHIEVEMENT),
		MINE_WOOD(ACHIEVEMENT),
		BUILD_WORKBENCH(ACHIEVEMENT),
		BUILD_FURNACE(ACHIEVEMENT),
		ACQUIRE_IRON(ACHIEVEMENT),
		BUILD_HOE(ACHIEVEMENT),
		MAKE_BREAD(ACHIEVEMENT),
		BAKE_CAKE(ACHIEVEMENT),
		BUILD_BETTER_PICKAXE(ACHIEVEMENT),
		COOK_FISH(ACHIEVEMENT),
		ON_A_RAIL(ACHIEVEMENT),
		BUILD_SWORD(ACHIEVEMENT),
		KILL_ENEMY(ACHIEVEMENT),
		KILL_COW(ACHIEVEMENT),
		FLY_PIG(ACHIEVEMENT),
		SNIPE_SKELETON(ACHIEVEMENT),
		DIAMONDS(ACHIEVEMENT),
		DIAMONDS_TO_YOU(ACHIEVEMENT),
		PORTAL(ACHIEVEMENT),
		GHAST(ACHIEVEMENT),
		BLAZE_ROD(ACHIEVEMENT),
		POTION(ACHIEVEMENT),
		THE_END(ACHIEVEMENT),
		THE_END2(ACHIEVEMENT),
		ENCHANTMENTS(ACHIEVEMENT),
		OVERKILL(ACHIEVEMENT),
		BOOKCASE(ACHIEVEMENT),
		BREED_COW(ACHIEVEMENT),
		SPAWN_WITHER(ACHIEVEMENT),
		KILL_WITHER(ACHIEVEMENT),
		FULL_BEACON(ACHIEVEMENT),
		EXPLORE_ALL_BIOMES(ACHIEVEMENT),

		LEAVE_GAME(STATISTIC),
		PLAY_ONE_MINUTE(STATISTIC),
		WALK_ONE_CM(STATISTIC),
		SWIM_ONE_CM(STATISTIC),
		FALL_ONE_CM(STATISTIC),
		CLIMB_ONE_CM(STATISTIC),
		FLY_ONE_CM(STATISTIC),
		DIVE_ONE_CM(STATISTIC),
		MINECART_ONE_CM(STATISTIC),
		BOAT_ONE_CM(STATISTIC),
		PIG_ONE_CM(STATISTIC),
		HORSE_ONE_CM(STATISTIC),
		JUMP(STATISTIC),
		DROP(STATISTIC),
		DAMAGE_DEALT(STATISTIC),
		DAMAGE_TAKEN(STATISTIC),
		DEATHS(STATISTIC),
		MOB_KILLS(STATISTIC),
		ANIMALS_BRED(STATISTIC),
		PLAYER_KILLS(STATISTIC),
		FISH_CAUGHT(STATISTIC),
		JUNK_FISHED(STATISTIC),
		TREASURE_FISHED(STATISTIC);

		private String identifier;
		private StatisticType type;

		Statistic(StatisticType type) {
			String identifier = name().toLowerCase();

			StringBuilder builder = new StringBuilder();
			String[] split = identifier.split("_");
			for(int i = 0; i < split.length; i++) {
				String string = split[i];

				if(i != 0) {
					char[] chars = string.toCharArray();
					chars[0] = Character.toUpperCase(chars[0]);
					string = new String(chars);
				}

				builder.append(string);
			}

			identifier = builder.toString();
			this.identifier = identifier;
			this.type = type;
		}

		public String getIdentifier() {
			return getType().getPrefix() + "." + identifier;
		}

		public StatisticType getType() {
			return type;
		}

		public static Statistic getStatistic(String identifier) {
			for(Statistic statistic : values()) {
				if(statistic.getIdentifier().equals(identifier)) {
					return statistic;
				}
			}

			return null;
		}

	}

}
