package me.paulf.fairylights.server.item;

import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import me.paulf.fairylights.FairyLights;
import me.paulf.fairylights.server.connection.ConnectionTypes;
import me.paulf.fairylights.server.item.crafting.FLCraftingRecipes;
import me.paulf.fairylights.server.string.StringType;
import me.paulf.fairylights.util.RegistryObjects;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public final class HangingLightsConnectionItem extends ConnectionItem {
    public HangingLightsConnectionItem(final Properties properties) {
        super(properties, ConnectionTypes.HANGING_LIGHTS);
    }

    @Override
    public void appendHoverText(final ItemStack stack, @Nullable final Level world, final List<Component> tooltip, final TooltipFlag flag) {
        final CompoundTag compound = stack.getTag();
        if (compound != null) {
            final ResourceLocation name = RegistryObjects.getName(getString(compound));
            tooltip.add(new TranslatableComponent("item." + name.getNamespace() + "." + name.getPath()).withStyle(ChatFormatting.GRAY));
        }
        if (compound != null && compound.contains("pattern", CompoundTag.TAG_LIST)) {
            final ListTag tagList = compound.getList("pattern", CompoundTag.TAG_COMPOUND);
            final int tagCount = tagList.size();
            if (tagCount > 0) {
                tooltip.add(new TextComponent(""));
            }
            for (int i = 0; i < tagCount; i++) {
                final ItemStack lightStack = ItemStack.of(tagList.getCompound(i));
                tooltip.add(lightStack.getDisplayName());
                lightStack.getItem().appendHoverText(lightStack, world, tooltip, flag);
            }
        }
    }

    @Override
    public void fillItemCategory(final CreativeModeTab tab, final NonNullList<ItemStack> subItems) {
        if (this.allowdedIn(tab)) {
            for (final DyeColor color : DyeColor.values()) {
                subItems.add(FLCraftingRecipes.makeHangingLights(new ItemStack(this), color));
            }
        }
    }

    public static StringType getString(final CompoundTag tag) {
        return Objects.requireNonNull(FairyLights.STRING_TYPES.getValue(ResourceLocation.tryParse(tag.getString("string"))));
    }

    public static void setString(final CompoundTag tag, final StringType string) {
        tag.putString("string", RegistryObjects.getName(string).toString());
    }
}
