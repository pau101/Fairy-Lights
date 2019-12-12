package me.paulf.fairylights.server.creativetabs;

import me.paulf.fairylights.FairyLights;
import me.paulf.fairylights.server.item.FLItems;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public final class CreativeTabsFairyLights extends ItemGroup {
	public CreativeTabsFairyLights() {
		super(FairyLights.ID);
	}

	@Override
	public ItemStack createIcon() {
		return new ItemStack(FLItems.HANGING_LIGHTS.orElseThrow(IllegalStateException::new));
	}
}
