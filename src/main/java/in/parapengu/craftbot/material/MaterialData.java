package in.parapengu.craftbot.material;

import in.parapengu.craftbot.util.ClassUtils;

public class MaterialData {

	private final int type;
	private byte data;

	public MaterialData(int type, byte data) {
		this.type = type;
		this.data = data;
	}

	public MaterialData(int type) {
		this(type, (byte) 0);
	}

	public MaterialData(Material material, byte data) {
		this(material.getId(), data);
	}

	public MaterialData(Material material) {
		this(material.getId());
	}

	public Material getType() {
		return Material.getMaterial(type);
	}

	public int getTypeId() {
		return type;
	}

	public byte getData() {
		return data;
	}

	@Override
	public String toString() {
		return ClassUtils.build(getClass(), this, true);
	}

}
