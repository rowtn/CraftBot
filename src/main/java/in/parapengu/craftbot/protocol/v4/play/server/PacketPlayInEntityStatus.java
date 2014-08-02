package in.parapengu.craftbot.protocol.v4.play.server;

import in.parapengu.craftbot.protocol.stream.PacketInputStream;

import java.io.IOException;

public class PacketPlayInEntityStatus extends PacketPlayInEntity {

	private int status;

	public PacketPlayInEntityStatus() {
		super(0x1A);
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
