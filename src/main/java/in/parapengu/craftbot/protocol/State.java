package in.parapengu.craftbot.protocol;

public enum State {

	PLAY(0),
	STATUS(1),
	LOGIN(2);

	private int id;

	State(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

}
