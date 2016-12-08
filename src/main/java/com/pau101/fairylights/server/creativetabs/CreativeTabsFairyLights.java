package com.pau101.fairylights.server.creativetabs;

import com.pau101.fairylights.FairyLights;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public final class CreativeTabsFairyLights extends CreativeTabs {
	public CreativeTabsFairyLights() {
		super(FairyLights.ID);
	}

	@Override
	public Item getTabIconItem() {
		return FairyLights.hangingLights;
	}
}
