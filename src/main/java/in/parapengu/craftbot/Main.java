package in.parapengu.craftbot;

import com.google.common.collect.Lists;
import in.parapengu.commons.utils.file.TextFile;
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
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

	private static CraftBot bot;

	public static void main(String[] args) {
		OptionParser parser = new OptionParser();
		parser.acceptsAll(asList("h", "help"), "Show this help dialog.");
		OptionSpec<Boolean> logAppend = parser.acceptsAll(asList("log-append"), "Whether to append to the log file").withRequiredArg().ofType(Boolean.class).defaultsTo(true).describedAs("Log append");
		OptionSpec<File> logFile = parser.acceptsAll(asList("log-file", "log"), "Specifies the log file name").withRequiredArg().ofType(File.class).defaultsTo(new File("output.log")).describedAs("Log filename");
		OptionSpec<SimpleDateFormat> dateFormat = parser.acceptsAll(asList("d", "date-format"), "Format of the date to display in the console (for log entries)").withRequiredArg().ofType(SimpleDateFormat.class).defaultsTo(new SimpleDateFormat("HH:mm:ss")).describedAs("Log date format");

		OptionSet options;
		try {
			options = parser.parse(args);
		} catch (OptionException ex) {
			Logger.getLogger(Main.class.getName()).log(Level.SEVERE, ex.getLocalizedMessage());
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

			SimpleDateFormat format = dateFormat.value(options);

			try {
				System.setOut(new CustomPrintStream(new FileOutputStream(file), System.out, format));
			} catch(FileNotFoundException ex) {
				// never
			}
		}

		CraftBot bot = new CraftBot(options);

		try {
			ConsoleReader console = new ConsoleReader();
			console.setPrompt("> ");
			String line = null;
			while ((line = console.readLine()) != null) {
				bot.run(line.split(" "));
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
