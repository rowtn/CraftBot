package in.parapengu.craftbot.bot;

import in.parapengu.craftbot.auth.YggdrasilAuthService;
import in.parapengu.craftbot.auth.YggdrasilSession;
import in.parapengu.craftbot.event.EventManager;
import in.parapengu.craftbot.event.bot.connection.BotConnectServerEvent;
import in.parapengu.craftbot.logging.Logger;
import in.parapengu.craftbot.logging.Logging;
import in.parapengu.craftbot.protocol.Packet;
import in.parapengu.craftbot.protocol.State;
import in.parapengu.craftbot.protocol.stream.BotPacketStream;
import in.parapengu.craftbot.protocol.stream.PacketInputStream;
import in.parapengu.craftbot.protocol.stream.PacketOutputStream;
import in.parapengu.craftbot.protocol.v4.ProtocolV4;
import in.parapengu.craftbot.protocol.v4.play.client.PacketPlayOutChatMessage;
import in.parapengu.craftbot.server.ServerPinger;
import in.parapengu.craftbot.world.World;

import java.math.BigInteger;
import java.net.Socket;
import java.util.*;

public class CraftBot {

	private static int id = 1;

	private Logger logger;
	private EventManager manager;

	private YggdrasilAuthService auth;
	private YggdrasilSession session;
	private String username;
	private String uuid;
	private UUID clientToken;
	private BigInteger accessToken;

	private State state;
	private Socket socket;
	private PacketOutputStream output;
	private PacketInputStream input;
	private BotPacketStream packetStream;

	private World world;

	public CraftBot(String account, String password, boolean authenticate) throws Exception {
		if(authenticate) {
			auth = new YggdrasilAuthService();
			session = auth.login(account, password);
			username = session.getSelectedProfile().getName();
			uuid = session.getSelectedProfile().getId();
			clientToken = session.getClientToken();
			accessToken = session.getAccessToken();
		} else {
			username = account.contains("@") ? "bot" + id : account;
			uuid = UUID.randomUUID().toString().replace("-", "");
			id++;
		}

		logger = Logging.getLogger(username, BotHandler.getHandler().getLogger());
		logger.info("Connected as " + username + " (" + uuid + ")");
		manager = new EventManager();

		state = State.STATUS;
	}

	public Logger getLogger() {
		return logger;
	}

	public EventManager getEventManager() {
		return manager;
	}

	public YggdrasilAuthService getAuth() {
		return auth;
	}

	public YggdrasilSession getSession() {
		return session;
	}

	public String getUsername() {
		return username;
	}

	public String getUUID() {
		return uuid;
	}

	public UUID getClientToken() {
		return clientToken;
	}

	public BigInteger getAccessToken() {
		return accessToken;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public PacketOutputStream getOutput() {
		return output;
	}

	public void setOutput(PacketOutputStream output) {
		this.output = output;
	}

	public PacketInputStream getInput() {
		return input;
	}

	public void setInput(PacketInputStream input) {
		this.input = input;
	}

	public BotPacketStream getPacketStream() {
		return packetStream;
	}

	public void setPacketStream(BotPacketStream packetStream) {
		this.packetStream = packetStream;
	}

	public World getWorld() {
		return world;
	}

	public void setWorld(World world) {
		this.world = world;
	}

	public void connect(String address, int port) {
		Map<String, String> ping = new ServerPinger().ping(address, port);
		if(ping == null) {
			logger.warning("Could not connect to ...");
			return;
		}

		if(ping.get("protocol").equals("4")) {
			manager.register(new ProtocolV4());
		} else {
			logger.warning("Unsupported Protocol version \"" + ping.get("protocol") + "\"");
			return;
		}

		manager.call(new BotConnectServerEvent(this, address, port));
	}

	public void sendMessage(String... messages) {
		for(String message : messages) sendMessage(message);
	}

	public void sendMessage(String message) {
		packetStream.sendPacket(new PacketPlayOutChatMessage(message));
	}

}
