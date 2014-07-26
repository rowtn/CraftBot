package in.parapengu.craftbot.command;

import in.parapengu.craftbot.bot.BotHandler;
import in.parapengu.craftbot.logging.Logger;

import java.util.ArrayList;
import java.util.List;

public abstract class CommandHandler {

	public List<SubCommandHandler> subCommands = new ArrayList<>();

	public abstract String[] getAliases();

	public abstract String getDescription();

	public abstract String getHelp();

	public abstract boolean execute(CommandContext context) throws CommandException;

	public void run(CommandContext context) {
		if(subCommands.size() > 0) {
			// magic
		}

		Logger logger = BotHandler.getHandler().getLogger();
		try {
			boolean success = execute(context);
			if(!success) {
				String help = getHelp();
				logger.warning("Incorrect command usage:");
				logger.warning(context.getLabel() + (help != null ? " " + help : ""));
			}
		} catch(CommandException ex) {
			logger.warning(ex.getMessage());
		} catch(NumberFormatException ex) {
			logger.warning("Number expected, string supplied...");
		} catch(IllegalArgumentException ex) {
			boolean debug = logger.isDebug();
			logger.setDebug(false);
			logger.log((String) null, ex);
			logger.setDebug(debug);
		} catch(Exception ex) {
			logger.log((String) null, ex);
		}
	}


}
