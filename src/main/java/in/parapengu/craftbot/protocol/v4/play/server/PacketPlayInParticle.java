package in.parapengu.craftbot.protocol.v4.play.server;

import in.parapengu.craftbot.protocol.Destination;
import in.parapengu.craftbot.protocol.Packet;
import in.parapengu.craftbot.protocol.PacketException;
import in.parapengu.craftbot.protocol.stream.PacketInputStream;
import in.parapengu.craftbot.protocol.stream.PacketOutputStream;

import java.io.IOException;

public class PacketPlayInParticle extends Packet {

	private String particle;
	private float x;
	private float y;
	private float z;
	private float offsetX;
	private float offsetY;
	private float offsetZ;
	private float data;
	private int quantity;

	public PacketPlayInParticle() {
		super(0x2A);
	}

	@Override
	public void build(PacketInputStream input) throws IOException {
		this.particle = input.readString();
		this.x = input.readFloat();
		this.y = input.readFloat();
		this.z = input.readFloat();
		this.offsetX = input.readFloat();
		this.offsetY = input.readFloat();
		this.offsetZ = input.readFloat();
		this.data = input.readFloat();
		this.quantity = input.readInt();
	}

	@Override
	public void send(PacketOutputStream output) throws IOException {
		throw new PacketException("Can not send an inbound packet", getClass(), Destination.SERVER);
	}

	public String getParticle() {
		return particle;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getZ() {
		return z;
	}

	public float getOffsetX() {
		return offsetX;
	}

	public float getOffsetY() {
		return offsetY;
	}

	public float getOffsetZ() {
		return offsetZ;
	}

	public float getData() {
		return data;
	}

	public int getQuantity() {
		return quantity;
	}

}
