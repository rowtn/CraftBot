package in.parapengu.craftbot;

import com.google.common.collect.Lists;
import jdk.nashorn.internal.runtime.Logging;
import joptsimple.OptionSet;

import java.util.logging.Logger;

public class CraftBot {

	private Logger logger;

	public CraftBot(OptionSet options) {
		this.logger = Logging.getLogger("CraftBot");
	}

	public Logger getLogger() {
		return logger;
	}

	public void run(String[] args) {
		logger.info("Handled command '" + args[0] + "': " + Lists.newArrayList(args));
	}

}
