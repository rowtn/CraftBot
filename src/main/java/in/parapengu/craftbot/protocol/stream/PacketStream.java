package in.parapengu.craftbot.protocol.stream;

import in.parapengu.commons.utils.OtherUtil;
import in.parapengu.craftbot.auth.EncryptionUtil;
import in.parapengu.craftbot.bot.BotHandler;
import in.parapengu.craftbot.event.EventManager;
import in.parapengu.craftbot.event.packet.ReceivePacketEvent;
import in.parapengu.craftbot.logging.Logger;
import in.parapengu.craftbot.protocol.Packet;
import in.parapengu.craftbot.protocol.State;
import in.parapengu.craftbot.protocol.v4.login.PacketLoginInEncryptionRequest;

import javax.crypto.SecretKey;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class PacketStream {

	private Map<State, Map<Integer, Class<? extends Packet>>> packets;
	private Connection connection;

	private Queue<Packet> packetProcessQueue;
	private Queue<Packet> packetWriteQueue;

	private AtomicBoolean pauseReading, pauseWriting;

	private ReadTask readTask;
	private WriteTask writeTask;

	private SecretKey sharedKey;
	private boolean encrypting, decrypting;

	public PacketStream(Map<State, Map<Integer, Class<? extends Packet>>> packets, String address, int port) {
		this.packets = packets;
		this.connection = new Connection(address, port);
		this.packetProcessQueue = new ArrayDeque<>();
		this.packetWriteQueue = new ArrayDeque<>();
		this.pauseReading = new AtomicBoolean();
		this.pauseWriting = new AtomicBoolean();
	}

	public abstract State getState();

	public Connection getConnection() {
		return connection;
	}

	public boolean isConnected() {
		return connection.isConnected() && readTask != null && !readTask.future.isDone() && writeTask != null && !writeTask.future.isDone();
	}

	public Queue<Packet> getPacketProcessQueue() {
		return packetProcessQueue;
	}

	public Queue<Packet> getPacketWriteQueue() {
		return packetWriteQueue;
	}

	public AtomicBoolean getPauseReading() {
		return pauseReading;
	}

	public AtomicBoolean getPauseWriting() {
		return pauseWriting;
	}

	public ReadTask getReadTask() {
		return readTask;
	}

	public WriteTask getWriteTask() {
		return writeTask;
	}

	public SecretKey getSharedKey() {
		return sharedKey;
	}

	public void setSharedKey(SecretKey sharedKey) {
		this.sharedKey = sharedKey;
	}

	public boolean isEncrypting() {
		return encrypting;
	}

	public void setEncrypting(boolean encrypting) {
		this.encrypting = encrypting;
		if(encrypting) {
			enableEncryption();
		}
	}

	public boolean isDecrypting() {
		return decrypting;
	}

	public void setDecrypting(boolean decrypting) {
		this.decrypting = decrypting;
		if(decrypting) {
			enableDecryption();
		}
	}

	public void sendPacket(Packet packet) {
		packetWriteQueue.offer(packet);
		// packetWriteQueue.notifyAll();
	}

	public abstract Logger getLogger();

	public abstract EventManager getManager();

	public boolean validate(int id) {
		getLogger().debug("Attempting to find a " + getState().name() + " packet with the id #" + id);
		Class<? extends Packet> clazz = packets.get(getState()).get(id);
		if(clazz == null) {
			disconnect("Could not find packet with id #" + id);
			return false;
		}

		return true;
	}

	public void start() {
		try {
			connect();
		} catch(Exception ex) {
			getLogger().log("Could not connect to server: ", ex);
			return;
		}

		while(connection.isConnected()) {
			process();
		}
	}

	public synchronized void process() {
		if(packetProcessQueue.size() == 0)
			return;

		Packet[] packets = packetProcessQueue.toArray(new Packet[packetProcessQueue.size()]);
		packetProcessQueue.clear();
		for(Packet packet : packets) {
			getManager().call(new ReceivePacketEvent(packet));
		}
	}

	public void connect() throws IOException {
		if(connection.isConnected())
			return;

		connection.connect();
		ExecutorService service = Executors.newCachedThreadPool();
		readTask = new ReadTask();
		writeTask = new WriteTask();
		readTask.future = service.submit(readTask);
		writeTask.future = service.submit(writeTask);
	}

	public void disconnect(String reason) {
		if(!connection.isConnected() && readTask.future == null && writeTask.future == null) {
			return;
		}

		if(readTask != null && readTask.future != null) {
			readTask.future.cancel(true);
		}

		if(writeTask != null && writeTask.future != null) {
			writeTask.future.cancel(true);
		}

		readTask = null;
		writeTask = null;
		sharedKey = null;
		encrypting = decrypting = false;
		connection.disconnect();

		getLogger().warning("Disconnected from " + connection.getHost() + (connection.getPort() != 25565 ? ":" + connection.getPort() : "") + (reason != null ? " because: " + reason : ""));
		if(reason != null) {
			// getManager().call(new DisconnectEvent(reason));
		}
	}

	public void handle(Packet packet) {
		getManager().call(new ReceivePacketEvent(packet));
	}

	public void enableEncryption() {
		if(!isConnected())
			throw new IllegalStateException("Not connected");
		if(encrypting)
			throw new IllegalStateException("Already encrypting");
		if(sharedKey == null)
			throw new IllegalStateException("Shared key not set");
		if(!pauseWriting.get() && (writeTask.thread == null || writeTask.thread != Thread.currentThread()))
			throw new IllegalStateException("Must be called from write thread");
		connection.setOutputStream(new PacketOutputStream(EncryptionUtil.encryptOutputStream(connection.getOutputStream(), sharedKey)));
		encrypting = true;
	}

	public void enableDecryption() {
		if(!isConnected())
			throw new IllegalStateException("Not connected");
		if(decrypting)
			throw new IllegalStateException("Already decrypting");
		if(sharedKey == null)
			throw new IllegalStateException("Shared key not set");
		if(!pauseReading.get() && (readTask.thread == null || readTask.thread != Thread.currentThread()))
			throw new IllegalStateException("Must be called from read thread");
		connection.setInputStream(new PacketInputStream(EncryptionUtil.decryptInputStream(connection.getInputStream(), sharedKey)));
		decrypting = true;
	}

	public void pauseReading() {
		pauseReading.set(true);
		// pauseReading.notifyAll();
	}

	public void pauseWriting() {
		pauseWriting.set(true);
		// pauseWriting.notifyAll();
		// packetWriteQueue.notifyAll();
	}

	public void resumeReading() {
		pauseReading.set(false);
		// pauseReading.notifyAll();
	}

	public void resumeWriting() {
		pauseWriting.set(false);
		// pauseWriting.notifyAll();
		// packetWriteQueue.notifyAll();
	}

	public boolean isReadingPaused() {
		return pauseReading.get();
	}

	public boolean isWritingPaused() {
		return pauseWriting.get();
	}

	private final class ReadTask implements Runnable {
		private Future<?> future;
		private Thread thread;

		@Override
		public void run() {
			thread = Thread.currentThread();
			try {
				Thread.sleep(500);
				while(isConnected()) {
					try {
						synchronized(pauseReading) {
							if(pauseReading.get()) {
								pauseReading.wait(500);
								continue;
							}
						}
					} catch(InterruptedException exception) {
						if(future == null || future.isCancelled())
							break;
						continue;
					}

					PacketInputStream in = connection.getInputStream();
					int length = in.readVarInt();
					int id = in.readVarInt();
					Class<? extends Packet> clazz = packets.get(getState()).get(id);
					if(!validate(id)) {
						throw new IOException("Unknown " + getState().name() + " packet #" + id);
					}
					Packet packet = clazz.newInstance();

					length = length - connection.getInputStream().getVarIntLength(id);
					final byte[] data = new byte[length];
					in.readFully(data);

					in = new PacketInputStream(new ByteArrayInputStream(data) {
						@Override
						public synchronized int read() {
							if(pos == count)
								System.out.println("WARNING: Packet 0x" + Integer.toHexString(id).toUpperCase() + " read past length of " + data.length);
							return super.read();
						}

						@Override
						public void close() throws IOException {
							if(pos != count)
								System.out.println("WARNING: Packet 0x" + Integer.toHexString(id).toUpperCase() + " read less than " + data.length + " (" + pos + ")");
						}
					});
					packet.build(in);
					in.close();

					handle(packet);
					packetProcessQueue.offer(packet);
				}
			} catch(Throwable exception) {
				exception.printStackTrace();
				disconnect("Read error: " + exception);
			}
		}
	}

	private final class WriteTask implements Runnable {
		private Future<?> future;
		private Thread thread;

		@Override
		public void run() {
			thread = Thread.currentThread();
			try {
				Thread.sleep(500);
				while(isConnected()) {
					Packet packet = null;
					try {
						synchronized(pauseWriting) {
							if(pauseWriting.get()) {
								pauseWriting.wait(500);
								continue;
							}
						}

						synchronized(packetWriteQueue) {
							if(!packetWriteQueue.isEmpty())
								packet = packetWriteQueue.poll();
							else
								packetWriteQueue.wait(500);
						}
					} catch(InterruptedException exception) {
						if(future == null || future.isCancelled())
							break;
						continue;
					}

					if(packet != null) {
						PacketOutputStream data = new PacketOutputStream(new ByteArrayOutputStream());
						data.writeVarInt(packet.getId()); // write the packet id
						packet.send(data); // write the packet data
						PacketOutputStream send = new PacketOutputStream(new ByteArrayOutputStream()); // create a new final byte array
						ByteArrayOutputStream dataBuf = (ByteArrayOutputStream) data.getOutput();
						ByteArrayOutputStream sendBuf = (ByteArrayOutputStream) send.getOutput();

						send.writeVarInt(dataBuf.toByteArray().length); // write the length of the buffer
						for(byte b : dataBuf.toByteArray()) {
							send.write(b); // write the array of bytes to the final byte array
						}

						connection.getOutputStream().write(sendBuf.toByteArray()); // write the final array to the data output stream
						connection.getOutputStream().flush(); // flush the output and send it on it's way

						List<Byte> list = new ArrayList<>();
						for(byte b : sendBuf.toByteArray()) {
							list.add(b);
						}
						BotHandler.getHandler().getLogger().debug("Wrote out bytes for " + data.getClass().getSimpleName() + ": " + OtherUtil.listToEnglishCompound(list, "", ""));
					}
				}
			} catch(Throwable exception) {
				exception.printStackTrace();
				disconnect("Write error: " + exception);
			}
		}
	}

}
