package com.pau101.fairylights.server.creativetabs;

import com.pau101.fairylights.FairyLights;

import com.pau101.fairylights.server.item.FLItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public final class CreativeTabsFairyLights extends CreativeTabs {
	public CreativeTabsFairyLights() {
		super(FairyLights.ID);
	}

	@Override
	public ItemStack createIcon() {
		return new ItemStack(FLItems.HANGING_LIGHTS);
	}
}
