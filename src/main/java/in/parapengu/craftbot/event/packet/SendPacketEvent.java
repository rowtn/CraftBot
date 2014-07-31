package in.parapengu.craftbot.event.packet;

import in.parapengu.craftbot.event.Cancellable;
import in.parapengu.craftbot.protocol.Packet;

public class SendPacketEvent extends PacketEvent implements Cancellable {

	private boolean cancelled;

	public SendPacketEvent(Packet packet) {
		super(packet);
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

}
