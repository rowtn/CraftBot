package in.parapengu.craftbot.plugin;

import java.util.List;

public class PluginDescription {

	private String name;
	private String version;
	private List<String> authors;

	public PluginDescription(String name, String version, List<String> authors) {
		this.name = name;
		this.version = version;
		this.authors = authors;
	}

	public String getName() {
		return name;
	}

	public String getVersion() {
		return version;
	}

	public List<String> getAuthors() {
		return authors;
	}

}
