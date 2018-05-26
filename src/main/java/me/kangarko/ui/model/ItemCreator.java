package me.kangarko.ui.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.meta.SpawnEggMeta;

import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import me.kangarko.ui.menu.MenuButton;
import me.kangarko.ui.menu.MenuButton.DummyButton;
import me.kangarko.ui.util.DesignerUtils;

/**
 * Highly efficient and productive utility class for
 * making custom items.
 *
 * @author kangarko
 */
@Builder
public class ItemCreator {

	private ItemMeta meta;
	private final ItemStack item;

	private final Material material;

	@Builder.Default
	private final int amount = 1;

	@Builder.Default
	private final int data = 0;
	@Builder.Default
	private final short damage = 0;

	/** Requires a compatible item material (like WOOL)! */
	private final DyeColor color;

	/** Requires a compatible item material (like MONSTER_EGG)! */
	private final EntityType monster;

	/** Requires a SKULL_ITEM item material! */
	private final String skullOwner;

	@Singular
	private final List<Enchant> enchants;

	private final String name;
	@Singular
	private final List<String> lores;
	@Singular
	private List<CreatorFlag> flags;
	private final Boolean unbreakable;

	private final boolean hideTags;
	private final boolean glow;

	/**
	 * Set a skull name for the item (skin heads apply). Your item must have the material SKULL_ITEM!
	 */
	public ItemCreator setSkull(String owner) {
		Validate.isTrue(material != null && material == Material.SKULL_ITEM, "Material must be skull item");

		final ItemStack is = make();
		final SkullMeta meta = (SkullMeta) is.getItemMeta();
		meta.setOwner(owner);

		this.meta = meta;

		return this;
	}

	/**
	 * Convert this to a button.
	 */
	public DummyButton makeButton() {
		return MenuButton.makeDummy(this);
	}

	/**
	 * Construct a finished shiny little tool.
	 */
	public ItemStack make() {
		if (item == null)
			Objects.requireNonNull(material, "Material == null!");

		final ItemStack is = item != null ? item.clone() : new ItemStack(material, amount, damage, (byte) (!material.toString().contains("LEATHER") && color != null ? color.getWoolData() : data));
		ItemMeta myMeta = meta != null ? meta.clone() : is.getItemMeta();

		myMeta = myMeta != null ? myMeta : Bukkit.getItemFactory().getItemMeta(material);
		flags = flags == null ? new ArrayList<>() : new ArrayList<>(flags);

		if (myMeta != null) {
			if (color != null && material.toString().contains("LEATHER"))
				((LeatherArmorMeta) myMeta).setColor(color.getColor());

			if (glow) {
				myMeta.addEnchant(Enchantment.DURABILITY, 1, true);

				flags.add(CreatorFlag.HIDE_ENCHANTS);
			}

			if (enchants != null)
				for (final Enchant ench : enchants)
					myMeta.addEnchant(ench.getEnchant(), ench.getLevel(), true);

			if (name != null)
				myMeta.setDisplayName(DesignerUtils.colorize(name));

			if (lores != null) {
				final List<String> coloredLore = new ArrayList<>();

				lores.forEach((line) -> coloredLore.add(DesignerUtils.colorize("&7" + line)));
				myMeta.setLore(coloredLore);
			}

			if (unbreakable != null) {
				flags.add(CreatorFlag.HIDE_ATTRIBUTES);
				flags.add(CreatorFlag.HIDE_UNBREAKABLE);

				try {
					myMeta.setUnbreakable(true);
				} catch (final Throwable t) {
					try {
						myMeta.spigot().setUnbreakable(true);
					} catch (final Throwable tt) {
					} // unsupported
				}
			}

			if (hideTags)
				for (final CreatorFlag f : CreatorFlag.values())
					if (!flags.contains(f))
						flags.add(f);

			if (flags != null)
				try {
					final List<org.bukkit.inventory.ItemFlag> f = new ArrayList<>();

					for (final CreatorFlag flag : flags)
						f.add(org.bukkit.inventory.ItemFlag.valueOf(flag.toString()));

					myMeta.addItemFlags(f.toArray(new org.bukkit.inventory.ItemFlag[f.size()]));
				} catch (final Throwable t) {
				} // unsupported for the current MC version

			if (monster != null) {
				Validate.isTrue(myMeta instanceof SpawnEggMeta, "Cannot make monster egg from " + is.getType());

				try {
					((SpawnEggMeta) meta).setSpawnedType(monster);

				} catch (final Error err) {
					System.out.println("Error creating " + monster + " mob egg from " + is + ". Minecraft incompatible? Got: " + err);
				}
			}

			if (skullOwner != null) {
				Validate.isTrue(material == Material.SKULL_ITEM, "Material must be SKULL_ITEM");

				((SkullMeta) myMeta).setOwner(skullOwner);
			}

			is.setItemMeta(myMeta);
		}

		return is;
	}

	/**
	 * A shortcut to easily build an item with given material, name and a lore.
	 *
	 * The name begins with &r so no need to reset it.
	 * Each lore line begins with a &7 so it is gray by default instead of italics pink.
	 */
	public static ItemCreatorBuilder of(Material mat, String name, @NonNull String... lore) {
		for (int i = 0; i < lore.length; i++)
			lore[i] = "&7" + lore[i];

		return ItemCreator.builder().material(mat).name("&r" + name).lores(Arrays.asList(lore)).hideTags(true);
	}

	/**
	 * Starts building with the selected item.
	 */
	public static ItemCreatorBuilder of(ItemStack copy) {
		return ItemCreator.builder().item(copy);
	}

	/**
	 * Starts building with a selected material.
	 */
	public static ItemCreatorBuilder of(Material mat) {
		return ItemCreator.builder().material(mat);
	}

	/**
	 * Starts building with a selected material and a data.
	 */
	public static ItemCreatorBuilder of(Material mat, int data) {
		return ItemCreator.builder().material(mat).data(data);
	}

	/**
	 * Item flags wrapper to maintain backwards compatibility.
	 */
	public enum CreatorFlag {
		HIDE_ENCHANTS, HIDE_ATTRIBUTES, HIDE_UNBREAKABLE, HIDE_DESTROYS, HIDE_PLACED_ON, HIDE_POTION_EFFECTS;
	}

}