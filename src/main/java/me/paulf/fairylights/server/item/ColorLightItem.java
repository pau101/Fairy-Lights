package me.paulf.fairylights.server.item;

import me.paulf.fairylights.server.block.LightBlock;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.Constants;

import net.minecraft.item.Item.Properties;

public class ColorLightItem extends LightItem {
    public ColorLightItem(final LightBlock light, final Properties properties) {
        super(light, properties);
    }

    @Override
    public ITextComponent func_200295_i(final ItemStack stack) {
        final CompoundNBT tag = stack.func_77978_p();
        if (tag != null && tag.func_150297_b("colors", Constants.NBT.TAG_LIST)) {
            return new TranslationTextComponent("format.fairylights.color_changing", super.func_200295_i(stack));
        }
        return DyeableItem.getDisplayName(stack, super.func_200295_i(stack));
    }

    @Override
    public void func_150895_a(final ItemGroup group, final NonNullList<ItemStack> items) {
        if (this.func_194125_a(group)) {
            for (final DyeColor dye : DyeColor.values()) {
                items.add(DyeableItem.setColor(new ItemStack(this), dye));
            }
        }
    }
}
