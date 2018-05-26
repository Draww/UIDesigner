package me.kangarko.ui.menu;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.bukkit.Material;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.kangarko.ui.model.ItemCreator;
import me.kangarko.ui.util.ReflectionUtil;

/**
 * Represents a button in a menu.
 */
public abstract class MenuButton {

	protected MenuButton() {
	}

	// ----------------------------------------------------------------
	// Button functions
	// ----------------------------------------------------------------

	/**
	 * Handles a click in the menu with this button.
	 */
	public abstract void onClickedInMenu(Player pl, Menu menu, ClickType click);

	/**
	 * Return the ItemStack representation.
	 *
	 * It is advised to use {@link ItemCreator} class for (a much) easier creation.
	 */
	public abstract ItemStack getItem();

	// ----------------------------------------------------------------
	// Static methods
	// ----------------------------------------------------------------

	/**
	 * Makes an empty button (hidden).
	 */
	public static DummyButton makeEmpty() {
		return makeDummy(ItemCreator.of(Material.AIR));
	}

	/**
	 * Makes an info button with a nether star with the provided lores (see below).
	 */
	public static DummyButton makeInfoButton(String... loreLines) {
		return makeInfoButton(Material.NETHER_STAR, loreLines);
	}

	/**
	 * Makes an empty button doing nothing besides displaying the lore information.
	 *
	 * There is a space between the name and the first line of the lore.
	 */
	public static DummyButton makeInfoButton(Material mat, String... loreLines) {
		final List<String> lores = new ArrayList<>();
		lores.add(" ");

		for (final String line : loreLines)
			lores.add("&7" + line);

		return makeDummy(ItemCreator.of(mat).name("&fMenu Information").hideTags(true).lores(lores));
	}

	/**
	 * Makes a button that does nothing with the provided item builder.
	 */
	public static DummyButton makeDummy(ItemCreator.ItemCreatorBuilder builder) {
		return makeDummy(builder.unbreakable(true).build());
	}

	/**
	 * Makes a button that does nothing with the provided item builder.
	 */
	public static DummyButton makeDummy(ItemCreator creator) {
		return new DummyButton(creator.make());
	}


	// ----------------------------------------------------------------
	// Helper classes methods
	// ----------------------------------------------------------------

	/**
	 * The button that doesn't do shit.
	 */
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public static class DummyButton extends MenuButton {

		private final ItemStack item;

		@Override
		public ItemStack getItem() {
			return item;
		}

		@Override
		public final void onClickedInMenu(Player pl, Menu menu, ClickType click) {
		}
	}

	/**
	 * A button that runs a server conversation
	 */
	public final static class MenuButtonConvo extends MenuButton {

		private final ConversationFactory convo;
		@Getter
		private final ItemStack item;

		public MenuButtonConvo(ConversationFactory convo, ItemCreator.ItemCreatorBuilder item) {
			this.convo = convo;
			this.item = item.hideTags(true).build().make();
		}

		@Override
		public final void onClickedInMenu(Player pl, Menu menu, ClickType click) {
			convo.buildConversation(pl).begin();
		}
	}

	/**
	 * A button that opens another menu
	 */
	public final static class MenuButtonTrigger extends MenuButton {

		private final MenuLateBind menuLateBind;
		private final Menu menuToOpen;

		private final ItemStack item;

		public MenuButtonTrigger(Class<? extends Menu> cl, Material material, String name, String... lore) {
			this(cl, ItemCreator.of(material, name, lore));
		}

		public MenuButtonTrigger(Class<? extends Menu> cl, ItemCreator.ItemCreatorBuilder item) {
			this(null, () -> ReflectionUtil.instatiate(cl), item.hideTags(true).build().make());
		}

		public MenuButtonTrigger(MenuLateBind menuLateBind, ItemCreator.ItemCreatorBuilder item) {
			this(null, menuLateBind, item.hideTags(true).build().make());
		}

		public MenuButtonTrigger(MenuLateBind menuLateBind, ItemStack item) {
			this(null, menuLateBind, item);
		}

		public MenuButtonTrigger(Menu menu, Material material, String name, String... lore) {
			this(menu, ItemCreator.of(material, name, lore));
		}

		public MenuButtonTrigger(Menu menu, ItemCreator.ItemCreatorBuilder item) {
			this(menu, null, item.hideTags(true).build().make());
		}

		public MenuButtonTrigger(Menu menu, ItemStack item) {
			this(menu, null, item);
		}

		private MenuButtonTrigger(Menu menuToOpen, MenuLateBind menuLateBind, ItemStack item) {
			this.menuToOpen = menuToOpen;
			this.menuLateBind = menuLateBind;
			this.item = item;
		}

		@Override
		public ItemStack getItem() {
			return item;
		}

		@Override
		public final void onClickedInMenu(Player pl, Menu menu, ClickType click) {
			if (menuLateBind != null)
				menuLateBind.getMenu().displayTo(pl);

			else {
				Objects.requireNonNull(menuToOpen, "Report / Neither of menu types is set!");
				menuToOpen.displayTo(pl);
			}
		}
	}

	@Override
	public final String toString() {
		return getClass().getSimpleName() + "{" + getItem().getType() + "}";
	}

	/**
	 * A special case used when you get infinite loop when making
	 * a new menu instance, Use this to delay the constructor loading.
	 */
	public interface MenuLateBind {
		public Menu getMenu();
	}
}
