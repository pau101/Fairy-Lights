package me.paulf.fairylights.server.item;

import me.paulf.fairylights.server.block.LightBlock;
import me.paulf.fairylights.util.Utils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.List;

public final class LightItem extends BlockItem {
    private final LightBlock light;

    public LightItem(final LightBlock light, final Properties properties) {
        super(light, properties);
        this.light = light;
    }

    @Override
    public ICapabilityProvider initCapabilities(final ItemStack stack, @Nullable final CompoundNBT nbt) {
        return LightVariant.provider(this.light.getVariant());
    }

    @Override
    public ITextComponent getDisplayName(final ItemStack stack) {
        return Utils.formatColored(LightItem.getLightColor(stack), super.getDisplayName(stack));
    }

    @Override
    public void addInformation(final ItemStack stack, @Nullable final World world, final List<ITextComponent> tooltip, final ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);
        final CompoundNBT tag = stack.getTag();
        if (tag != null) {
            if (tag.getBoolean("twinkle")) {
                tooltip.add(new TranslationTextComponent("item.fairyLights.twinkle"));
            }
        }
    }

    @Override
    public void fillItemGroup(final ItemGroup group, final NonNullList<ItemStack> items) {
        if (this.isInGroup(group)) {
            for (final DyeColor dye : DyeColor.values()) {
                items.add(LightItem.setLightColor(new ItemStack(this), dye));
            }
        }
    }

    public static DyeColor getLightColor(final ItemStack stack) {
        final CompoundNBT tag = stack.getTag();
        if (tag != null && tag.contains("color", Constants.NBT.TAG_ANY_NUMERIC)) {
            return DyeColor.byId(tag.getByte("color"));
        }
        return DyeColor.byId(Math.floorMod((int) (Util.milliTime() / 1500), 16));
    }

    public static ItemStack setLightColor(final ItemStack stack, final DyeColor color) {
        setLightColor(stack.getOrCreateTag(), color);
        return stack;
    }

    public static void setLightColor(final CompoundNBT nbt, final DyeColor color) {
        nbt.putByte("color", (byte) color.getId());
    }

    public static int getColorValue(final DyeColor color) {
        if (color == DyeColor.BLACK) {
            return 0x323232;
        }
        if (color == DyeColor.GRAY) {
            return 0x606060;
        }
        final float[] rgb = color.getColorComponentValues();
        return (int) (rgb[0] * 255) << 16 | (int) (rgb[1] * 0xFF) << 8 | (int) (rgb[2] * 255);
    }
}
