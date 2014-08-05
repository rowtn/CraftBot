package in.parapengu.craftbot.world;

import in.parapengu.craftbot.location.Vector;
import in.parapengu.craftbot.material.Material;

/**
 * Created by August on 8/5/14.
 */
public class Block {

	private Material material;
	private Vector vector;
	private World world;

	public Block(Material material, Vector vector, World world) {
		this.material = material;
		this.vector = vector;
		this.world = world;
	}

	public Material getMaterial() {
		return material;
	}

	public Vector getVector() {
		return vector;
	}

	public World getWorld() {
		return world;
	}

	public int getX() {
		return (int) vector.getX();
	}

	public int getY() {
		return (int) vector.getY();
	}

	public int getZ() {
		return (int) vector.getZ();
	}

	public Material getType() {
		return material;
	}

	public int getTypeId() {
		return material.getId();
	}

	@Override
	public String toString() {
		return "Block{" +
				"material=" + material +
				", vector=" + vector +
				'}';
	}
}