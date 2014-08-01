package in.parapengu.craftbot.protocol.v4.play;

import in.parapengu.craftbot.protocol.stream.PacketInputStream;

import java.io.IOException;

public class PacketPlayInEntityAttach extends PacketPlayInEntity {

	private int vehicle;
	private boolean leash;

	public PacketPlayInEntityAttach() {
		super(0x1B);
	}

	@Override
	public void build(PacketInputStream input) throws IOException {
		super.build(input);
		this.vehicle = input.readInt();
		this.leash = input.readBoolean();
	}

	public int getVehicle() {
		return vehicle;
	}

	public boolean isLeash() {
		return leash;
	}

}
