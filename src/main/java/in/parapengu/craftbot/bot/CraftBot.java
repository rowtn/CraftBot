package in.parapengu.craftbot.bot;

import in.parapengu.craftbot.logging.Logger;
import in.parapengu.craftbot.logging.Logging;

public class CraftBot {

	private Logger logger;
	private BotAuthenticator authenticator;

	private String username;
	private String uuid;

	public CraftBot(String account, String password) {
		authenticator = new BotAuthenticator(account, password);
		if(!authenticator.authenticate()) {
			throw new IllegalArgumentException("Username/Password combination was invalid!");
		}

		username = authenticator.getProfileName();
		uuid = authenticator.getProfileID();

		logger = Logging.getLogger(username, BotHandler.getHandler().getLogger());
		logger.info("Connected as " + username + " (" + uuid + ")");
	}

	public Logger getLogger() {
		return logger;
	}

	public String getUsername() {
		return username;
	}

	public String getUUID() {
		return uuid;
	}

}
