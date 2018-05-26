package me.kangarko.ui.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.md_5.bungee.api.ChatColor;

/**
 * Various utility class.
 *
 * @author kangarko
 */
public class DesignerUtils {

	/**
	 * Send a message to the player, with & letters colorized.
	 */
	public static final void tell(CommandSender sender, String message) {
		sender.sendMessage(colorize(message));
	}

	/**
	 * Replace the & letter with the {@link org.bukkit.ChatColor.COLOR_CHAR} in the message.
	 */
	public static final String colorize(String message) {
		return ChatColor.translateAlternateColorCodes('&', message);
	}

	/**
	 * See {@link #range(int, int, int)}
	 */
	public static double range(double value, double min, double max) {
		return Math.min(Math.max(value, min), max);
	}

	/**
	 * Get a value in range. If the value is < min, returns min, if it is > max, returns max.
	 */
	public static int range(int value, int min, int max) {
		return Math.min(Math.max(value, min), max);
	}

	/**
	 * Return an empty String if the String is null.
	 */
	public static final String getOrEmpty(String t) {
		return t != null ? t : "";
	}

	/**
	 * Convert Iterable to ArrayList.
	 */
	public static final <T> List<T> toList(Iterable<T> it) {
		final List<T> list = new ArrayList<>();
		it.forEach((el) -> list.add(el));

		return list;
	}

	/**
	 * Compare two lists. Two lists are considered equal if they are same length and all values are the same.
	 *
	 * Exception: Strings are stripped of colors before comparation.
	 */
	public static final <T> boolean listEquals(List<T> first, List<T> second) {
		if (first == null && second == null)
			return true;

		if (first == null && second != null)
			return false;

		if (first != null && second == null)
			return false;

		if (first != null) {
			if (first.size() != second.size())
				return false;

			for (int i = 0; i < first.size(); i++) {
				final T f = first.get(i);
				final T s = second.get(i);

				if (f == null && s != null)
					return false;

				if (f != null && s == null)
					return false;

				if (f != null && s != null && !f.equals(s))
					if (!ChatColor.stripColor(f.toString()).equals(ChatColor.stripColor(s.toString())))
						return false;
			}
		}

		return true;
	}

	/**
	 * Compares two items. Returns true if they are similar.
	 *
	 * Two items are similar if both are not null and if their type, data, name and lore equals.
	 * The damage, quantity, item flags enchants and other properties are ignored.
	 */
	public static boolean itemsSimilar(ItemStack first, ItemStack second) {
		if (first == null || second == null)
			return false;

		if (first.equals(second) || (first.hasItemMeta() && second.hasItemMeta() && first.getItemMeta().equals(second.getItemMeta())))
			return true;

		final boolean idMatch = first.getTypeId() == second.getTypeId();
		boolean dataMatch = first.getData().getData() == second.getData().getData();
		boolean metaMatch = first.hasItemMeta() == second.hasItemMeta();

		if (metaMatch) {
			final ItemMeta f = first.getItemMeta();
			final ItemMeta s = second.getItemMeta();

			if (!DesignerUtils.getOrEmpty(f.getDisplayName()).equals(DesignerUtils.getOrEmpty(s.getDisplayName())))
				metaMatch = false;

			if (!DesignerUtils.listEquals(f.getLore(), s.getLore()))
				metaMatch = false;
		}

		// A workaround for bows, that can take damage and thus won't return the same
		if (first.getType() == Material.BOW && second.getType() == Material.BOW)
			if (!dataMatch)
				dataMatch = true;

		return idMatch && dataMatch && metaMatch;
	}
}
