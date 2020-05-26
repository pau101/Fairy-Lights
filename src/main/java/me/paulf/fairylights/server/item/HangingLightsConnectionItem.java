package me.paulf.fairylights.server.item;

import me.paulf.fairylights.server.fastener.connection.*;
import me.paulf.fairylights.server.item.crafting.*;
import me.paulf.fairylights.util.*;
import net.minecraft.client.util.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.util.*;
import net.minecraft.util.text.*;
import net.minecraft.world.*;
import net.minecraftforge.common.util.Constants.*;

import javax.annotation.*;
import java.util.*;

public final class HangingLightsConnectionItem extends ConnectionItem {
    public HangingLightsConnectionItem(final Properties properties) {
        super(properties);
    }

    @Override
    public void addInformation(final ItemStack stack, @Nullable final World worldIn, final List<ITextComponent> tooltip, final ITooltipFlag flagIn) {
        if (!stack.hasTag()) {
            return;
        }
        final CompoundNBT compound = stack.getTag();
        if (compound.getBoolean("twinkle")) {
            tooltip.add(new TranslationTextComponent("item.fairyLights.twinkle"));
        }
        if (compound.getBoolean("tight")) {
            tooltip.add(new TranslationTextComponent("item.fairyLights.tight"));
        }
        if (compound.contains("pattern", NBT.TAG_LIST)) {
            final ListNBT tagList = compound.getList("pattern", NBT.TAG_COMPOUND);
            final int tagCount = tagList.size();
            if (tagCount > 0) {
                tooltip.add(new TranslationTextComponent("item.fairyLights.pattern"));
            }
            for (int i = 0; i < tagCount; i++) {
                final CompoundNBT lightCompound = tagList.getCompound(i);
                final ITextComponent variant = LightVariant.getLightVariant(lightCompound.getInt("light")).getItem().getName();
                tooltip.add(new TranslationTextComponent("format.pattern.entry", Utils.formatColored(DyeColor.byId(lightCompound.getByte("color")), variant)));
            }
        }
    }

    @Override
    public void fillItemGroup(final ItemGroup tab, final NonNullList<ItemStack> subItems) {
        if (this.isInGroup(tab)) {
            for (final DyeColor color : DyeColor.values()) {
                subItems.add(FLCraftingRecipes.makeHangingLights(new ItemStack(this), color));
            }
        }
    }

    @Override
    public ConnectionType getConnectionType() {
        return ConnectionType.HANGING_LIGHTS;
    }
}
