package me.kangarko.ui;

import org.bukkit.plugin.java.JavaPlugin;

public final class UIDesignerPlugin extends JavaPlugin {

	@Override
	public void onEnable() {
		System.out.println("---------------------------------------------------");
		System.out.println("UIDesigner loaded as a plugin. THIS IS UNSAFE.");
		System.out.println("");
		System.out.println("It is recommended that you shade this in your");
		System.out.println("plugin instead to avoid conflicts later!");
		System.out.println("---------------------------------------------------");
	}
}
