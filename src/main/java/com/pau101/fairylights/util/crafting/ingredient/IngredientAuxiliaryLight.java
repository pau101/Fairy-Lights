package com.pau101.fairylights.util.crafting.ingredient;

import com.google.common.collect.ImmutableList;
import com.pau101.fairylights.FairyLights;
import com.pau101.fairylights.server.item.ItemLight;
import com.pau101.fairylights.util.crafting.ingredient.behavior.BehaviorAuxiliary;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.oredict.OreDictionary;

public final class IngredientAuxiliaryLight extends IngredientAuxiliaryItem {
	public IngredientAuxiliaryLight(boolean isRequired) {
		super(
			ImmutableList.of(),
			list -> list.add("recipe.hangingLights.light"),
			ImmutableList.of(new BehaviorAuxiliary<NBTTagList>() {
				@Override
				public NBTTagList accumulator() {
					return new NBTTagList();
				}

				@Override
				public void consume(NBTTagList pattern, ItemStack ingredient) {
					int variant = ingredient.getMetadata();
					NBTTagCompound light = new NBTTagCompound();
					light.setInteger("light", ItemLight.getLightVariantOrdinal(variant));
					light.setByte("color", ItemLight.getLightColorOrdinal(variant));
					pattern.appendTag(light);
				}

				@Override
				public boolean finish(NBTTagList pattern, ItemStack output) {
					if (pattern.tagCount() > 0) {
						output.setTagInfo("pattern", pattern);
					}
					return false;
				}
			}), isRequired, 8,
			new ItemStack(FairyLights.light, 1, OreDictionary.WILDCARD_VALUE)
		);
	}

	@Override
	public ImmutableList<ImmutableList<ItemStack>> getInput(ItemStack output) {
		NBTTagCompound compound = output.getTagCompound();
		if (compound == null) {
			return ImmutableList.of();
		}
		NBTTagList pattern = compound.getTagList("pattern", Constants.NBT.TAG_COMPOUND);
		if (pattern.hasNoTags()) {
			return ImmutableList.of();
		}
		ImmutableList.Builder<ImmutableList<ItemStack>> lights = ImmutableList.builder();
		for (int i = 0; i < pattern.tagCount(); i++) {
			NBTTagCompound light = pattern.getCompoundTagAt(i);
			int meta = light.getInteger("light") * ItemLight.COLOR_COUNT + light.getByte("color");
			lights.add(ImmutableList.of(new ItemStack(FairyLights.light, 1, meta)));
		}
		return lights.build();
	}

	@Override
	public boolean dictatesOutputType() {
		return true;
	}
}
