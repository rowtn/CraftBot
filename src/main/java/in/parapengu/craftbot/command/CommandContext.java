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

	public boolean getBoolean(int index) {
		String arg = arguments[index].toLowerCase();
		if(arg.equals("1") || arg.equals("true") || arg.equals("on")) {
			return true;
		} else if(arg.equals("0") || arg.equals("false") || arg.equals("off")) {
			return false;
		}

		throw new IllegalArgumentException("\"" + arg + "\" is not a valid boolean");
	}

	public int getInteger(int index) {
		return Integer.parseInt(arguments[index]);
	}

	public double getDouble(int index) {
		return Double.parseDouble(arguments[index]);
	}

	public float getFloat(int index) {
		return Float.parseFloat(arguments[index]);
	}

}
