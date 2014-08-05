package in.parapengu.craftbot;

import com.google.common.collect.Lists;
import in.parapengu.craftbot.bot.BotHandler;
import in.parapengu.craftbot.logging.Logger;
import in.parapengu.craftbot.logging.Logging;
import jline.TerminalFactory;
import jline.console.ConsoleReader;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

public class Main {

	private static BotHandler bot;

	public static void main(String[] args) {
		OptionParser parser = new OptionParser();
		parser.acceptsAll(asList("h", "help"), "Show this help dialog.").forHelp();
		OptionSpec<Boolean> logAppend = parser.acceptsAll(asList("log-append"), "Whether to append to the log file").withRequiredArg().ofType(Boolean.class).defaultsTo(true).describedAs("Log append");
		OptionSpec<Boolean> debug = parser.acceptsAll(asList("debug"), "Whether to enable debug mode").withRequiredArg().ofType(Boolean.class).defaultsTo(false).describedAs("Debug mode");
		OptionSpec<File> logFile = parser.acceptsAll(asList("log-file", "log"), "Specifies the log file name").withRequiredArg().ofType(File.class).defaultsTo(new File("output.log")).describedAs("Log filename");
		OptionSpec<SimpleDateFormat> dateFormat = parser.acceptsAll(asList("d", "date-format"), "Format of the date to display in the console (for log entries)").withRequiredArg().ofType(SimpleDateFormat.class).defaultsTo(new SimpleDateFormat("HH:mm:ss")).describedAs("Log date format");
		OptionSpec<File> accountsFile = parser.acceptsAll(asList("a", "accounts"), "Specifies the accounts file").withRequiredArg().ofType(File.class).defaultsTo(new File("accounts.json")).describedAs("Accounts filename");
		OptionSpec<File> pluginsFolder = parser.acceptsAll(asList("p", "plugins"), "Specifies the plugins folder").withRequiredArg().ofType(File.class).defaultsTo(new File("plugins")).describedAs("Plugins folder name");
		OptionSpec<Boolean> offline = parser.acceptsAll(asList("o", "offline"), "Whether to enable offline mode").withRequiredArg().ofType(Boolean.class).defaultsTo(false).describedAs("Offline mode");
		OptionSpec<Integer> bots = parser.acceptsAll(asList("b", "bots"), "How many bots to load in (requires offline mode)").withRequiredArg().ofType(Integer.class).describedAs("Bot count");

		OptionSet options;
		try {
			options = parser.parse(args);
		} catch(OptionException ex) {
			Logging.getLogger(Main.class.getName()).severe(ex.getLocalizedMessage());
			return;
		}

		if(options.has("h")) {
			try {
				parser.printHelpOn(System.out);
			} catch(IOException ex) {
				Logging.getLogger().log("Could not display help: ", ex);
			}

			return;
		}

		boolean append = logAppend.value(options);
		if(append) {
			File file = logFile.value(options);
			try {
				if(!file.exists()) {
					if(!file.createNewFile()) {
						throw new IOException("Could not create log file");
					}
				} else if(!file.isFile()) {
					throw new IOException("Could not create new log file because it was a directory");
				}
			} catch(IOException ex) {
				ex.printStackTrace();
				return;
			}

			try {
				System.setOut(new CustomPrintStream(new FileOutputStream(file), System.out));
			} catch(FileNotFoundException ex) {
				// never
			}
		}

		File accounts = accountsFile.value(options);
		if(accounts.exists() && !accounts.isFile()) {
			throw new IllegalArgumentException(accounts.getPath() + " exists and is not a file");
		}

		File plugins = pluginsFolder.value(options);
		if(plugins.exists() && !plugins.isDirectory()) {
			throw new IllegalArgumentException(plugins.getPath() + " exists and is not a folder");
		}

		SimpleDateFormat format = dateFormat.value(options);
		BotHandler bot = new BotHandler(options, format);

		try {
			ConsoleReader console = new ConsoleReader();
			// console.setPrompt("> ");
			console.setCompletionHandler((reader, candidates, position) -> {
				bot.getLogger().info(candidates);
				return false;
			});
			String line;
			while((line = console.readLine()) != null) {
				bot.command(line.split(" "));
			}
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			try {
				TerminalFactory.get().restore();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static List<String> asList(String... params) {
		return Lists.newArrayList(params);
	}

}
