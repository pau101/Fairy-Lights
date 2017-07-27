package com.pau101.fairylights.util.crafting.ingredient;

import com.google.common.collect.ImmutableList;
import com.pau101.fairylights.FairyLights;
import com.pau101.fairylights.server.item.LightVariant;
import com.pau101.fairylights.util.crafting.ingredient.behavior.BehaviorRegularCopyNBT;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public final class IngredientAuxiliaryAugHangingLights extends IngredientRegularItem {
	public IngredientAuxiliaryAugHangingLights() {
		super(
			ImmutableList.of(), EMPTY_TOOLTIP,
			ImmutableList.of(new BehaviorRegularCopyNBT()),
			new ItemStack(FairyLights.hangingLights)
		);
	}

	@Override
	public ImmutableList<ItemStack> getInputs() {
		ItemStack stack = ingredient.copy();
		stack.setTagCompound(new NBTTagCompound());
		return makeHangingLightsExamples(stack);
	}

	@Override
	public ImmutableList<ImmutableList<ItemStack>> getInput(ItemStack output) {
		ItemStack stack = output.copy();
		NBTTagCompound compound = stack.getTagCompound();
		if (compound == null) {
			return ImmutableList.of();
		}
		stack.setCount(1);
		compound.setBoolean("twinkle", false);
		return ImmutableList.of(ImmutableList.of(stack));
	}

	private static ImmutableList<ItemStack> makeHangingLightsExamples(ItemStack stack) {
		return ImmutableList.of(
			makeHangingLights(stack, EnumDyeColor.CYAN, EnumDyeColor.MAGENTA, EnumDyeColor.CYAN, EnumDyeColor.WHITE),
			makeHangingLights(stack, EnumDyeColor.CYAN, EnumDyeColor.LIGHT_BLUE, EnumDyeColor.CYAN, EnumDyeColor.LIGHT_BLUE),
			makeHangingLights(stack, EnumDyeColor.SILVER, EnumDyeColor.PINK, EnumDyeColor.CYAN, EnumDyeColor.GREEN),
			makeHangingLights(stack, EnumDyeColor.SILVER, EnumDyeColor.PURPLE, EnumDyeColor.SILVER, EnumDyeColor.GREEN),
			makeHangingLights(stack, EnumDyeColor.CYAN, EnumDyeColor.YELLOW, EnumDyeColor.CYAN, EnumDyeColor.PURPLE)
		);
	}

	public static ItemStack makeHangingLights(ItemStack base, EnumDyeColor... colors) {
		ItemStack stack = base.copy();
		NBTTagCompound compound = stack.getTagCompound();
		NBTTagList lights = new NBTTagList();
		for (EnumDyeColor color : colors) {
			NBTTagCompound pennant = new NBTTagCompound();
			pennant.setByte("color", (byte) color.getDyeDamage());
			pennant.setInteger("light", LightVariant.FAIRY.ordinal());
			lights.appendTag(pennant);
		}
		if (compound == null) {
			compound = new NBTTagCompound();
			stack.setTagCompound(compound);
		}
		compound.setTag("pattern", lights);
		compound.setBoolean("twinkle", false);
		compound.setBoolean("tight", false);
		return stack;
	}
}
