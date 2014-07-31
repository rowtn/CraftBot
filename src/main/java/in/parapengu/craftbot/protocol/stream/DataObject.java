package in.parapengu.craftbot.protocol.stream;

public class DataObject<T extends Object> {

	private final int id, type;
	private T object;

	public DataObject(int type, int id, T object) {
		this.id = id;
		this.type = type;
		this.object = object;
	}

	public int getDataValueId() {
		return id;
	}

	public T getObject() {
		return object;
	}

	public void setObject(T object) {
		this.object = object;
	}

	public int getObjectType() {
		return type;
	}

}
