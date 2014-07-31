package in.parapengu.craftbot.protocol.v4;

import in.parapengu.craftbot.bot.CraftBot;
import in.parapengu.craftbot.event.EventHandler;
import in.parapengu.craftbot.event.Listener;
import in.parapengu.craftbot.event.bot.connection.BotConnectServerEvent;
import in.parapengu.craftbot.protocol.Packet;
import in.parapengu.craftbot.protocol.Protocol;
import in.parapengu.craftbot.protocol.State;
import in.parapengu.craftbot.protocol.stream.BotPacketStream;
import in.parapengu.craftbot.protocol.stream.PacketInputStream;
import in.parapengu.craftbot.protocol.stream.PacketOutputStream;
import in.parapengu.craftbot.protocol.v4.handshaking.PacketHandshakeOutStatus;
import in.parapengu.craftbot.protocol.v4.play.PacketPlayInKeepAlive;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ProtocolV4 extends Protocol implements Listener {

	private Map<State, Map<Integer, Class<? extends Packet>>> packets;

	public ProtocolV4() {
		super(4, "1.7.2", "1.7.5");

		packets = new HashMap<>();

		Map<Integer, Class<? extends Packet>> status = new HashMap<>();
		status.put(0x00, PacketPlayInKeepAlive.class);
		packets.put(State.STATUS, status);

		Map<Integer, Class<? extends Packet>> login = new HashMap<>();
		login.put(0x00, PacketPlayInKeepAlive.class);
		packets.put(State.LOGIN, login);

		Map<Integer, Class<? extends Packet>> play = new HashMap<>();
		login.put(0x00, PacketPlayInKeepAlive.class);
		packets.put(State.PLAY, play);
	}

	@EventHandler
	public void onConnect(BotConnectServerEvent event) {
		event.getBot().getLogger().info("Testing connection to " + event.getAddress() + ":" + event.getPort());

		CraftBot bot = event.getBot();
		String address = event.getAddress();
		int port = event.getPort();

		try {
			socket = new Socket();
			socket.connect(new InetSocketAddress(address, port), 5000);
			output = new PacketOutputStream(socket.getOutputStream());
			input = new PacketInputStream(socket.getInputStream());
			bot.setSocket(socket);
			bot.setOutput(output);
			bot.setInput(input);
		} catch(IOException ex) {
			bot.getLogger().log("Could not connect to " + address + (port != 25565 ? ":" + port : "") + ": ", ex);
			return;
		}

		BotPacketStream stream = new BotPacketStream(packets, socket, output, input, bot);
		new Thread(stream::start).start();

		bot.setState(State.LOGIN);
		stream.sendPacket(new PacketHandshakeOutStatus(this, address, port, State.LOGIN));
	}

}
