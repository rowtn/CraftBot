package in.parapengu.craftbot.bot;

import in.parapengu.craftbot.event.EventManager;
import in.parapengu.craftbot.event.bot.connection.BotConnectServerEvent;
import in.parapengu.craftbot.logging.Logger;
import in.parapengu.craftbot.logging.Logging;
import in.parapengu.craftbot.protocol.Packet;
import in.parapengu.craftbot.protocol.State;
import in.parapengu.craftbot.protocol.stream.PacketInputStream;
import in.parapengu.craftbot.protocol.stream.PacketOutputStream;
import in.parapengu.craftbot.protocol.v4.ProtocolV4;
import in.parapengu.craftbot.server.ServerPinger;
import org.json.JSONException;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;

public class CraftBot {

	private Logger logger;
	private BotAuthenticator authenticator;
	private EventManager manager;

	private String username;
	private String uuid;
	private String clientToken;
	private String accessToken;

	private State state;
	private Socket socket;
	private PacketOutputStream output;
	private PacketInputStream input;

	public CraftBot(String account, String password) throws IOException, JSONException {
		authenticator = new BotAuthenticator(account, password);
		if(!authenticator.authenticate()) {
			throw new IllegalArgumentException("Username/Password combination was invalid!");
		}

		username = authenticator.getProfileName();
		uuid = authenticator.getProfileID();
		clientToken = authenticator.getClientToken();
		accessToken = authenticator.getAccessToken();

		logger = Logging.getLogger(username, BotHandler.getHandler().getLogger());
		logger.info("Connected as " + username + " (" + uuid + ")");
		manager = new EventManager();

		state = State.STATUS;
	}

	public Logger getLogger() {
		return logger;
	}

	public BotAuthenticator getAuthenticator() {
		return authenticator;
	}

	public EventManager getEventManager() {
		return manager;
	}

	public String getUsername() {
		return username;
	}

	public String getUUID() {
		return uuid;
	}

	public String getClientToken() {
		return clientToken;
	}

	public String getAccessToken() {
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

}
