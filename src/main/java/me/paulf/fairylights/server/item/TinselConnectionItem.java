package me.paulf.fairylights.server.item;

import me.paulf.fairylights.server.fastener.connection.ConnectionType;
import me.paulf.fairylights.util.Utils;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;

public final class TinselConnectionItem extends ConnectionItem {
    public TinselConnectionItem(final Properties properties) {
        super(properties);
    }

    @Override
    public ITextComponent getDisplayName(final ItemStack stack) {
        final ITextComponent localizedTinselName = super.getDisplayName(stack);
        if (stack.hasTag()) {
            final CompoundNBT compound = stack.getTag();
            return Utils.formatColored(DyeColor.byId(compound.getByte("color")), localizedTinselName);
        }
        return localizedTinselName;
    }

    @Override
    public void fillItemGroup(final ItemGroup tab, final NonNullList<ItemStack> items) {
        if (this.isInGroup(tab)) {
            for (final DyeColor color : DyeColor.values()) {
                final ItemStack tinsel = new ItemStack(this);
                tinsel.getOrCreateTag().putByte("color", (byte) color.getId());
                items.add(tinsel);
            }
        }
    }

    @Override
    public ConnectionType getConnectionType() {
        return ConnectionType.TINSEL;
    }
}
