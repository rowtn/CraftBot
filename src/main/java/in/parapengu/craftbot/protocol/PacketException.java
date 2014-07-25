package in.parapengu.craftbot.protocol;

public class PacketException extends RuntimeException {

	private static final long serialVersionUID = 2234835951103883703L;

	private Class<? extends Packet> packet;
	private Destination destination;

	public PacketException(String message, Class<? extends Packet> packet, Destination destination) {
		super("Error while " + (destination == Destination.SERVER ? "sending" : "receiving") + " packet data for " + packet.getSimpleName() + ": " + message);
		this.packet = packet;
		this.destination = destination;
	}

	public Class<? extends Packet> getPacket() {
		return packet;
	}

	public Destination getDestination() {
		return destination;
	}

}
