package me.kangarko.ui.tool;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import me.kangarko.ui.util.DesignerUtils;

/**
 * An easy way to have special tools (ItemStack-s).
 *
 * MAKE A NEW INSTANCE OF EACH OF YOUR TOOL SOMEWHERE AND CALL IT TO BE REGISTERED,
 * OR CALL Tools.register(new YourTool())
 */
public abstract class Tool {

	protected Tool() {
		// A workaround to register after each top constructor.
		new Thread() {

			@Override
			public void run() {
				try {
					Thread.sleep(5);
				} catch (final InterruptedException e) {
				}

				if (!Tools.isRegistered(Tool.this))
					Tools.register(Tool.this);
			}
		}.start();
	}

	/**
	 * Return the item as ItemStack.
	 */
	public abstract ItemStack getItem();

	/**
	 * Called when the player clicks with this tool
	 */
	public abstract void onBlockClick(PlayerInteractEvent e);

	/**
	 * Called when the player swap items in their hotbar and the new slot matches this tool.
	 */
	public void onHotbarFocused(Player pl) {}

	/**
	 * Called when the player the tool is out of focus at hotbar
	 */
	public void onHotbarDefocused(Player pl) {}

	/**
	 * Return if the given item is this tool.
	 */
	public boolean isTool(ItemStack item) {
		return DesignerUtils.itemsSimilar(getItem(), item);
	}

	/**
	 * Ignore cancelled clicks? Return false if you want clicking air.
	 */
	public boolean ignoreCancelled() {
		return true;
	}

	/**
	 * Cancel the event? False by default
	 */
	public boolean autoCancel() {
		return false;
	}

	/**
	 * Please use isTool instead for better precision.
	 */
	@Deprecated
	@Override
	public boolean equals(Object obj) {
		return obj instanceof Tool && ((Tool)obj).getItem().equals(getItem());
	}
}
