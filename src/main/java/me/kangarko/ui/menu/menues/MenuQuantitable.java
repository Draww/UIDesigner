package me.kangarko.ui.menu.menues;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import me.kangarko.ui.menu.Menu;
import me.kangarko.ui.menu.MenuButton;
import me.kangarko.ui.menu.MenuQuantity;
import me.kangarko.ui.model.ItemCreator;

/**
 * An interface making it easier to create quantitable buttons.
 */
public interface MenuQuantitable {

	void setQuantity(MenuQuantity newQuantity);

	MenuQuantity getQuantity();

	default int getNextQuantity(ClickType type) {
		return type == ClickType.LEFT ? - + getQuantity().getAmount() : getQuantity().getAmount();
	}

	default MenuButton craftButton(MenuStandard sm) {
		return new MenuButton() {

			@Override
			public final void onClickedInMenu(Player pl, Menu menu, ClickType click) {
				setQuantity(click == ClickType.LEFT ? getQuantity().previous() : getQuantity().next());
				sm.redraw();

				sm.animateTitle("&9Editing quantity set to " + getQuantity().getAmount());
			}

			@Override
			public ItemStack getItem() {
				return ItemCreator
						.of(Material.STRING,
								"Edit Quantity: &7" + getQuantity().getAmount(),
								"",
								"&8< &7Left click to decrease",
								"&8> &7Right click to increase")
						.build().make();
			}
		};
	}
}
