package in.parapengu.craftbot.entity.potion;

public enum PotionEffectType {

	SPEED(1),
	SLOWNESS(2),
	HASTE(3),
	MINING_FATIGUE(4),
	STRENGTH(5),
	INSTANT_HEALTH(6),
	INSTANT_DAMAGE(7),
	JUMP_BOOST(8),
	NAUSEA(9),
	REGENERATION(10),
	RESISTANCE(11),
	FIRE_RESISTANCE(12),
	WATER_BREATHING(13),
	INVISIBILITY(14),
	NIGHT_VISION(16),
	HUNGER(17),
	WEAKNESS(18),
	POISON(19),
	WITHER(20),
	HEALTH_BOOST(21),
	ABSORPTION(22),
	SATURATION(23);

	private int effectId;

	PotionEffectType(int effectId) {
		this.effectId = effectId;
	}

	public int getEffectId() {
		return effectId;
	}

	public static PotionEffectType getType(int effectId) {
		for(PotionEffectType type : values()) {
			if(type.getEffectId() == effectId) {
				return type;
			}
		}

		return null;
	}

}
