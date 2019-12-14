package me.paulf.fairylights.util.crafting.ingredient;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.Tag;

import javax.annotation.Nullable;

public class InertOreAuxiliaryIngredient extends OreAuxiliaryIngredient<Void> {
	public InertOreAuxiliaryIngredient(Tag<Item> tag, boolean isRequired, int limit) {
		super(tag, isRequired, limit);
	}

	@Nullable
	@Override
	public final Void accumulator() {
		return null;
	}

	@Override
	public final void consume(Void v, ItemStack ingredient) {}

	@Override
	public final boolean finish(Void v, ItemStack output) {
		return false;
	}
}
