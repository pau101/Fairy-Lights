package me.paulf.fairylights.server.creativetabs;

import me.paulf.fairylights.*;
import me.paulf.fairylights.server.item.*;
import net.minecraft.item.*;

public final class FairyLightsItemGroup extends ItemGroup {
    public FairyLightsItemGroup() {
        super(FairyLights.ID);
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(FLItems.HANGING_LIGHTS.orElseThrow(IllegalStateException::new));
    }
}
