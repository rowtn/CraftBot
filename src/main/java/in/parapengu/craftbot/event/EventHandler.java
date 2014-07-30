package in.parapengu.craftbot.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventHandler {

	public EventPriority value() default EventPriority.NORMAL;

	public boolean ignoreCancelled() default true;

	public boolean classMatches() default true;

}
