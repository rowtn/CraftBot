package in.parapengu.craftbot;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CustomPrintStream extends PrintStream {
	private final PrintStream second;
	private final SimpleDateFormat format;

	public CustomPrintStream(OutputStream main, PrintStream second, SimpleDateFormat format) {
		super(main);
		this.second = second;
		this.format = format;
	}

	/**
	 * Closes the main stream.
	 * The second stream is just flushed but <b>not</b> closed.
	 * @see java.io.PrintStream#close()
	 */
	@Override
	public void close() {
		// just for documentation
		super.close();
	}

	@Override
	public void flush() {
		super.flush();
		second.flush();
	}

	@Override
	public void write(byte[] buf, int off, int len) {
		super.write(buf, off, len);
		second.write(buf, off, len);
	}

	@Override
	public void write(int b) {
		super.write(b);
		second.write(b);
	}

	@Override
	public void write(byte[] b) throws IOException {
		super.write(b);
		second.write(b);
	}

	@Override
	public void write(String s) {
		s += "[" + format.format(new Date()) + "] ";
		super.print(s);
	}

}
