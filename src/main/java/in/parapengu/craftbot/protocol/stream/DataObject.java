package in.parapengu.craftbot.protocol.stream;

import in.parapengu.craftbot.util.ClassUtils;

public class DataObject<T extends Object> {

	private int id;
	private int type;
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

	@Override
	public String toString() {
		return ClassUtils.build(getClass(), this, true);
	}

}
