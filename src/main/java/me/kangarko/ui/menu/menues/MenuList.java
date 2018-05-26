package me.kangarko.ui.menu.menues;

import java.util.ArrayList;
import java.util.List;

import me.kangarko.ui.menu.Menu;
import me.kangarko.ui.menu.MenuButton;
import me.kangarko.ui.menu.MenuInventory;

/**
 *  An incremental menu that list items.
 */
public abstract class MenuList extends MenuStandard {

	private final ArrayList<String> list;

	protected MenuList(String listName, Iterable<String> list) {
		this(null, listName, list);
	}

	protected MenuList(Menu parent, String listName, Iterable<String> list) {
		super(parent);

		this.list = new ArrayList<>();
		list.forEach((str) -> this.list.add(str));

		setSize(18 + (9 * (this.list.size() / 9)));
		setTitle(listName + " Menu");
	}

	@Override
	protected final List<MenuButton> getButtonsToAutoRegister() {
		final List<MenuButton> items = new ArrayList<>(getSize());

		for (int i = 0; i < list.size(); i++)
			items.add(getListButton(list.get(i), i) );

		fillSpace(items, 2);
		return items;
	}

	private final void fillSpace(List<MenuButton> items, int preserve) {
		for (int i = items.size(); i < getSize() - preserve; i++)
			items.add(MenuButton.makeEmpty());
	}

	protected abstract MenuButton getListButton(String listName, int listIndex);

	@Override
	protected final void paint(MenuInventory inv) {
		for (final MenuButton item : getButtonsToAutoRegister())
			inv.addItem( item.getItem() );
	}

	@Override
	protected final String[] getInfo() {
		return null;
	}
}
