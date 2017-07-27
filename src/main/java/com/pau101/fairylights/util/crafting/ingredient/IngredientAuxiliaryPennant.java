package com.pau101.fairylights.util.crafting.ingredient;

import com.google.common.collect.ImmutableList;
import com.pau101.fairylights.FairyLights;
import com.pau101.fairylights.util.crafting.ingredient.behavior.BehaviorAuxiliary;
import com.pau101.fairylights.util.styledstring.StyledString;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.oredict.OreDictionary;

public final class IngredientAuxiliaryPennant extends IngredientAuxiliaryItem {
	public IngredientAuxiliaryPennant() {
		super(
			ImmutableList.of(),
			list -> list.add("recipe.pennantBunting.pennant"),
			ImmutableList.of(new BehaviorAuxiliary<NBTTagList>() {
				@Override
				public NBTTagList accumulator() {
					return new NBTTagList();
				}

				@Override
				public void consume(NBTTagList pattern, ItemStack ingredient) {
					NBTTagCompound pennant = new NBTTagCompound();
					pennant.setByte("color", (byte) ingredient.getMetadata());
					pattern.appendTag(pennant);
				}

				@Override
				public boolean finish(NBTTagList pattern, ItemStack output) {
					if (pattern.tagCount() > 0) {
						output.setTagInfo("pattern", pattern);
						output.setTagInfo("text", StyledString.serialize(new StyledString()));
					}
					return false;
				}
			}), true, 8,
			new ItemStack(FairyLights.pennant, 1, OreDictionary.WILDCARD_VALUE)
		);
	}

	@Override
	public boolean dictatesOutputType() {
		return true;
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
		ImmutableList.Builder<ImmutableList<ItemStack>> pennants = ImmutableList.builder();
		for (int i = 0; i < pattern.tagCount(); i++) {
			NBTTagCompound pennant = pattern.getCompoundTagAt(i);
			pennants.add(ImmutableList.of(new ItemStack(FairyLights.pennant, 1, pennant.getByte("color"))));
		}
		return pennants.build();
	}
}
