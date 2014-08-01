package in.parapengu.craftbot.protocol;

import in.parapengu.craftbot.event.Listener;
import in.parapengu.craftbot.protocol.stream.PacketInputStream;
import in.parapengu.craftbot.protocol.stream.PacketOutputStream;

import java.net.Socket;
import java.util.Map;

public class Protocol implements Listener {

	protected Map<State, Map<Integer, Class<? extends Packet>>> packets;
	protected Map<State, Integer> maxIds;

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

	public Map<State, Map<Integer, Class<? extends Packet>>> getPackets() {
		return packets;
	}

	public Map<State, Integer> getMaxIds() {
		return maxIds;
	}

	public int getMaxPacketId(State state) {
		return maxIds.get(state);
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
