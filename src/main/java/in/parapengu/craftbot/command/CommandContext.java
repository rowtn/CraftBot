package in.parapengu.craftbot.command;

public class CommandContext {

	private String[] args;

	public CommandContext() {
		this.args = new String[0];
	}

	public CommandContext setArguments(String[] args) {
		this.args = args;
		return this;
	}

}
