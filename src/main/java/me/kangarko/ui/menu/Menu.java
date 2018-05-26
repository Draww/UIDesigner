package me.kangarko.ui.menu;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import me.kangarko.ui.UIDesignerAPI;
import me.kangarko.ui.model.OneTimeRunnable;
import me.kangarko.ui.util.ReflectionUtil;

/**
 * Represents a Menu.
 *
 * @author kangarko
 */
public abstract class Menu {

	// --------------------------------------------------------------------------------
	// Static
	// --------------------------------------------------------------------------------

	// A list of running animated tasks when animating menu title
	private static volatile Map<UUID, BukkitTask> animatedTasks = new HashMap<>();

	/**
	 * Tag used for setting player's metadata when the player opens this menu.
	 */
	static final String TAG_CURRENT = "UIDesigner_Current";

	/**
	 * Tag used for setting player's metadata when the player opens this menu, if his previous menu exist.
	 */
	static final String TAG_PREVIOUS = "UIDesigner_Previous";

	/**
	 * Return currently opened menu or null if none.
	 */
	public static Menu getMenu(HumanEntity human) {
		return getMenu0(human, TAG_CURRENT);
	}

	/**
	 * Return previously currently opened menu or null if none.
	 */
	public static Menu getPreviousMenu(HumanEntity human) {
		return getMenu0(human, TAG_PREVIOUS);
	}

	// Search for menu using a tag
	private static Menu getMenu0(HumanEntity human, String tag) {
		if (human.hasMetadata(tag)) {
			final Menu menu = (Menu) human.getMetadata(tag).get(0).value();
			Objects.requireNonNull(menu, "Menu missing from " + human.getName() + "'s Menu meta!");

			return menu;
		}

		return null;
	}

	// --------------------------------------------------------------------------------
	// Actual class
	// --------------------------------------------------------------------------------

	// Buttons to be registered automatically
	private final List<MenuButton> registeredButtons = new ArrayList<>();

	// A special registor that runs once when the menu is displayed
	private final OneTimeRunnable registor;

	/**
	 * The player viewer.
	 *
	 * THIS IS NULL IN YOUR CONSTRUCTOR AND IT IS SET IN THE {@link #displayTo(Player)} METHOD!
	 */
	@Getter(AccessLevel.PROTECTED)
	@Setter(AccessLevel.PROTECTED)
	protected Player viewer;

	/**
	 * Make a new menu (please extend this class).
	 */
	protected Menu() {
		registor = new OneTimeRunnable(() -> registerFields());

		Objects.requireNonNull(UIDesignerAPI.getPlugin(), "A plugin is using UIDesigner but forgot to call UIDesignerAPI.setPlugin first!");
	}

	// --------------------------------------------------------------------------------
	// Dirty reflection
	// --------------------------------------------------------------------------------

	/**
	 * Scan the class and register all fields of type MenuButton to this menu.
	 */
	protected final void registerFields() {
		registeredButtons.clear();

		// Register mild
		{
			final List<MenuButton> buttons = getButtonsToAutoRegister();

			if (buttons != null)
				registeredButtons.addAll(buttons);
		}

		// Register hard way
		{
			Class<?> lookup = getClass();

			do
				for (final Field f : lookup.getDeclaredFields())
					registerField(f);
			while ( Menu.class.isAssignableFrom( (lookup = lookup.getSuperclass()) ) && !Menu.class.equals(lookup) );
		}
	}

	private final void registerField(Field f) {
		f.setAccessible(true);

		final Class<?> type = f.getType();

		if (MenuButton.class.isAssignableFrom(type)) {
			final MenuButton button = (MenuButton) ReflectionUtil.getField(f, this);

			Objects.requireNonNull(button, "Null " + f.getName() + " in " + this);
			registeredButtons.add(button);
		}

		else if (MenuButton[].class.isAssignableFrom(type)) {
			Validate.isTrue(Modifier.isFinal(f.getModifiers()), "Report / Field must be final - " + f.getName());
			final MenuButton[] buttons = (MenuButton[]) ReflectionUtil.getField(f, this);

			Validate.isTrue(buttons != null && buttons.length > 0, "Null " + f.getName() + "[] in " + this);
			registeredButtons.addAll(Arrays.asList(buttons));
		}
	}

	/**
	 * Return list of buttons to auto register. This is for you to add manually, since
	 * your Menu class will be scanned automatically anyways.
	 */
	protected List<MenuButton> getButtonsToAutoRegister() {
		return null;
	}

	// --------------------------------------------------------------------------------
	// Actual menu functions
	// --------------------------------------------------------------------------------

	/**
	 * Show the menu to the player (ignore if he is conversing).
	 */
	public final void displayTo(Player player) {
		displayTo(player, false);
	}

