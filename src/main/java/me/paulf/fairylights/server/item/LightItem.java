package me.paulf.fairylights.server.item;

import me.paulf.fairylights.server.block.LightBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;
import java.util.List;

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
                tooltip.add(Component.translatable("item.fairyLights.twinkle").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
            }
            if (tag.contains("colors", Tag.TAG_LIST)) {
                final ListTag colors = tag.getList("colors", Tag.TAG_INT);
                for (int i = 0; i < colors.size(); i++) {
                    tooltip.add(DyeableItem.getColorName(colors.getInt(i)).copy().withStyle(ChatFormatting.GRAY));
                }
            }
        }
    }
}
