package in.parapengu.craftbot.command;

public class CommandContext {

	private String label;
	private String[] arguments;

	public CommandContext(String label) {
		this.label = label;
		this.arguments = new String[0];
	}

	public String getLabel() {
		return label;
	}

	public String[] getArguments() {
		return arguments;
	}

	public CommandContext setArguments(String[] args) {
		this.arguments = args;
		return this;
	}

	public int getInteger(int arg) {
		return Integer.parseInt(arguments[arg]);
	}

	public double getDouble(int arg) {
		return Double.parseDouble(arguments[arg]);
	}

	public float getFloat(int arg) {
		return Float.parseFloat(arguments[arg]);
	}

}
