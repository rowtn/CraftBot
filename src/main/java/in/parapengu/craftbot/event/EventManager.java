package in.parapengu.craftbot.event;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class EventManager {

	private Map<Class<? extends Event>, PrioritisedMethods> events;

	public EventManager() {
		events = new HashMap<>();
	}

	public void register(Listener listener) {
		Class<?> clazz = listener.getClass();

		Method[] methods = clazz.getMethods();
		for(Method method : methods) {
			if(!method.isAnnotationPresent(EventHandler.class)) {
				continue;
			}

			EventHandler handler = method.getAnnotation(EventHandler.class);
			if(method.getParameterTypes().length != 1) {
				continue;
			}

			Class<?> parameter = method.getParameterTypes()[0];
			if(!Event.class.isAssignableFrom(parameter.getClass())) {
				continue;
			}

			Class<? extends Event> event = (Class<? extends Event>) parameter;

			PrioritisedMethods prioritised = events.get(event);
			if(prioritised == null) {
				prioritised = new PrioritisedMethods();
				events.put(event, prioritised);
			}

			prioritised.add(listener, handler, method, event);
		}
	}

	public void unregister(Listener listener) {
		for(PrioritisedMethods methods : events.values()) {
			methods.remove(listener);
		}
	}

	public void unregister(Class<? extends Event> event) {
		unregister(event, false);
	}

	public void unregister(Class<? extends Event> event, boolean matches) {
		for(PrioritisedMethods methods : events.values()) {
			methods.remove(event, !matches);
		}
	}

	public void call(Event event) {
		List<PrioritisedMethods> methods = events.entrySet().stream().filter(entry -> entry.getKey().isAssignableFrom(event.getClass())).map(Entry::getValue).collect(Collectors.toList());
		Map<EventPriority, List<EventMethod>> priorityMap = new HashMap<>();
		for(EventPriority priority : EventPriority.values()) {
			List<EventMethod> list = new ArrayList<>();
			for(PrioritisedMethods method : methods) {
				list.addAll(method.getMethods(priority));
			}
			priorityMap.put(priority, list);
		}

		for(EventPriority priority : EventPriority.values()) {
			for(EventMethod method : priorityMap.get(priority)) {
				method.handle(event);
			}
		}
	}

}
