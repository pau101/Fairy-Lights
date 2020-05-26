package me.paulf.fairylights.server.item;

import me.paulf.fairylights.server.fastener.connection.*;
import me.paulf.fairylights.server.item.crafting.*;
import me.paulf.fairylights.util.styledstring.*;
import net.minecraft.client.util.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.util.*;
import net.minecraft.util.text.*;
import net.minecraft.world.*;
import net.minecraftforge.common.util.Constants.*;

import java.util.*;

public class PennantBuntingConnectionItem extends ConnectionItem {
    public PennantBuntingConnectionItem(final Item.Properties properties) {
        super(properties);
    }

    @Override
    public void addInformation(final ItemStack stack, final World world, final List<ITextComponent> tooltip, final ITooltipFlag flag) {
        if (!stack.hasTag()) {
            return;
        }
        final CompoundNBT compound = stack.getTag();
        if (compound.contains("text", NBT.TAG_COMPOUND)) {
            final CompoundNBT text = compound.getCompound("text");
            final StyledString s = StyledString.deserialize(text);
            if (s.length() > 0) {
                tooltip.add(new TranslationTextComponent("format.text", s.toTextComponent()));
            }
        }
        if (compound.contains("pattern", NBT.TAG_LIST)) {
            final ListNBT tagList = compound.getList("pattern", NBT.TAG_COMPOUND);
            final int tagCount = tagList.size();
            if (tagCount > 0) {
                tooltip.add(new TranslationTextComponent("item.pennantBunting.colors"));
            }
            for (int i = 0; i < tagCount; i++) {
                final CompoundNBT lightCompound = tagList.getCompound(i);
                tooltip.add(new TranslationTextComponent("format.pattern.entry", new TranslationTextComponent("color." + DyeColor.byId(lightCompound.getByte("color")) + ".name")));
            }
        }
    }

    @Override
    public void fillItemGroup(final ItemGroup tab, final NonNullList<ItemStack> subItems) {
        if (this.isInGroup(tab)) {
            for (final DyeColor color : DyeColor.values()) {
                final ItemStack stack = new ItemStack(this);
                LightItem.setLightColor(stack, color);
                subItems.add(FLCraftingRecipes.makePennant(stack, color));
            }
        }
    }

    @Override
    public ConnectionType getConnectionType() {
        return ConnectionType.PENNANT_BUNTING;
    }
}
