package me.kangarko.ui.menu.buttons;

/**
 * Provides an easy way to do stuff when an object is removed.
 */
@FunctionalInterface
public interface ButtonRemovalListener {

	/**
	 * What happens when the button to remove a certain object is pressed.
	 *
	 * The object is always a string. If you have a plugin listing Classes, Arenas etc.,
	 * then the object will logically be their name and here you need to search in your registry
	 * for a Class/Arena/... of that name and remove it manually ;)
	 */
	public void onRemove(String object);

}