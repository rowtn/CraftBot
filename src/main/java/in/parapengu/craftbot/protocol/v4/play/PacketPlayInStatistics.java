package in.parapengu.craftbot.protocol.v4.play;

import in.parapengu.craftbot.protocol.Destination;
import in.parapengu.craftbot.protocol.Packet;
import in.parapengu.craftbot.protocol.PacketException;
import in.parapengu.craftbot.protocol.stream.PacketInputStream;
import in.parapengu.craftbot.protocol.stream.PacketOutputStream;

import java.io.IOException;

public class PacketPlayInStatistics extends Packet {

	private Statistic[] statistics;

	public PacketPlayInStatistics() {
		super(0x37);
	}

	@Override
	public void build(PacketInputStream input) throws IOException {
		int count = input.readVarInt();
		statistics = new Statistic[count];
		for(int i = 0; i < count; i++) {
			String name = input.readString();
			int value = input.readVarInt();
			statistics[i] = new Statistic(name, value);
		}
	}

	@Override
	public void send(PacketOutputStream output) throws IOException {
		throw new PacketException("Can not send an inbound packet", getClass(), Destination.SERVER);
	}

	public Statistic[] getStatistics() {
		return statistics.clone();
	}

	public static class Statistic {

		private String name;
		private int value;

		private Statistic(String name, int value) {
			this.name = name;
			this.value = value;
		}

		public String getName() {
			return name;
		}

		public int getValue() {
			return value;
		}

	}

}
