package com.pau101.fairylights.server.integration.jei;

import javax.annotation.Nullable;

import mezz.jei.api.ISubtypeRegistry.ISubtypeInterpreter;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants.NBT;

public final class TinselSubtypeInterpreter implements ISubtypeInterpreter {
	@Nullable
	@Override
	public String getSubtypeInfo(ItemStack stack) {
		NBTTagCompound compound = stack.getTagCompound();
		if (compound != null && compound.hasKey("color", NBT.TAG_BYTE)) {
			return EnumDyeColor.byDyeDamage(compound.getByte("color")).getName();
		}
		return null;
	}
}
