package me.paulf.fairylights.server.item;

import java.util.List;

import javax.annotation.Nullable;

import me.paulf.fairylights.server.block.LightBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class LightItem extends BlockItem {
    private final LightBlock light;

    public LightItem(final LightBlock light, final Properties properties) {
        super(light, properties);
        this.light = light;
    }

    @Override
    public LightBlock getBlock() {
        return this.light;
    }

    @Override
    public ICapabilityProvider initCapabilities(final ItemStack stack, @Nullable final CompoundTag nbt) {
        return LightVariant.provider(this.light.getVariant());
    }

    @Override
    public void appendHoverText(final ItemStack stack, @Nullable final Level world, final List<Component> tooltip, final TooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);
        final CompoundTag tag = stack.getTag();
        if (tag != null) {
            if (tag.getBoolean("twinkle")) {
                tooltip.add(new TranslatableComponent("item.fairyLights.twinkle").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
            }
            if (tag.contains("colors", CompoundTag.TAG_LIST)) {
                final ListTag colors = tag.getList("colors", CompoundTag.TAG_INT);
                for (int i = 0; i < colors.size(); i++) {
                    tooltip.add(DyeableItem.getColorName(colors.getInt(i)).withStyle(ChatFormatting.GRAY));
                }
            }
        }
    }
}
