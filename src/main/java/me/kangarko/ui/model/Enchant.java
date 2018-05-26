package me.kangarko.ui.model;

import org.bukkit.enchantments.Enchantment;

import lombok.Getter;

/**
 * Represents an enchantment together with a level.
 *
 * @author kangarko
 */
@Getter
public final class Enchant {

	private final Enchantment enchant;
	private final int level;

	/**
	 * Create a new Enchant class with given enchant of level 1.
	 */
	public Enchant(Enchantment enchant) {
		this(enchant, 1);
	}

	/**
	 * Create a new Enchant class with specified values.
	 */
	public Enchant(Enchantment enchant, int level) {
		this.enchant = enchant;
		this.level = level;
	}
}