package in.parapengu.craftbot.protocol.v4.play;

import in.parapengu.craftbot.protocol.stream.PacketInputStream;

import java.io.IOException;

public class PacketPlayInEntityStatus extends PacketPlayInEntity {

	private int status;

	public PacketPlayInEntityStatus() {
		super(0x16);
	}

	@Override
	public void build(PacketInputStream input) throws IOException {
		super.build(input);
		this.status = input.readByte();
	}

	public int getStatus() {
		return status;
	}

}