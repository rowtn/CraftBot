package in.parapengu.craftbot.protocol;

import in.parapengu.craftbot.protocol.stream.PacketInputStream;
import in.parapengu.craftbot.protocol.stream.PacketOutputStream;

import java.net.Socket;

public class Protocol {

	private int version;
	private String startingVersion;
	private String endingVersion;

	protected Socket socket;
	protected PacketOutputStream output;
	protected PacketInputStream input;

	protected Protocol(int version, String startingVersion, String endingVersion) {
		this.version = version;
		this.startingVersion = startingVersion;
		this.endingVersion = endingVersion;
	}

	public int getVersion() {
		return version;
	}

	public String getStartingVersion() {
		return startingVersion;
	}

	public String getEndingVersion() {
		return endingVersion;
	}

}
