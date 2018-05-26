package me.kangarko.ui.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.kangarko.ui.UIDesignerAPI;
import me.kangarko.ui.util.DesignerUtils;
import me.kangarko.ui.util.ReflectionUtil;

/**
 * Makes it possible to click in menues automatically saving tons of work.
 */
public final class MenuListener implements Listener {

	/**
	 * Broadcast menu closing.
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public final void onInvClose(InventoryCloseEvent e) {
		if (!(e.getPlayer() instanceof Player))
			return;

		final Player pl = (Player) e.getPlayer();
		final Menu menu = Menu.getMenu(e.getPlayer());

		if (menu != null) {
			menu.onMenuClose(pl, e.getInventory());

			pl.removeMetadata(Menu.TAG_CURRENT, UIDesignerAPI.getPlugin());
		}
	}

	/**
	 * Handle clicking (...).
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public final void onInvClick(InventoryClickEvent e) {
		if (!(e.getWhoClicked() instanceof Player))
			return;

		final Player pl = (Player) e.getWhoClicked();
		final Menu menu = Menu.getMenu(pl);

		if (menu != null) {
			final ItemStack slotItem = e.getCurrentItem();
			final ItemStack cursor = e.getCursor();
			final Inventory clickedInv = ReflectionUtil.getClickedInventory(e);

			final InventoryAction action = e.getAction();
			final MenuClickLocation whereClicked = clickedInv != null ? (clickedInv.getType() == InventoryType.CHEST ? MenuClickLocation.MENU : MenuClickLocation.PLAYER) : MenuClickLocation.OUTSIDE;

			final boolean allowed = menu.isActionAllowed(whereClicked, e.getSlot(), slotItem, cursor);

			if (action.toString().contains("PICKUP") || action.toString().contains("PLACE") || action == InventoryAction.CLONE_STACK) {
				if (whereClicked == MenuClickLocation.MENU) {
					try {
						final MenuButton tool = menu.getButton(slotItem);

						if (tool != null)
							menu.onMenuClick(pl, e.getSlot(), action, e.getClick(), tool);
						else
							menu.onMenuClick(pl, e.getSlot(), action, e.getClick(), cursor, slotItem, !allowed);

					} catch (final Throwable t) {
						DesignerUtils.tell(pl, "&cOups! There was a problem with this menu! Please contact the administrator to review the console for details.");
						pl.closeInventory();

						System.out.println("&cError clicking in menu " + menu);
						t.printStackTrace();
					}
				}

				if (!allowed)
					e.setResult(Result.DENY);

			} else if (action == InventoryAction.MOVE_TO_OTHER_INVENTORY || whereClicked != MenuClickLocation.PLAYER)
				e.setResult(Result.DENY);
		}
	}
}
