package in.parapengu.craftbot.plugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class ClassPathLoader {

	public static URLClassLoader addFile(String s) throws IOException {
		File f = new File(s);
		return addFile(f);
	}

	public static URLClassLoader addFile(File f) throws IOException {
		return addURL(f.toURI().toURL());
	}

	public static URLClassLoader addURL(URL u) throws IOException {
		URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
		Class sysclass = URLClassLoader.class;

		try {
			Method method = sysclass.getDeclaredMethod("addURL", URL.class);
			method.setAccessible(true);
			method.invoke(sysloader, u);
		} catch(Throwable t) {
			t.printStackTrace();
			throw new IOException("Error, could not add URL to system classloader");
		}

		return sysloader;
	}

	/*
	public static URLClassLoader addURL(URL u) throws IOException {
		return URLClassLoader.newInstance(new URL[]{u}, ClassLoader.getSystemClassLoader());
	}
	*/

}
