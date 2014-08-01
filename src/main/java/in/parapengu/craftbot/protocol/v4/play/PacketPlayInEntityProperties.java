package in.parapengu.craftbot.protocol.v4.play;

import com.google.common.collect.Lists;
import in.parapengu.craftbot.protocol.stream.PacketInputStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PacketPlayInEntityProperties extends PacketPlayInEntity {

	private EntityProperty[] properties;

	public PacketPlayInEntityProperties() {
		super(0x20);
	}

	@Override
	public void build(PacketInputStream input) throws IOException {
		super.build(input);

		this.properties = new EntityProperty[input.readInt()];
		for(int i = 0; i < properties.length; i++) {
			String name = input.readString();
			double value = input.readDouble();
			EntityProperty property = new EntityProperty(name, value);
			properties[i] = property;

			short modifiers = input.readShort();
			for(int i2 = 0; i2 < modifiers; i2++) {
				long uu = input.readLong();
				long id = input.readLong();
				UUID uuid = new UUID(uu, id);
				double amount = input.readDouble();
				int operation = input.read();
				property.addModifier(uuid, amount, operation);
			}
		}
	}

	public EntityProperty[] getProperties() {
		return properties.clone();
	}

	public static final class EntityProperty {

		private final String name;
		private final double value;
		private final List<Modifier> modifiers;

		private EntityProperty(String name, double value) {
			this.name = name;
			this.value = value;
			this.modifiers = new ArrayList<>();
		}

		public String getName() {
			return name;
		}

		public double getValue() {
			return value;
		}

		public List<Modifier> getModifiers() {
			return Lists.newArrayList(modifiers);
		}

		private void addModifier(UUID uuid, double amount, int operation) {
			modifiers.add(new Modifier(uuid, amount, operation));
		}

	}

	public static class Modifier {

		private final UUID uuid;
		private final double amount;
		private final int operation;

		private Modifier(UUID uuid, double amount, int operation) {
			this.uuid = uuid;
			this.amount = amount;
			this.operation = operation;
		}

		public UUID getUUID() {
			return uuid;
		}

		public double getAmount() {
			return amount;
		}

		public int getOperation() {
			return operation;
		}

	}

}
