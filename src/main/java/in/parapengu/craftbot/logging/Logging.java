package in.parapengu.craftbot.logging;

import static in.parapengu.craftbot.logging.LogLevel.*;

public class Logging {

	public static Logger getLogger(String name, Logger parent, LogLevel level) {
		Logger logger = new Logger(name, level);
		logger.setParent(parent);
		return logger;
	}

	public static Logger getLogger(Class<?> clazz, Logger parent, LogLevel level) {
		return getLogger(clazz.getSimpleName(), parent, level);
	}

	public static Logger getLogger(String name, Logger parent) {
		return getLogger(name, parent, INFO);
	}

	public static Logger getLogger(Class<?> clazz, Logger parent) {
		return getLogger(clazz.getSimpleName(), parent);
	}

	public static Logger getLogger(String name, LogLevel level) {
		return getLogger(name, null, level);
	}

	public static Logger getLogger(Class<?> clazz, LogLevel level) {
		return getLogger(clazz.getSimpleName(), level);
	}

	public static Logger getLogger(String name) {
		return getLogger(name, (Logger) null);
	}

	public static Logger getLogger(Class<?> clazz) {
		return getLogger(clazz.getSimpleName());
	}

	public static Logger getLogger() {
		return getLogger((String) null);
	}

}
