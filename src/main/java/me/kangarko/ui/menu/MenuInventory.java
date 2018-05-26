package me.kangarko.ui.menu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import me.kangarko.ui.model.ItemCreator;
import me.kangarko.ui.util.DesignerUtils;

/**
 * Represents the entire inventory in a menu.
 */
public final class MenuInventory {

	@Getter
	private final int size;
	private final String title;
	private final ItemStack[] content;

	/**
	 * Make a new inventory with a size and a title.
	 *
	 * Use the static access below.
	 */
	private MenuInventory(int size, String title) {
		this.size = size;
		this.title = DesignerUtils.colorize(title);

		this.content = new ItemStack[size];
	}

	/**
	 * Mass add items using the item creator builder.
	 */
	public final void addItems(ItemCreator.ItemCreatorBuilder... items) {
		for (final ItemCreator.ItemCreatorBuilder item : items)
			addItem(item.build().make());
	}

	/**
	 * Mass add items. They will be placed when the slot is not null.
	 */
	public final void addItems(ItemStack... items) {
		for (final ItemStack item : items)
			addItem(item);
	}

	/**
	 * Place an item to the first free slot starting from 0, or placing at the end if the menu is full.
	 */
	public final void addItem(ItemStack is) {
		boolean added = false;

		for (int i = 0; i < content.length; i++) {
			final ItemStack c = content[i];

			if (c == null) {
				content[i] = is;
				added = true;
				break;
			}
		}

		if (!added)
			content[size - 1] = is;
	}

	/**
	 * Is slot taken?
	 */
	public final boolean isSlotTaken(int slot) {
		return slot < content.length && content[slot] != null;
	}

	/**
	 * Sets an item at the specified slot.
	 */
	public final void setItem(int slot, ItemStack item) {
		content[slot] = item;
	}

	/**
	 * Replace all content in this inventory. If new content is shorter, rest will be filled with air
	 */
	public final void setContent(ItemStack[] newContent) {
		for (int i = 0; i < content.length; i++)
			content[i] = i < newContent.length ? newContent[i] : new ItemStack(Material.AIR);
	}

	/**
	 * Display this inventory to a player.
	 */
	public final void display(Player pl) {
		final Inventory inv = Bukkit.createInventory(pl, size, title);
		inv.setContents(content);

		if (pl.getOpenInventory() != null)
			pl.closeInventory();

		pl.openInventory(inv);
	}

	/**
	 * Make a new MenuInventory of the specified size and a title.
	 */
	public static MenuInventory of(int size, String title) {
		return new MenuInventory(size, title);
	}
}
