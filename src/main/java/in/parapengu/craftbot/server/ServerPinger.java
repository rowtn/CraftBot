package in.parapengu.craftbot.server;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import in.parapengu.craftbot.protocol.Packet;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ServerPinger {

	public static Map<String, String> ping(String server, int port) {
		Socket clientSocket;
		try {
			clientSocket = new Socket(server, port);
			DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
			DataInputStream in = new DataInputStream(clientSocket.getInputStream());
			ByteArrayDataOutput buf = ByteStreams.newDataOutput();
			Packet.writeVarInt(buf, 0);
			Packet.writeVarInt(buf, 4);
			Packet.writeString(buf, server);
			buf.writeShort(port);
			Packet.writeVarInt(buf, 1);
			Packet.sendPacket(buf, out);

			buf = ByteStreams.newDataOutput();
			Packet.writeVarInt(buf, 0);
			Packet.sendPacket(buf, out);

			Packet.readVarInt(in);
			int id = Packet.readVarInt(in);

			if(id == 0) {
				Map<String, String> map = new HashMap<>();
				String pings = Packet.getString(in);
				JSONObject json = new JSONObject(pings);
				map.put("motd", json.getString("description"));
				if(json.has("favicon")) {
					map.put("favicon", json.getString("favicon").replace("data:image/png;base64,", ""));
				}

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
				return map;
			}

			out.close();
			in.close();
			clientSocket.close();

		} catch(Exception e) {
			e.printStackTrace();
		}

		return null;
	}

}
