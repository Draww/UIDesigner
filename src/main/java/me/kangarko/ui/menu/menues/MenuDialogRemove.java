package me.kangarko.ui.menu.menues;

import org.bukkit.inventory.ItemStack;

import me.kangarko.ui.menu.Menu;
import me.kangarko.ui.menu.MenuButton;
import me.kangarko.ui.menu.MenuInventory;
import me.kangarko.ui.menu.buttons.ButtonRemove.RemoveConfirmButton;
import me.kangarko.ui.menu.buttons.ButtonReturnBack;

/**
 * A dialog confirming removal of an object.
 */
public class MenuDialogRemove extends Menu {

	private final RemoveConfirmButton removeButton;
	private final MenuButton returnButton;

	public MenuDialogRemove(Menu parentMenu, RemoveConfirmButton removeButton) {
		this.removeButton = removeButton;
		this.returnButton = new ButtonReturnBack(parentMenu);
	}

	@Override
	protected ItemStack getItemAt(int slot) {
		if (slot == 9 + 3)
			return removeButton.getItem();

		if (slot == 9 + 5)
			return returnButton.getItem();

		return null;
	}

	@Override
	protected MenuInventory formInventory() {
		return MenuInventory.of(9 * 3, getTitle());
	}

	@Override
	protected String getTitle() {
		return "&0Confirm removal";
	}
}
