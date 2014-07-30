package in.parapengu.craftbot.event;

public interface Cancellable {

	public boolean isCancelled();

	public void setCancelled(boolean cancelled);

}
