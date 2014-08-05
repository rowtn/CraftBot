package in.parapengu.craftbot.event;

import com.google.common.collect.Lists;
import in.parapengu.craftbot.bot.BotHandler;
import in.parapengu.craftbot.logging.Logger;
import in.parapengu.craftbot.logging.Logging;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class EventManager {

	private Logger logger;
	private Map<Class<? extends Event>, PrioritisedMethods> events;

	public EventManager(Logger logger) {
		this.logger = logger;
		this.events = new HashMap<>();
	}

	public EventManager() {
		this(Logging.getLogger(EventManager.class, BotHandler.getHandler().getLogger()));
	}

	public void register(Listener listener) {
		Class<?> clazz = listener.getClass();

		Method[] methods = clazz.getMethods();
		logger.debug(Lists.newArrayList(methods) + " from " + listener.getClass().getSimpleName());
		for(Method method : methods) {
			String display = method.getName() + "()";
			if(!method.isAnnotationPresent(EventHandler.class)) {
				logger.debug("Could not load " + display + " because " + EventHandler.class.getSimpleName() + " was not present");
				continue;
			}

			EventHandler handler = method.getAnnotation(EventHandler.class);
			if(method.getParameterTypes().length != 1) {
				int length = method.getParameterTypes().length;
				logger.debug("Could not load " + display + " it had " + (length < 1 ? "less than" : "more than") + " one parameter");
				continue;
			}

			Class<?> parameter = method.getParameterTypes()[0];
			Class<? extends Event> event;
			try {
				event = (Class<? extends Event>) parameter;
			} catch(Exception ex) {
				logger.debug("Could not load " + display + " because " + parameter.getSimpleName() + " is not an instance of " + Event.class.getSimpleName());
				continue;
			}

			display = method.getName() + "(" + event.getSimpleName() + ")";
			if(events == null) {
				logger.debug("Events in null wat");
				continue;
			}
			PrioritisedMethods prioritised = events.containsKey(event) ? events.get(event) : null;
			if(prioritised == null) {
				prioritised = new PrioritisedMethods();
				events.put(event, prioritised);
			}

			logger.debug("Registered " + display + " from " + listener.getClass().getSimpleName());
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

	public <T extends Event> T call(T event) {
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

		return event;
	}

}
