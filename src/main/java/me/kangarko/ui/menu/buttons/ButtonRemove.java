package me.kangarko.ui.menu.buttons;

import java.util.Arrays;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import me.kangarko.ui.menu.Menu;
import me.kangarko.ui.menu.MenuButton;
import me.kangarko.ui.menu.menues.MenuDialogRemove;
import me.kangarko.ui.model.ItemCreator;
import me.kangarko.ui.model.ItemCreator.CreatorFlag;
import me.kangarko.ui.util.DesignerUtils;

/**
 * A finished button that removes stuff, with a middle step where you have to confirm the removal.
 */
@RequiredArgsConstructor
public class ButtonRemove extends MenuButton {

	/**
	 * The parent menu.
	 */
	private final Menu parentMenu;

	/**
	 * The type of the object, for example "class/arena/sword"
	 */
	private final String objectType;

	/**
	 * The name of the object, for example "Death Arena/Mighty Sword/Zombie Boss" etc.
	 */
	private final String objectName;

	/**
	 * What happens when the object is removed.
	 */
	private final ButtonRemovalListener removal;

	@Override
	public ItemStack getItem() {
		return ItemCreator

				.of(Material.LAVA_BUCKET)
				.name("&4&lRemove " + objectName)

				.lores(Arrays.asList(
						"&r",
						"&7The selected " + objectType + " will",
						"&7be removed permanently."))

				.flag(CreatorFlag.HIDE_ATTRIBUTES)
				.build().make();
	}

	/**
	 * Show the remove dialog (a Menu) when clicked.
	 */
	@Override
	public void onClickedInMenu(Player pl, Menu menu, ClickType click) {
		new MenuDialogRemove(parentMenu, new RemoveConfirmButton()).displayTo(pl);
	}

	/**
	 * A button for the middle step before actually removing the object, for safety.
	 */
	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	public class RemoveConfirmButton extends MenuButton {

		@Override
		public ItemStack getItem() {
			return ItemCreator

					.of(Material.WOOL)
					.data(DyeColor.RED.getWoolData())
					.name("&6&lRemove " + objectName)

					.lores(Arrays.asList(
							"&r",
							"&7Confirm that this " + objectType + " will",
							"&7be removed permanently.",
							"&cCannot be undone."))

					.flag(CreatorFlag.HIDE_ATTRIBUTES)
					.build().make();
		}

		/**
		 * Close inventory, call the removal listener with the object's name and send the player a message.
		 */
		@Override
		public final void onClickedInMenu(Player pl, Menu menu, ClickType click) {
			pl.closeInventory();
			removal.onRemove(objectName);

			DesignerUtils.tell(pl, "&2The " + (!objectType.isEmpty() ? objectType + " " : "") + objectName + " has been deleted.");
		}
	}
}