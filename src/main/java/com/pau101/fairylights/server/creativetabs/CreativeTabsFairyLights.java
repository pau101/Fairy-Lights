package com.pau101.fairylights.server.creativetabs;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

import com.pau101.fairylights.FairyLights;

public final class CreativeTabsFairyLights extends CreativeTabs {
	public CreativeTabsFairyLights() {
		super(FairyLights.ID);
	}

	@Override
	public ItemStack getTabIconItem() {
		return new ItemStack(FairyLights.hangingLights);
	}
}
