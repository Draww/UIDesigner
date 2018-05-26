package me.kangarko.ui;

import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import lombok.Setter;
import me.kangarko.ui.menu.MenuListener;
import me.kangarko.ui.model.UISound;
import me.kangarko.ui.tool.ToolsListener;

/**
 * The "main" class of this library. Welcome abroad!
 *
 * Please set your plugin asap with the setPlugin() method!
 *
 * @author kangarko
 */
public class UIDesignerAPI {

	/**
	 * Your plugin that this library is hooking into.
	 *
	 * PLEASE SET IT ASAP WITH setPlugin().
	 */
	@Getter
	private static JavaPlugin plugin;

	/**
	 * The default sound when switching between menues.
	 */
	@Getter @Setter
	private static UISound sound = new UISound(Sound.BLOCK_NOTE_HAT, .4f);

	/**
	 * Set your plugin so this library can use its instance, and register important events as it.
	 */
	public static void setPlugin(JavaPlugin plugin) {
		UIDesignerAPI.plugin = plugin;

		Bukkit.getPluginManager().registerEvents(new MenuListener(), plugin);
		Bukkit.getPluginManager().registerEvents(new ToolsListener(), plugin);
	}

	// Check if the user really registered the plugin.
	static {
		new Thread() {

			@Override
			public void run() {
				try { Thread.sleep(10); } catch (final InterruptedException e) {}

				Objects.requireNonNull(plugin, "A plugin is using UIDesigner but forgot to call UIDesignerAPI.setPlugin first!");
			}

		}.start();
	}
}
