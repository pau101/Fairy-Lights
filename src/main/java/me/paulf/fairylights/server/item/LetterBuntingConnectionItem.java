package me.paulf.fairylights.server.item;

import me.paulf.fairylights.server.fastener.connection.*;
import me.paulf.fairylights.util.styledstring.*;
import net.minecraft.client.util.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.util.*;
import net.minecraft.util.text.*;
import net.minecraft.world.*;
import net.minecraftforge.common.util.Constants.*;

import java.util.*;

public class LetterBuntingConnectionItem extends ConnectionItem {
    public LetterBuntingConnectionItem(final Item.Properties properties) {
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
    }

    @Override
    public void fillItemGroup(final ItemGroup tab, final NonNullList<ItemStack> items) {
        if (this.isInGroup(tab)) {
            final ItemStack bunting = new ItemStack(this, 1);
            bunting.getOrCreateTag().put("text", StyledString.serialize(new StyledString()));
            items.add(bunting);
        }
    }

    @Override
    public ConnectionType getConnectionType() {
        return ConnectionType.LETTER_BUNTING;
    }
}
