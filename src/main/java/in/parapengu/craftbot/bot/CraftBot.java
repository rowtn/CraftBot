package in.parapengu.craftbot.bot;

import in.parapengu.craftbot.event.EventManager;
import in.parapengu.craftbot.logging.Logger;
import in.parapengu.craftbot.logging.Logging;
import in.parapengu.craftbot.protocol.stream.PacketInputStream;
import in.parapengu.craftbot.protocol.stream.PacketOutputStream;
import org.json.JSONException;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class CraftBot {

	private Logger logger;
	private BotAuthenticator authenticator;
	private EventManager manager;

	private String username;
	private String uuid;
	private String clientToken;
	private String accessToken;

	private Socket socket;
	private PacketOutputStream out;
	private PacketInputStream in;

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

	public String connect(String address, int port) {
		socket = new Socket();
		try {
			socket.connect(new InetSocketAddress(address, port), 5000);
			out = new PacketOutputStream(socket.getOutputStream());
			in = new PacketInputStream(socket.getInputStream());

		} catch(IOException ex) {
			return ex.getMessage();
		}

		return null;
	}

}
