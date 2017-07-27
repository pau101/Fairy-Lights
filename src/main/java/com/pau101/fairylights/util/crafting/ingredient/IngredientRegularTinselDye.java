package com.pau101.fairylights.util.crafting.ingredient;

import com.google.common.collect.ImmutableList;
import com.pau101.fairylights.util.OreDictUtils;
import com.pau101.fairylights.util.crafting.ingredient.behavior.BehaviorRegular;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public final class IngredientRegularTinselDye extends IngredientRegularDye {
	public IngredientRegularTinselDye() {
		super(ImmutableList.of(), EMPTY_TOOLTIP, ImmutableList.of(new BehaviorRegular() {
			@Override
			public void matched(ItemStack ingredient, ItemStack output) {
				output.getTagCompound().setByte("color", (byte) OreDictUtils.getDyeMetadata(ingredient));
			}
		}));
	}

	@Override
	public ImmutableList<ImmutableList<ItemStack>> getInput(ItemStack output) {
		NBTTagCompound compound = output.getTagCompound();
		if (compound == null) {
			return ImmutableList.of();
		}
		return ImmutableList.of(OreDictUtils.getDyes(EnumDyeColor.byDyeDamage(compound.getByte("color"))));
	}

	@Override
	public boolean dictatesOutputType() {
		return true;
	}
}
