package in.parapengu.craftbot;

import com.google.common.collect.Lists;
import in.parapengu.craftbot.logging.Logger;
import in.parapengu.craftbot.logging.Logging;
import joptsimple.OptionSet;

import java.text.SimpleDateFormat;

public class CraftBot {

	private Logger logger;

	public CraftBot(OptionSet options, SimpleDateFormat format) {
		this.logger = Logging.getLogger(getClass()).setFormat(format);
	}

	public Logger getLogger() {
		return logger;
	}

	public void run(String[] args) {
		logger.info("Handled command '" + args[0] + "': " + Lists.newArrayList(args));
	}

}
