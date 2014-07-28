package in.parapengu.craftbot.bot;

import in.parapengu.craftbot.logging.Logger;
import in.parapengu.craftbot.logging.Logging;
import org.json.JSONException;

import java.io.IOException;

public class CraftBot {

	private Logger logger;
	private BotAuthenticator authenticator;

	private String username;
	private String uuid;
	private String clientToken;
	private String accessToken;

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
	}

	public Logger getLogger() {
		return logger;
	}

	public BotAuthenticator getAuthenticator() {
		return authenticator;
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

}
