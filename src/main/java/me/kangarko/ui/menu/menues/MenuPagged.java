package me.kangarko.ui.menu.menues;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import lombok.val;
import me.kangarko.ui.menu.Menu;
import me.kangarko.ui.menu.MenuButton;
import me.kangarko.ui.model.ItemCreator;
import me.kangarko.ui.model.PosuvnikMenu;
import me.kangarko.ui.util.DesignerUtils;
import me.kangarko.ui.util.ReflectionUtil;

/**
 * Pretty advanced menu allowing you to list items.
 *
 * The menu grows with size and provides Next and Previous button for listing pages.
 */
public abstract class MenuPagged<T> extends MenuStandard {

	@Getter
	private final Map<Integer, List<T>> pages;

	@Getter
	private int page;

	private MenuButton nextButton;
	private MenuButton prevButton;

	protected MenuPagged(int cellSize, Menu parent, Iterable<T> pages) {
		this(cellSize, parent, false, pages);
	}

	protected MenuPagged(int cellSize, Menu parent, boolean makeNewInstance, Iterable<T> pages) {
		this(cellSize, 1, parent, makeNewInstance, pages);
	}

	private MenuPagged(int cellSize, int page, Menu parent, boolean makeNewInstance, Iterable<T> values) {
		super(parent, makeNewInstance);

		this.page = page;
		this.pages = PosuvnikMenu.strany(cellSize, values);

		setSize(9 + cellSize);
		setTitleAndButtons();
	}

	private final void setTitleAndButtons() {
		final boolean hasPages = pages.size() > 1;

		{ // Set title
			final String title = getMenuTitle() + (hasPages ? " &8" + page + "/" + pages.size() : "");

			if (getViewer() != null)
				ReflectionUtil.updateInventoryTitle(getViewer(), title);
			else
				setTitle(title);
		}

		{ // Set buttons
			this.prevButton = hasPages ? new MenuButton() {
				final boolean canGo = page > 1;

				@Override
				public void onClickedInMenu(Player pl, Menu menu, ClickType click) {
					if (canGo) {
						MenuPagged.this.page = DesignerUtils.range(page - 1, 1, pages.size());

						reinit();
					}
				}

				@Override
				public ItemStack getItem() {
					return ItemCreator.of(Material.INK_SACK, canGo ? 10 : 8).name("&8<< &fPage " + (page - 1)).build().make();
				}
			} : MenuButton.makeEmpty();

			this.nextButton = hasPages ? new MenuButton() {
				final boolean canGo = page < pages.size();

				@Override
				public void onClickedInMenu(Player pl, Menu menu, ClickType click) {
					if (canGo) {
						MenuPagged.this.page = DesignerUtils.range(page + 1, 1, pages.size());

						reinit();
					}
				}

				@Override
				public ItemStack getItem() {
					return ItemCreator.of(Material.INK_SACK, canGo ? 10 : 8).name("&fPage " + (page + 1) + " &8>>").build().make();
				}
			} : MenuButton.makeEmpty();
		}
	}

	private final void reinit() {
		setTitleAndButtons();
		restartMenu(null);

		getViewer().playSound(getViewer().getLocation(), Sound.BLOCK_ANVIL_HIT, 1, 1);
	}

	protected abstract String getMenuTitle();

	@Override
	protected ItemStack getItemAt(int slot) {
		if (slot < getValues().size()) {
			final T object = getValues().get(slot);

			if (object != null)
				return convertToItemStack(object);
		}

		if (slot == getSize() - 6)
			return prevButton.getItem();

		if (slot == getSize() - 4)
			return nextButton.getItem();

		return null;
	}

	protected abstract ItemStack convertToItemStack(T object);

	@Override
	public final void onMenuClick(Player pl, int slot, InventoryAction action, ClickType click, ItemStack cursor, ItemStack clicked, boolean cancelled) {
		if (slot < getValues().size()) {
			final T obj = getValues().get(slot);

			if (obj != null) {
				val prevType = pl.getOpenInventory().getType();
				onMenuClickPaged(pl, obj, click);

				if (updateButtonOnClick()) {
					Validate.isTrue(prevType == pl.getOpenInventory().getType(), "Inventory changed in the meanwhile from " + prevType + " to " + pl.getOpenInventory().getType());

					pl.getOpenInventory().getTopInventory().setItem(slot, getItemAt(slot));
				}
			}
		}
	}

	@Override
	public final void onMenuClick(Player pl, int slot, ItemStack clicked) {
		throw new RuntimeException("Simplest click unsupported");
	}

	protected abstract void onMenuClickPaged(Player pl, T object, ClickType click);

	protected boolean updateButtonOnClick() {
		return true;
	}

	protected final List<T> getValues() {
		Validate.isTrue(pages.containsKey(page - 1), "Je len " + pages.size() + " stran, nie " + page + " stran!");

		return pages.get(page - 1);
	}
}
