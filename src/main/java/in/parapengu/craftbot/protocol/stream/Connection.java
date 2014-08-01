package in.parapengu.craftbot.protocol.stream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

public class Connection {

	private String host;
	private int port;
	private Socket socket;
	private PacketInputStream inputStream;
	private PacketOutputStream outputStream;

	public Connection(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public Connection(Socket socket) {
		if(!socket.isConnected())
			throw new IllegalArgumentException("Socket must be open");
		try {
			InetAddress address = socket.getInetAddress();
			host = address.getHostAddress();
			port = socket.getPort();
			this.socket = socket;
			createStreams();
		} catch(IOException exception) {
			exception.printStackTrace();
		}
	}

	public void connect() throws IOException {
		if(isConnected())
			return;

		socket = new Socket();
		try {
			socket.setSoTimeout(20*1000);
		} catch(SocketException ignored) {}
		socket.connect(new InetSocketAddress(host, port), 3000);
		createStreams();
	}

	private void createStreams() throws IOException {
		InputStream in = socket.getInputStream();
		inputStream = new PacketInputStream(in);
		OutputStream out = socket.getOutputStream();
		outputStream = new PacketOutputStream(out);
	}

	public void disconnect() {
		if(isConnected()) {
			try {
				socket.close();
			} catch(IOException e) {
			}
			socket = null;
			inputStream = null;
			outputStream = null;
		}
	}

	public boolean isConnected() {
		return socket != null && socket.isConnected() && !socket.isClosed();
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public Socket getSocket() {
		return socket;
	}

	public PacketInputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(PacketInputStream inputStream) {
		this.inputStream = inputStream;
	}

	public PacketOutputStream getOutputStream() {
		return outputStream;
	}

	public void setOutputStream(PacketOutputStream outputStream) {
		this.outputStream = outputStream;
	}
}
