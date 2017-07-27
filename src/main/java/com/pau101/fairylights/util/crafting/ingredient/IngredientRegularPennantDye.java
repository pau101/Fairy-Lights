package com.pau101.fairylights.util.crafting.ingredient;

import com.google.common.collect.ImmutableList;
import com.pau101.fairylights.util.OreDictUtils;
import com.pau101.fairylights.util.crafting.ingredient.behavior.BehaviorRegular;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;

public final class IngredientRegularPennantDye extends IngredientRegularDye {
	public IngredientRegularPennantDye() {
		super(ImmutableList.of(), EMPTY_TOOLTIP, ImmutableList.of(new BehaviorRegular() {
			@Override
			public void matched(ItemStack ingredient, ItemStack output) {
				output.setItemDamage(OreDictUtils.getDyeMetadata(ingredient));
			}
		}));
	}

	@Override
	public ImmutableList<ImmutableList<ItemStack>> getInput(ItemStack output) {
		return ImmutableList.of(OreDictUtils.getDyes(EnumDyeColor.byDyeDamage(output.getItemDamage())));
	}

	@Override
	public boolean dictatesOutputType() {
		return true;
	}
}
