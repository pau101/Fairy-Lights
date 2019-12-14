package me.paulf.fairylights.server.creativetabs;

import me.paulf.fairylights.FairyLights;
import me.paulf.fairylights.server.item.FLItems;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public final class FairyLightsItemGroup extends ItemGroup {
	public FairyLightsItemGroup() {
		super(FairyLights.ID);
	}

	@Override
	public ItemStack createIcon() {
		return new ItemStack(FLItems.HANGING_LIGHTS.orElseThrow(IllegalStateException::new));
	}
}
