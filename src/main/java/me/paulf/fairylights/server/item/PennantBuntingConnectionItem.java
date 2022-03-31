package me.paulf.fairylights.server.item;

import me.paulf.fairylights.server.connection.ConnectionTypes;
import me.paulf.fairylights.server.item.crafting.FLCraftingRecipes;
import me.paulf.fairylights.util.styledstring.StyledString;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;

import java.util.List;

public class PennantBuntingConnectionItem extends ConnectionItem {
    public PennantBuntingConnectionItem(final Item.Properties properties) {
        super(properties, ConnectionTypes.PENNANT_BUNTING);
    }

    @Override
    public void func_77624_a(final ItemStack stack, final World world, final List<ITextComponent> tooltip, final ITooltipFlag flag) {
        final CompoundNBT compound = stack.func_77978_p();
        if (compound == null) {
            return;
        }
        if (compound.func_150297_b("text", NBT.TAG_COMPOUND)) {
            final CompoundNBT text = compound.func_74775_l("text");
            final StyledString s = StyledString.deserialize(text);
            if (s.length() > 0) {
                tooltip.add(new TranslationTextComponent("format.fairylights.text", s.toTextComponent()).func_240699_a_(TextFormatting.GRAY));
            }
        }
        if (compound.func_150297_b("pattern", NBT.TAG_LIST)) {
            final ListNBT tagList = compound.func_150295_c("pattern", NBT.TAG_COMPOUND);
            final int tagCount = tagList.size();
            if (tagCount > 0) {
                tooltip.add(new StringTextComponent(""));
            }
            for (int i = 0; i < tagCount; i++) {
                final ItemStack item = ItemStack.func_199557_a(tagList.func_150305_b(i));
                tooltip.add(item.func_200301_q());
            }
        }
    }

    @Override
    public void func_150895_a(final ItemGroup tab, final NonNullList<ItemStack> subItems) {
        if (this.func_194125_a(tab)) {
            for (final DyeColor color : DyeColor.values()) {
                final ItemStack stack = new ItemStack(this);
                DyeableItem.setColor(stack, color);
                subItems.add(FLCraftingRecipes.makePennant(stack, color));
            }
        }
    }
}
