package me.kangarko.ui.menu.buttons;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import me.kangarko.ui.menu.Menu;
import me.kangarko.ui.menu.MenuButton;
import me.kangarko.ui.model.ItemCreator;

/**
 * A simple button that, upon clicked, will open the previous menu.
 */
@RequiredArgsConstructor
@AllArgsConstructor
public class ButtonReturnBack extends MenuButton {

	/**
	 * The menu to return to.
	 */
	private final Menu menuToReturn;

	/**
	 * Make a new instance of the previous menu?
	 *
	 * Deal with caution. Make sure you overwrite newInstance in your menu IF your constructor takes parameters.
	 */
	private boolean makeNewInstance = false;

	@Override
	public final ItemStack getItem() {
		return ItemCreator
				.of(Material.WOOD_DOOR)
				.name("&4&lReturn")
				.lores(getLore())
				.unbreakable(true)
				.build().make();
	}

	@Override
	public final void onClickedInMenu(Player pl, Menu menu, ClickType click) {
		onPreReturnBack();

		if (makeNewInstance)
			menuToReturn.newInstance().displayTo(pl);
		else
			menuToReturn.displayTo(pl);
	}

	public List<String> getLore() {
		return Arrays.asList(
				"&r",
				"&7Return back.");
	}

	/**
	 * Called before we return to the previous menu.
	 */
	public void onPreReturnBack() {}
}