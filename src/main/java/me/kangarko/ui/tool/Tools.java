package me.kangarko.ui.tool;

import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.lang.Validate;
import org.bukkit.inventory.ItemStack;

/**
 * Tools are being automatically registered here.
 */
public class Tools {

	private static final Collection<Tool> tools = new ConcurrentLinkedQueue<>();

	static final synchronized void register(Tool tool) {
		Validate.isTrue(!isRegistered(tool), "Tool (" + tool.getItem() + ") already registered");

		tools.add(tool);
	}

	static final synchronized boolean isRegistered(Tool tool) {
		return getTool(tool.getItem()) != null;
	}

	public static final Tool getTool(ItemStack item) {
		for (final Tool t : tools)
			if (t.isTool(item))
				return t;

		return null;
	}

	public static final Tool[] getTools() {
		return tools.toArray( new Tool[tools.size()] );
	}
}
