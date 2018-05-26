package me.kangarko.ui.menu.menues;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import me.kangarko.ui.menu.MenuButton;
import me.kangarko.ui.model.Enchant;
import me.kangarko.ui.model.ItemCreator;
import me.kangarko.ui.model.ItemCreator.CreatorFlag;
import me.kangarko.ui.tool.Tool;

/**
 * An extension of the standard menu allowing you to list Tools with confidence.
 *
 * The menu makes it easy to give players special items/tools.
 * It only allows players to obtain a single piece of each item inside.
 *
 * Make sure to set the title and the size before having the confidence.
 */
public abstract class MenuItems extends MenuStandard {

	private final List<HaveAbleItem> tools;

	protected MenuItems() {
		super(null);

		this.tools = compile( makeTools() );
	}

	/**
	 * Return an array of items from top left corner.
	 * Accepts: 0 for air, ItemStack, Tool or a MenuButton.
	 */
	protected abstract Object[] makeTools();

	// Compile the items in constructors from makeTools method
	private final List<HaveAbleItem> compile(Object... tools) {
		final List<HaveAbleItem> list = new ArrayList<>();

		if (tools != null)
			for (final Object tool : tools)
				list.add( new HaveAbleItem(tool) );

		return list;
	}

	@Override
	protected final ItemStack getItemAt(int slot) {
		return slot < tools.size() ? tools.get(slot).get(getViewer()) : null;
	}

	@Override
	public final void onMenuClick(Player pl, int slot, InventoryAction action, ClickType click, ItemStack cursor, ItemStack item, boolean cancelled) {
		final ItemStack it = getItemAt(slot);

		if (it != null) {
			lookupHaveable(it).giveOrTake(pl);

			redraw();
		}
	}

	private final HaveAbleItem lookupHaveable(ItemStack item) {
		for (final HaveAbleItem h : tools)
			if (h.equals(item))
				return h;

		return null;
	}

	@Override
	protected final int getInfoButtonPosition() {
		return getSize() - 1;
	}
}

/**
 * Wraps an ItemStack that the player can have only once.
 */
final class HaveAbleItem {

	private final ItemStack item;

	private boolean has = false;

	HaveAbleItem(Object unparsed) {
		if (unparsed != null) {
			if (unparsed instanceof ItemStack)
				this.item = (ItemStack) unparsed;

			else if (unparsed instanceof MenuButton)
				this.item = ((MenuButton)unparsed).getItem();

			else if (unparsed instanceof Tool)
				this.item = ((Tool)unparsed).getItem();

			else if (unparsed instanceof Number)
				this.item = new ItemStack(Material.AIR);

			else throw new RuntimeException("unknown: " + unparsed);

		} else
			this.item = new ItemStack(Material.AIR);
	}

	final ItemStack get(Player pl) {
		update(pl);

		return has ? getWhenHas() : getWhenHasnt();
	}

	private final void update(Player pl) {
		has = pl.getOpenInventory().getBottomInventory().containsAtLeast(item, 1);
	}

	private final ItemStack getWhenHas() {
		return ItemCreator
				.of(item)
				.enchant(new Enchant(Enchantment.ARROW_INFINITE, 1))
				.flag(CreatorFlag.HIDE_ENCHANTS)
				.lores(Arrays.asList("", "&cYou already have this item.", "&7Click to take it away."))
				.build().make();
	}

	private final ItemStack getWhenHasnt() {
		return item;
	}

	final void giveOrTake(Player pl) {
		final PlayerInventory inv = pl.getInventory();

		if (has = !has)
			inv.addItem(item);

		else
			inv.removeItem(item);
	}

	final boolean equals(ItemStack item) {
		return getWhenHas().isSimilar(item) || getWhenHasnt().isSimilar(item);
	}
}
