package in.parapengu.craftbot.event;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PrioritisedMethods {

	private Map<EventPriority, List<EventMethod>> methods;

	public PrioritisedMethods() {
		methods = new HashMap<>();
		for(EventPriority priority : EventPriority.values()) {
			methods.put(priority, new ArrayList<>());
		}
	}

	public List<EventMethod> getMethods(EventPriority priority) {
		return methods.get(priority);
	}

	public List<EventMethod> getOrderedMethods() {
		List<EventMethod> methods = new ArrayList<>();
		for(EventPriority priority : EventPriority.values()) {
			methods.addAll(getMethods(priority));
		}

		return methods;
	}

	public void add(Listener listener, EventHandler handler, Method method, Class<? extends Event> event) {
		methods.get(handler.value()).add(new EventMethod(listener, handler, method, event));
	}

	public void remove(Listener listener) {
		for(List<EventMethod> methods : this.methods.values()) {
			List<EventMethod> remove = methods.stream().filter(method -> method.getListener().equals(listener)).collect(Collectors.toList());
			methods.removeAll(remove);
		}
	}

	public void remove(Class<? extends Event> event, boolean assignable) {
		for(List<EventMethod> methods : this.methods.values()) {
			List<EventMethod> remove = methods.stream().filter(method -> (assignable && event.isAssignableFrom(method.getEvent())) || (!assignable && event.equals(method.getEvent()))).collect(Collectors.toList());
			methods.removeAll(remove);
		}
	}

}
