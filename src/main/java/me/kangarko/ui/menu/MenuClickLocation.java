package me.kangarko.ui.menu;

/**
 * Represents a click when a menu is opened.
 *
 * @author kangarko
 */
public enum MenuClickLocation {

	/**
	 * Player clicked in the menu (the top part of his inventory).
	 */
	MENU,

	/**
	 * Player clicked at the bottom part of his inventory.
	 */
	PLAYER,

	/**
	 * Player clicked outside (may have dropped the item on cursor).
	 */
	OUTSIDE
}