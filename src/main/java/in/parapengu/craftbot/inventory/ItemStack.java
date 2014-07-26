package in.parapengu.craftbot.inventory;

import in.parapengu.craftbot.inventory.nbt.NBTTagCompound;
import in.parapengu.craftbot.material.Material;
import in.parapengu.craftbot.material.MaterialData;

public class ItemStack {

	private int type;
	private int amount;
	private short durability;
	private NBTTagCompound compound;
	private MaterialData data;

	public ItemStack(int type) {
		this(type, 1);
	}

	public ItemStack(Material type) {
		this(type, 1);
	}

	public ItemStack(int type, int amount) {
		this(type, amount, (short) 0);
	}

	public ItemStack(Material type, int amount) {
		this(type.getId(), amount);
	}

	public ItemStack(int type, int amount, short damage) {
		this.type = type;
		this.amount = amount;
		this.durability = damage;
	}

	public ItemStack(Material type, int amount, short damage) {
		this(type.getId(), amount, damage);
	}

	public ItemStack(int type, int amount, short damage, Byte data) {
		this.type = type;
		this.amount = amount;
		this.durability = damage;
		if(data != null) {
			createData(data);
			this.durability = data;
		}
	}

	public ItemStack(Material type, int amount, short damage, Byte data) {
		this(type.getId(), amount, damage, data);
	}

	public int getTypeId() {
		return type;
	}

	public void setTypeId(int type) {
		this.type = type;
	}

	public Material getType() {
		Material material = Material.getMaterial(type);
		return material != null ? material : Material.AIR;
	}

	public void setType(Material material) {
		setTypeId(material.getId());
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public short getDurability() {
		return durability;
	}

	public void setDurability(short durability) {
		this.durability = durability;
	}

	public NBTTagCompound getCompound() {
		return compound;
	}

	public void setCompound(NBTTagCompound compound) {
		this.compound = compound;
	}

	public MaterialData getData() {
		if(data == null) {
			createData((byte) durability);
		}

		return data;
	}

	public void setData(MaterialData data) {
		Material type = getType();

		if(data == null || type == null || type.getData() == null) {
			this.data = data;
		} else {
			if((data.getClass() == type.getData()) || (data.getClass() == MaterialData.class)) {
				this.data = data;
			} else {
				throw new IllegalArgumentException("Provided data is not of type " + type.getData().getName() + ", found " + data.getClass().getName());
			}
		}
	}

	private void createData(byte data) {
		Material mat = Material.getMaterial(type);

		if(mat == null) {
			this.data = new MaterialData(type, data);
		} else {
			this.data = mat.getData(data);
		}
	}

}
