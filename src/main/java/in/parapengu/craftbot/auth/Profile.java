package in.parapengu.craftbot.auth;

public class Profile {

	private final String id, name;

	public Profile(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

}
