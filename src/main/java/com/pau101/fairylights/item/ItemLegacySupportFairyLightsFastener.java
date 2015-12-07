package com.pau101.fairylights.item;

import com.pau101.fairylights.FairyLights;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemLegacySupportFairyLightsFastener extends ItemBlock {
	public ItemLegacySupportFairyLightsFastener(Block block) {
		super(block);
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isSelected) {
		if (!world.isRemote) {
			stack.setItem(FairyLights.fairyLights);
		}
	}
}
