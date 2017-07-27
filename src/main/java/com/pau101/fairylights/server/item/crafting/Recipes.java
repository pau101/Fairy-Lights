package com.pau101.fairylights.server.item.crafting;

import com.google.common.collect.ImmutableList;
import com.pau101.fairylights.server.item.ItemLight;
import com.pau101.fairylights.util.OreDictUtils;
import com.pau101.fairylights.util.crafting.ingredient.IngredientRegular;
import com.pau101.fairylights.util.crafting.ingredient.IngredientRegularDye;
import net.minecraft.item.ItemStack;

public final class Recipes {
	private Recipes() {}

	public static final IngredientRegular LIGHT_DYE = new IngredientRegularDye() {
		@Override
		public ImmutableList<ImmutableList<ItemStack>> getInput(ItemStack output) {
			return ImmutableList.of(OreDictUtils.getDyes(ItemLight.getLightColor(output.getItemDamage())));
		}

		@Override
		public boolean dictatesOutputType() {
			return true;
		}

		/*@Override
		public void matched(ItemStack ingredient, ItemStack output) {
			output.setItemDamage(Mth.floorInterval(output.getMetadata(), ItemLight.COLOR_COUNT) + OreDictUtils.getDyeMetadata(ingredient));
		}*/
	};
}
