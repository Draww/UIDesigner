package me.kangarko.ui.menu.menues;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang.Validate;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.kangarko.ui.menu.Menu;
import me.kangarko.ui.menu.MenuButton;
import me.kangarko.ui.menu.MenuButton.DummyButton;
import me.kangarko.ui.menu.MenuInventory;
import me.kangarko.ui.menu.buttons.ButtonReturnBack;

/**
 * This is "the" standard menu you want to use all the time
 * featuring a return button, and an info button
 */
public abstract class MenuStandard extends Menu {

	/**
	 * The size of the menu
	 */
	private Integer size = 9 * 3;

	/**
	 * The inventory title of the menu
	 */
	private String title;

	/**
	 * Parent menu
	 */
	private final Menu parent;

	/**
	 * The return button to the previous menu, null if none
	 */
	private final MenuButton returnButton;

	protected MenuStandard(Menu parent) {
		this(parent, false);
	}

	protected MenuStandard(Menu parent, boolean makeNewInstance) {
		this.parent = parent;
		this.returnButton = parent != null ? new ButtonReturnBack(parent, makeNewInstance) : MenuButton.makeEmpty();
	}

	@Override
	protected final MenuInventory formInventory() {
		Objects.requireNonNull(size, "Size not set in " + this);
		Objects.requireNonNull(title, "Title not set in " + this);

		final MenuInventory inv = MenuInventory.of(size, title);

		drawBottomBar(inv);
		paint(inv);

		return inv;
	}

	private final void drawBottomBar(MenuInventory inv) {
		if (getInfo() != null)
			inv.setItem(getInfoButtonPosition(),  MenuButton.makeInfoButton(getInfo()).getItem());

		if (addReturnButton() && !(returnButton instanceof DummyButton))
			inv.setItem(getReturnButtonPosition(), returnButton.getItem());
	}

	/**
	 * Re-register fields, redraw and send an animated title.
	 *
	 * Useful when you change some buttons.
	 */
	protected final void restartMenu(String title) {
		registerFields();
		redraw();

		if (title != null)
			animateTitle(title);
	}

	/**
	 * Redraw all items including the bottom bar.
	 */
	public final void redraw() {
		final Inventory inv = getViewer().getOpenInventory().getTopInventory();
		Validate.isTrue(inv.getType() == InventoryType.CHEST, getViewer().getName() + "'s inventory closed in the meanwhile (now == " + inv.getType() + ").");

		for (int i = 0; i < size; i++) {
			final ItemStack item = getItemAt(i);

			Validate.isTrue(i < inv.getSize(), "Item (" + (item != null ? item.getType() : "null") + ") position (" + i + ") > inv size (" + inv.getSize() + ")");
			inv.setItem(i, item);
		}

		drawBottomBar(inv);
	}

	private final void drawBottomBar(Inventory inv) {
		if (getInfo() != null)
			inv.setItem(getInfoButtonPosition(),  MenuButton.makeInfoButton(getInfo()).getItem());

		if (addReturnButton() && !(returnButton instanceof DummyButton))
			inv.setItem(getReturnButtonPosition(), returnButton.getItem());
	}

	/**
	 * Get the menu description. By default a nether star appear in the bottom left corner.
	 *
	 * Return null to disable.
	 */
	protected abstract String[] getInfo();

	protected void paint(MenuInventory inv) {}

	/**
	 * Should we add a return button?
	 */
	protected boolean addReturnButton() {
		return true;
	}

	/**
	 * Description button position, by default bottom left corner.
	 */
	protected int getInfoButtonPosition() {
		return size - 9;
	}

	/**
	 * Return button position, by default bottom right corner.
	 */
	protected int getReturnButtonPosition() {
		return size - 1;
	}

	// ----------------------------------------------------------------------------------------------------
	// FINAL
	// ----------------------------------------------------------------------------------------------------

	/**
	 * Returns all items, also the NULL ones!
	 */
	protected final Map<Integer, ItemStack> getItemsExceptBottomBar(Inventory inv) {
		final Map<Integer, ItemStack> items = new HashMap<>();

		for (int i = 0; i < getSize() - 9; i++) {
			final ItemStack item = inv.getItem(i);

			items.put(i, item != null && item.getTypeId() != 0 ? item : null);
		}

		return items;
	}

	protected final int getSize() {
		return size;
	}

	protected final void setSize(int size) {
		this.size = size;
	}

	@Override
	protected final String getTitle() {
		return title;
	}

	protected final void setTitle(String title) {
		this.title = "&0" + title;
	}

	/**
	 * Returns approximatelly the center slot, sometimes fail (depending on the mood).
	 */
	@Deprecated
	protected final int getCenterSlot() {
		int pos = Arrays.asList(13, 22, 31).contains( (pos = size / 2) ) ? pos : pos - 5;

		return pos;
	}

	protected final Menu getParent() {
		return parent;
	}
}
