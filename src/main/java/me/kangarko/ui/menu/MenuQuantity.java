package me.kangarko.ui.menu;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Some menus may have a quantity button that will make it possible to add or subtract
 * amounts of items (buttons) when they are clicked on.
 *
 * This enumeration list all quantity possibilities.
 */
@RequiredArgsConstructor
@Getter
public enum MenuQuantity {
	ONE(1),
	TWO(2),
	FIVE(5),
	TEN(10),
	TWENTY(20);

	private final int amount;

	/**
	 * Get the previous quantity, or the last one if reached the end.
	 */
	public final MenuQuantity previous() {
		final int next = ordinal() - 1;
		final MenuQuantity[] values = MenuQuantity.values();

		return next >= 0 ? values[next] : values[values.length - 1];
	}

	/**
	 * Get the next quantity, or the first one if reached the end.
	 */
	public final MenuQuantity next() {
		final int next = ordinal() + 1;
		final MenuQuantity[] values = MenuQuantity.values();

		return next >= values.length ? values[0] : values[next];
	}
}