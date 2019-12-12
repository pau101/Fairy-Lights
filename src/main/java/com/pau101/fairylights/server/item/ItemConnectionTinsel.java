package com.pau101.fairylights.server.item;

import com.pau101.fairylights.server.fastener.connection.ConnectionType;
import com.pau101.fairylights.util.Utils;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;

public final class ItemConnectionTinsel extends ItemConnection {
	public ItemConnectionTinsel(Properties properties) {
		super(properties);
	}

	@Override
	public ITextComponent getDisplayName(ItemStack stack) {
		ITextComponent localizedTinselName = super.getDisplayName(stack);
		if (stack.hasTag()) {
			CompoundNBT compound = stack.getTag();
			return Utils.formatColored(DyeColor.byId(compound.getByte("color")), localizedTinselName);
		}
		return localizedTinselName;
	}

	@Override
	public void fillItemGroup(ItemGroup tab, NonNullList<ItemStack> items) {
		if (isInGroup(tab)) {
			for (DyeColor color : DyeColor.values()) {
				ItemStack tinsel = new ItemStack(this);
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
