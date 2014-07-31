package in.parapengu.craftbot.protocol.v4.play;

import in.parapengu.craftbot.protocol.Destination;
import in.parapengu.craftbot.protocol.Packet;
import in.parapengu.craftbot.protocol.PacketException;
import in.parapengu.craftbot.protocol.stream.DataObject;
import in.parapengu.craftbot.protocol.stream.PacketInputStream;
import in.parapengu.craftbot.protocol.stream.PacketOutputStream;

import java.io.IOException;
import java.util.Map;

public class PacketPlayInSpawnPlayer extends Packet {

	private int entity;
	private String uuid;
	private String name;
	private PlayerProperty[] properties;

	private double x;
	private double y;
	private double z;
	private double yaw;
	private double pitch;
	private int heldItemId;
	private Map<Integer, DataObject<?>> metadata;

	public PacketPlayInSpawnPlayer() {
		super(0x0C);
	}

	@Override
	public void build(PacketInputStream input) throws IOException {
		this.entity = input.readVarInt();
		this.uuid = input.readString();
		this.name = input.readString();

		this.properties = new PlayerProperty[input.readVarInt()];
		for(int i = 0; i < properties.length; i++) {
			String name = input.readString();
			String value = input.readString();
			String signature = input.readString();
			this.properties[i] = new PlayerProperty(name, value, signature);
		}

		this.x = input.readInt() / 32D;
		this.y = input.readInt() / 32D;
		this.z = input.readInt() / 32D;
		this.yaw = (input.readByte() * 360) / 256D;
		this.pitch = (input.readByte() * 360) / 256D;
		this.heldItemId = input.readShort();
		this.metadata = input.readDataObjects();
	}

	@Override
	public void send(PacketOutputStream output) throws IOException {
		throw new PacketException("Can not send an inbound packet", getClass(), Destination.SERVER);
	}

	public int getEntity() {
		return entity;
	}

	public String getUUID() {
		return uuid;
	}

	public String getName() {
		return name;
	}

	public PlayerProperty[] getProperties() {
		return properties;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}

	public double getYaw() {
		return yaw;
	}

	public double getPitch() {
		return pitch;
	}

	public int getHeldItemId() {
		return heldItemId;
	}

	public Map<Integer, DataObject<?>> getMetadata() {
		return metadata;
	}

	public static class PlayerProperty {
		private final String name, value, signature;

		private PlayerProperty(String name, String value, String signature) {
			this.name = name;
			this.value = value;
			this.signature = signature;
		}

		public String getName() {
			return name;
		}

		public String getValue() {
			return value;
		}

		public String getSignature() {
			return signature;
		}
	}

}
