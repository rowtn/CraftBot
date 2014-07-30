package in.parapengu.craftbot.protocol.v4;

import in.parapengu.craftbot.bot.CraftBot;
import in.parapengu.craftbot.event.EventHandler;
import in.parapengu.craftbot.event.Listener;
import in.parapengu.craftbot.event.bot.connection.BotConnectServerEvent;
import in.parapengu.craftbot.protocol.stream.PacketInputStream;
import in.parapengu.craftbot.protocol.stream.PacketOutputStream;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ProtocolV4 implements Listener {

	@EventHandler
	public void onConnect(BotConnectServerEvent event) {
		event.getBot().getLogger().info("Testing connection to " + event.getAddress() + ":" + event.getPort());

		CraftBot bot = event.getBot();
		String address = event.getAddress();
		int port = event.getPort();

		Socket socket;
		PacketOutputStream output;
		PacketInputStream input;
		try {
			socket = new Socket();
			socket.connect(new InetSocketAddress(address, port), 5000);
			output = new PacketOutputStream(socket.getOutputStream());
			input = new PacketInputStream(socket.getInputStream());
			bot.setSocket(socket);
			bot.setOutput(output);
			bot.setInput(input);

			// output.sendPacket(new );
		} catch(IOException ex) {
			bot.getLogger().log("Could not connect to " + address + (port != 25565 ? ":" + port : "") + ": ", ex);
			return;
		}
	}

}
