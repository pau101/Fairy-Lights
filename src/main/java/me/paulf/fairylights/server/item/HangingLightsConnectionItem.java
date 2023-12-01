package me.paulf.fairylights.server.item;

import me.paulf.fairylights.FairyLights;
import me.paulf.fairylights.server.connection.ConnectionTypes;
import me.paulf.fairylights.server.item.crafting.FLCraftingRecipes;
import me.paulf.fairylights.server.string.StringType;
import me.paulf.fairylights.util.RegistryObjects;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public final class HangingLightsConnectionItem extends ConnectionItem {
    public HangingLightsConnectionItem(final Properties properties) {
        super(properties, ConnectionTypes.HANGING_LIGHTS);
    }


    @Override
    public void appendHoverText(final ItemStack stack, @Nullable final Level world, final List<Component> tooltip, final TooltipFlag flag) {
        final CompoundTag compound = stack.getTag();
        if (compound != null) {
            final ResourceLocation name = RegistryObjects.getName(FairyLights.STRING_TYPES.get(), getString(compound));
            tooltip.add(Component.translatable("item." + name.getNamespace() + "." + name.getPath()).withStyle(ChatFormatting.GRAY));
        }
        if (compound != null && compound.contains("pattern", Tag.TAG_LIST)) {
            final ListTag tagList = compound.getList("pattern", Tag.TAG_COMPOUND);
            final int tagCount = tagList.size();
            if (tagCount > 0) {
                tooltip.add(Component.empty());
            }
            for (int i = 0; i < tagCount; i++) {
                final ItemStack lightStack = ItemStack.of(tagList.getCompound(i));
                tooltip.add(lightStack.getHoverName());
                lightStack.getItem().appendHoverText(lightStack, world, tooltip, flag);
            }
        }
    }

    public static StringType getString(final CompoundTag tag) {
        return Objects.requireNonNull(FairyLights.STRING_TYPES.get().getValue(ResourceLocation.tryParse(tag.getString("string"))));
    }

    public static void setString(final CompoundTag tag, final StringType string) {
        tag.putString("string", RegistryObjects.getName(FairyLights.STRING_TYPES.get(), string).toString());
    }
}