	/**
	 * Show the menu to the player.
	 *
	 * If force is true, we will show the menu even if the player is conversing, otherwise
	 * we tell the player to exit the conversation.
	 */
	public final void displayTo(Player player, boolean force) {
		registor.runIfHasnt();

		if (!force && player.isConversing()) {
			player.sendRawMessage(ChatColor.RED + "You need to quit your conversation before opening this menu.");
			return;
		}

		this.viewer = player;

		final MenuInventory inv = formInventory();

		for (int i = 0; i < inv.getSize(); i++) {
			final ItemStack item = getItemAt(i);

			if (item != null && !inv.isSlotTaken(i))
				inv.setItem(i, item);
		}

		if (UIDesignerAPI.getSound() != null)
			UIDesignerAPI.getSound().play(player);

		{
			final Menu previous = getMenu(player);

			if (previous != null)
				player.setMetadata(TAG_PREVIOUS, new FixedMetadataValue(UIDesignerAPI.getPlugin(), previous));
		}

		inv.display(player);
		player.setMetadata(TAG_CURRENT, new FixedMetadataValue(UIDesignerAPI.getPlugin(), this));
	}

	/**
	 * Sends an animated title for a second.
	 */
	public final void animateTitle(String title) {
		Objects.requireNonNull(getViewer(), "Menu has no assigned player yet!");
		ReflectionUtil.updateInventoryTitle(getViewer(), title);

		final UUID id = getViewer().getUniqueId();
		final BukkitTask oldTask = animatedTasks.remove(id);
		final String old = getTitle();

		if (oldTask != null)
			oldTask.cancel();

		animatedTasks.put(id, new BukkitRunnable() {
			@Override
			public void run() {
				final Menu futureMenu = getMenu(getViewer());

				if (futureMenu != null && futureMenu.getClass().getName().equals(Menu.this.getClass().getName()))
					ReflectionUtil.updateInventoryTitle(getViewer(), old);
			}
		}.runTaskLater(UIDesignerAPI.getPlugin(), 20));
	}

	/**
	 * Get the title of this menu.
	 */
	protected abstract String getTitle();

	/**
	 * Create the inventory for this menu. This is done for you in MenuStandard class!
	 */
	protected abstract MenuInventory formInventory();

	/**
	 * Return the item at the given slot.
	 *
	 * PLEASE OVERWRITE THIS AND RETURN YOUR ITEMS.
	 */
	protected ItemStack getItemAt(int slot) {
		return null;
	}

	/**
	 * Attempt to get a MenuButton from an ItemStack, or return null if not recognized.
	 *
	 * This is used to make your buttons work.
	 */
	public final MenuButton getButton(ItemStack is) {
		registor.runIfHasnt();

		if (is != null)
			for (final MenuButton button : registeredButtons) {
				Objects.requireNonNull(button, "Menu button is null in " + getClass().getSimpleName());
				Objects.requireNonNull(button.getItem(), "ItemStack cannot be null in " + button.getClass().getSimpleName());

				try {
					if (button.getItem().equals(is))
						return button;
				} catch (final NullPointerException ex) {}
			}

		return null;
	}

	/**
	 * Makes a new instance of this menu.
	 *
	 * PLEASE OVERWRITE IF YOUR CLASS HAS CONSTRUCTOR PARAMETERS!
	 */
	public Menu newInstance() {
		try {
			return ReflectionUtil.instatiate(getClass());
		} catch (final Throwable t) {

			try {
				final Object parent = getClass().getMethod("getParent").invoke(getClass());

				if (parent != null)
					return ReflectionUtil.instatiate(getClass(), parent);
			} catch (final Throwable tt) {}
		}

		throw new RuntimeException("Please overwrite newInstance in " + getClass().getSimpleName() + " with your implementation!");
	}

	// --------------------------------------------------------------------------------
	// Events
	// --------------------------------------------------------------------------------

	/**
	 * Handles a click in the menu, by default the more simple method with only player, slot and clicked is used.
	 *
	 * @param item The item in the slot that is being clicked.
	 */
	public void onMenuClick(Player pl, int slot, InventoryAction action, ClickType click, ItemStack cursor, ItemStack clicked, boolean cancelled) {
		onMenuClick(pl, slot, clicked);
	}

	/**
	 * Handles a click in the menu (does nothing unless overwritten).
	 */
	public void onMenuClick(Player pl, int slot, ItemStack clicked) {
	}

	/**
	 * Handles a click in the menu with a button (parses the click to the button).
	 */
	public final void onMenuClick(Player pl, int slot, InventoryAction action, ClickType click, MenuButton button) {
		button.onClickedInMenu(pl, this, click);
	}

	/**
	 * Triggered when the menu is closed.
	 */
	public void onMenuClose(Player pl, Inventory inv) {
	}

	/**
	 * Return if we should cancel an action in the menu.
	 *
	 * By default, ALL actions are cancelled so you can only right/left click buttons (for safety).
	 */
	public boolean isActionAllowed(MenuClickLocation location, int slot, ItemStack clicked, ItemStack cursor) {
		return false;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "{}";
	}
}
