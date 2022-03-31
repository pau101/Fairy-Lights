package me.paulf.fairylights.server.item;

import me.paulf.fairylights.server.connection.ConnectionTypes;
import me.paulf.fairylights.util.styledstring.StyledString;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;

import java.util.List;

public class LetterBuntingConnectionItem extends ConnectionItem {
    public LetterBuntingConnectionItem(final Item.Properties properties) {
        super(properties, ConnectionTypes.LETTER_BUNTING);
    }

    @Override
    public void func_77624_a(final ItemStack stack, final World world, final List<ITextComponent> tooltip, final ITooltipFlag flag) {
        if (!stack.func_77942_o()) {
            return;
        }
        final CompoundNBT compound = stack.func_77978_p();
        if (compound.func_150297_b("text", NBT.TAG_COMPOUND)) {
            final CompoundNBT text = compound.func_74775_l("text");
            final StyledString s = StyledString.deserialize(text);
            if (s.length() > 0) {
                tooltip.add(new TranslationTextComponent("format.fairylights.text", s.toTextComponent()).func_240699_a_(TextFormatting.GRAY));
            }
        }
    }

    @Override
    public void func_150895_a(final ItemGroup tab, final NonNullList<ItemStack> items) {
        if (this.func_194125_a(tab)) {
            final ItemStack bunting = new ItemStack(this, 1);
            bunting.func_196082_o().func_218657_a("text", StyledString.serialize(new StyledString()));
            items.add(bunting);
        }
    }
}
