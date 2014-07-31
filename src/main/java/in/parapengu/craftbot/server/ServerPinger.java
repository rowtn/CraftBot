package in.parapengu.craftbot.server;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import in.parapengu.craftbot.bot.BotHandler;
import in.parapengu.craftbot.bot.ChatColor;
import in.parapengu.craftbot.protocol.Packet;
import in.parapengu.craftbot.protocol.State;
import in.parapengu.craftbot.protocol.stream.GlobalPacketStream;
import in.parapengu.craftbot.protocol.stream.PacketInputStream;
import in.parapengu.craftbot.protocol.stream.PacketOutputStream;
import in.parapengu.craftbot.protocol.stream.PacketStream;
import in.parapengu.craftbot.protocol.v4.handshaking.PacketHandshakeOutStatus;
import in.parapengu.craftbot.protocol.v4.play.PacketPlayInKeepAlive;
import in.parapengu.craftbot.protocol.v4.status.PacketStatusInResponse;
import in.parapengu.craftbot.protocol.v4.status.PacketStatusInTime;
import in.parapengu.craftbot.protocol.v4.status.PacketStatusOutRequest;
import in.parapengu.craftbot.protocol.v4.status.PacketStatusOutTime;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class ServerPinger {

	private static Map<State, Map<Integer, Class<? extends Packet>>> packets;
	public static final char COLOR_CHAR = '\u00A7';
	private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + String.valueOf(COLOR_CHAR) + "[0-9A-FK-OR]");

	static {
		packets = new HashMap<>();
		Map<Integer, Class<? extends Packet>> status = new HashMap<>();
		status.put(0x00, PacketStatusInResponse.class);
		status.put(0x01, PacketStatusInTime.class);
		packets.put(State.STATUS, status);
	}

	private Map<String, String> map;

	public Map<String, String> ping(String server, int port) {
		Socket socket;
		try {
			long start = System.currentTimeMillis();
			socket = new Socket();
			socket.connect(new InetSocketAddress(server, port), 5000);
			long end = System.currentTimeMillis();
			double ping = (double) (end - start) / 1000;

			PacketOutputStream out = new PacketOutputStream(socket.getOutputStream());
			PacketInputStream in = new PacketInputStream(socket.getInputStream());

			BotHandler.getHandler().getLogger().info("Time " + ping + "s (Created streams)");
			GlobalPacketStream stream = new GlobalPacketStream(packets, socket, out, in) {

				@Override
				public void handle(Packet packet) {
					getLogger().info("Received " + packet.getClass().getSimpleName() + ": " + packet.toString());
					String response = result(packet);
					if(response == null) {
						return;
					}

					map = new HashMap<>();
					JSONObject json = new JSONObject(response);
					String motd = ChatColor.stripColor(json.getString("description"));
					motd = motd.contains("\n") ? "\n" + motd : motd;
					map.put("motd", motd);
					// if(json.has("favicon")) {
					// 	map.put("favicon", json.getString("favicon").replace("data:image/png;base64,", ""));
					// }

					JSONObject version = json.getJSONObject("version");
					map.put("version", version.getString("name"));
					if(version.get("protocol").getClass().equals(String.class)) {
						map.put("protocol", version.getString("protocol"));
					} else {
						map.put("protocol", version.getInt("protocol") + "");
					}

					JSONObject players = json.getJSONObject("players");
					map.put("players", players.getInt("online") + "");
					map.put("max-players", players.getInt("max") + "");
					map.put("socket-ping", ping + "s");
					sendPacket(new PacketStatusOutTime(0L));
					close();
				}

			};
			BotHandler.getHandler().getLogger().info("Time " + ping + "s (Created Packet stream)");

			stream.sendPacket(new PacketHandshakeOutStatus(4, server, port, State.STATUS));
			stream.sendPacket(new PacketStatusOutRequest());
			BotHandler.getHandler().getLogger().info("Time " + ping + "s (Sent packets)");
			stream.start();
			BotHandler.getHandler().getLogger().info("Time " + ping + "s (Started stream)");
			return map;
		} catch(Exception e) {
			// nothing
		}

		return null;
	}

	public String result(Packet packet) {
		if(packet instanceof PacketStatusInResponse) {
			PacketStatusInResponse response = (PacketStatusInResponse) packet;
			return response.getJSON();
		}

		return null;
	}

}
