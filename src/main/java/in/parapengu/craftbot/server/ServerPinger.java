package in.parapengu.craftbot.server;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import in.parapengu.craftbot.protocol.stream.PacketStream;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class ServerPinger {

	public static final char COLOR_CHAR = '\u00A7';
	private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + String.valueOf(COLOR_CHAR) + "[0-9A-FK-OR]");

	public static Map<String, String> ping(String server, int port) {
		Socket clientSocket;
		try {
			long start = System.currentTimeMillis();
			clientSocket = new Socket();
			clientSocket.connect(new InetSocketAddress(server, port), 5000);
			long end = System.currentTimeMillis();
			double ping = (double) (end - start) / 1000;

			DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
			DataInputStream in = new DataInputStream(clientSocket.getInputStream());
			ByteArrayDataOutput buf = ByteStreams.newDataOutput();
			PacketStream.writeVarInt(buf, 0x00);
			PacketStream.writeVarInt(buf, 4);
			PacketStream.writeString(buf, server);
			buf.writeShort(port);
			PacketStream.writeVarInt(buf, 1);
			PacketStream.sendPacket(buf, out);

			buf = ByteStreams.newDataOutput();
			PacketStream.writeVarInt(buf, 0x00);
			PacketStream.sendPacket(buf, out);

			PacketStream.readVarInt(in);
			int id = PacketStream.readVarInt(in);

			if(id == 0x00) {
				Map<String, String> map = new HashMap<>();
				String pings = PacketStream.getString(in);
				JSONObject json = new JSONObject(pings);
				String motd = stripColor(json.getString("description"));
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
				return map;
			}

			out.close();
			in.close();
			clientSocket.close();

		} catch(Exception e) {
			// nothing
		}

		return null;
	}

	public static String stripColor(final String input) {
		if(input == null) {
			return null;
		}

		return STRIP_COLOR_PATTERN.matcher(input).replaceAll("");
	}

}
