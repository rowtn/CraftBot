package in.parapengu.craftbot.bot;

import org.apache.commons.lang.Validate;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Attribute;

import java.util.regex.Pattern;

public enum ChatColor {

	BLACK('0', Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.BLACK).boldOff().toString()),
	DARK_BLUE('1', Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.BLUE).boldOff().toString()),
	DARK_GREEN('2', Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.GREEN).boldOff().toString()),
	DARK_AQUA('3', Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.CYAN).boldOff().toString()),
	DARK_RED('4', Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.RED).boldOff().toString()),
	DARK_PURPLE('5', Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.MAGENTA).boldOff().toString()),
	GOLD('6', Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.YELLOW).boldOff().toString()),
	GRAY('7', Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.WHITE).boldOff().toString()),
	DARK_GRAY('8', Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.BLACK).bold().toString()),
	BLUE('9', Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.BLUE).bold().toString()),
	GREEN('a', Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.GREEN).bold().toString()),
	AQUA('b', Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.CYAN).bold().toString()),
	RED('c', Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.RED).bold().toString()),
	LIGHT_PURPLE('d', Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.MAGENTA).bold().toString()),
	YELLOW('e', Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.YELLOW).bold().toString()),
	WHITE('f', Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.WHITE).bold().toString()),
	MAGIC('k', Ansi.ansi().a(Attribute.BLINK_SLOW).toString()),
	BOLD('l', Ansi.ansi().a(Attribute.UNDERLINE_DOUBLE).toString()),
	STRIKETHROUGH('m', Ansi.ansi().a(Attribute.STRIKETHROUGH_ON).toString()),
	UNDERLINE('n', Ansi.ansi().a(Attribute.UNDERLINE).toString()),
	ITALIC('o', Ansi.ansi().a(Attribute.ITALIC).toString()),
	RESET('r', Ansi.ansi().a(Attribute.RESET).toString());

	public static final char COLOR_CHAR = '\u00A7';
	private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + String.valueOf(COLOR_CHAR) + "[0-9A-FK-OR]");

	private char code;
	private String console;
	private String toString;

	ChatColor(char code, String string) {
		this.code = code;
		this.console = string;
		this.toString = new String(new char[] {COLOR_CHAR, code});
	}

	public String toConsole() {
		return console;
	}

	@Override
	public String toString() {
		return toString;
	}

	public static ChatColor getByChar(char code) {
		for(ChatColor color : values()) {
			if(color.code == code) {
				return color;
			}
		}

		return null;
	}

	public static ChatColor getByChar(String code) {
		Validate.notNull(code, "Code cannot be null");
		Validate.isTrue(code.length() > 0, "Code must have at least one char");

		return getByChar(code.charAt(0));
	}

	public static String stripColor(final String input) {
		if (input == null) {
			return null;
		}

		return STRIP_COLOR_PATTERN.matcher(input).replaceAll("");
	}

	public static String getConsoleString(String input) {
		for(ChatColor color : values()) {
			input = input.replace(color.toString(), color.toConsole());
		}

		return input;
	}

}
