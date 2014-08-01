package in.parapengu.craftbot.entity.potion;

public class PotionEffect {

	private PotionEffectType type;
	private int amplifier;
	private int duration;

	public PotionEffect(PotionEffectType type, int amplifier, int duration) {
		this.type = type;
		this.amplifier = amplifier;
		this.duration = duration;
	}

	public PotionEffectType getType() {
		return type;
	}

	public void setType(PotionEffectType type) {
		this.type = type;
	}

	public int getAmplifier() {
		return amplifier;
	}

	public void setAmplifier(int amplifier) {
		this.amplifier = amplifier;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

}
