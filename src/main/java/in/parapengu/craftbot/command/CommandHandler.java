package in.parapengu.craftbot.command;

import java.util.ArrayList;
import java.util.List;

public abstract class CommandHandler {

	public List<SubCommandHandler> subCommands = new ArrayList<>();

	public abstract String[] getAliases();

	public abstract String getDescription();

	public abstract String getHelp();

	public abstract boolean execute(CommandContext context);

	public void run(CommandContext context) {
		if(subCommands.size() > 0) {
			// magic
		}

		boolean success = execute(context);
		if(!success) {

		}
	}



}
