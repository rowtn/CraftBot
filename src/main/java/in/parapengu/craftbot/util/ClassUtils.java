package in.parapengu.craftbot.util;

import com.google.common.collect.Lists;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ClassUtils {

	public static String build(Class<?> clazz, Object object, String... excludes) {
		return build(clazz, object, false, excludes);
	}

	public static String build(Class<?> clazz, Object object, boolean all, String... excludes) {
		try {
			return buildThrows(clazz, object, all, excludes);
		} catch(IllegalAccessException illegal) {
			illegal.printStackTrace();
			return clazz.getSimpleName() + "{class=" + clazz.getName() + "}";
		}
	}

	private static String buildThrows(Class<?> clazz, Object object, String... excludes) throws IllegalAccessException {
		return buildThrows(clazz, object, false, excludes);
	}

	private static String buildThrows(Class<?> clazz, Object object, boolean all, String... excludes) throws IllegalAccessException {
		List<String> exclude = Lists.newArrayList(excludes);
		StringBuilder string = new StringBuilder();
		string.append(clazz.getSimpleName()).append("{");

		List<Class<?>> classes = new ArrayList<>();
		do {
			classes.add(clazz);
			clazz = clazz.getSuperclass();
		} while(all && clazz != Object.class);

		boolean previous = false;
		for(Class<?> cl : classes) {
			Field[] fields = cl.getDeclaredFields();
			for(Field field : fields) {
				if(!exclude.contains(field.getName())) {
					field.setAccessible(true);
					if(previous) {
						string.append(",");
					}

					Object value = field.get(object);
					if(value != null && value.getClass().isArray()) {
						List<Object> objects = new ArrayList<>();
						int length = Array.getLength(value);
						for(int i = 0; i < length; i++) {
							objects.add(Array.get(value, i));
						}
						value = objects;
					}

					string.append(field.getName()).append("=").append("[").append(value != null ? value : "null").append("]");
					previous = true;
				}
			}
		}

		if(previous) {
			string.append(",");
		}

		string.append("class").append("=").append(object.getClass().getName());

		string.append("}");
		return string.toString();
	}

}
