package me.kangarko.ui.tool;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import me.kangarko.ui.util.DesignerUtils;

/**
 * Listens to when a player clicks with a tool.
 */
public final class ToolsListener implements Listener {

	@EventHandler(priority=EventPriority.HIGHEST)
	public final void onToolClick(PlayerInteractEvent e) {
		if (!isPrimaryHand(e))
			return;

		final Player pl = e.getPlayer();
		final Tool tool = Tools.getTool(pl.getItemInHand());

		if (tool != null)
			try {
				if ((e.isCancelled() || !e.hasBlock()) && tool.ignoreCancelled())
					return;

				tool.onBlockClick(e);

				if (tool.autoCancel())
					e.setCancelled(true);

			} catch (final Throwable t) {
				DesignerUtils.tell(pl, "&cOups! There was a problem with this tool! Please contact the administrator to review the console for details.");

				e.setCancelled(true);
				t.printStackTrace();
			}
	}

	private static boolean isPrimaryHand(PlayerInteractEvent e) {
		try {
			return e.getHand() != null && e.getHand() == EquipmentSlot.HAND;
		} catch (final NoSuchMethodError err) {
			return true;
		}
	}
}
