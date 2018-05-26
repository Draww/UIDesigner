package me.kangarko.ui.model;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Holds a Sound, volume or pitch together.
 *
 * When the pitch is null, it is given random.
 *
 * @author kangarko
 */
@Getter
@RequiredArgsConstructor
public class UISound {

	private final Sound sound;
	private final float volume;
	private final Float pitch;

	/**
	 * Makes a new sound with a random pitch each time you call getPitch() or play(Player).
	 */
	public UISound(Sound sound, float volume) {
		this(sound, volume, null);
	}

	/**
	 * Plays the sound to the player. When the pitch is null, it is given random.
	 */
	public final void play(Player player) {
		player.playSound(player.getLocation(), sound, volume, getPitch());
	}

	/**
	 * Get the set pitch or a random one,.
	 */
	public float getPitch() {
		return pitch != null ? pitch : (float) Math.random();
	}
}
