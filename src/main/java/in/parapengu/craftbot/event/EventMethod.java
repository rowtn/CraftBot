package in.parapengu.craftbot.event;

import in.parapengu.craftbot.bot.BotHandler;

import java.lang.reflect.Method;

public class EventMethod {

	private Listener listener;
	private EventHandler handler;
	private Method method;
	private Class<? extends Event> event;

	public EventMethod(Listener listener, EventHandler handler, Method method, Class<? extends Event> event) {
		this.listener = listener;
		this.handler = handler;
		this.method = method;
		this.event = event;
	}

	public Listener getListener() {
		return listener;
	}

	public EventHandler getHandler() {
		return handler;
	}

	public Method getMethod() {
		return method;
	}

	public Class<? extends Event> getEvent() {
		return event;
	}

	public void handle(Event event) {
		if(handler.ignoreCancelled() && event instanceof Cancellable && ((Cancellable) event).isCancelled()) {
			return;
		}

		if((handler.classMatches() && this.event.equals(event.getClass())) || (!handler.classMatches() && this.event.isAssignableFrom(event.getClass()))) {
			try {
				method.invoke(listener, event);
			} catch(Exception ex) {
				BotHandler.getHandler().getLogger().log("Could not invoke " + method.getName() + "(" + this.event.getSimpleName() + ") from " + listener.getClass().getSimpleName(), ex);
			}
		}
	}

}
