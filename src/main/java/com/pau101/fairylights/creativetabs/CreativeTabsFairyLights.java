package com.pau101.fairylights.creativetabs;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

import com.pau101.fairylights.FairyLights;

public class CreativeTabsFairyLights extends CreativeTabs {
	public CreativeTabsFairyLights() {
		super(FairyLights.MODID);
	}

	@Override
	public Item getTabIconItem() {
		return FairyLights.fairyLights;
	}
}
