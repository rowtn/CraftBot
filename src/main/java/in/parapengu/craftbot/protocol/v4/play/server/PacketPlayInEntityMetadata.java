package in.parapengu.craftbot.protocol.v4.play.server;

import in.parapengu.craftbot.protocol.stream.DataObject;
import in.parapengu.craftbot.protocol.stream.PacketInputStream;

import java.io.IOException;
import java.util.Map;

public class PacketPlayInEntityMetadata extends PacketPlayInEntity {

	private Map<Integer, DataObject<?>> metadata;

	public PacketPlayInEntityMetadata() {
		super(0x1C);
	}

	@Override
	public void build(PacketInputStream input) throws IOException {
		super.build(input);
		this.metadata = input.readDataObjects();
	}

	public Map<Integer, DataObject<?>> getMetadata() {
		return metadata;
	}

}
