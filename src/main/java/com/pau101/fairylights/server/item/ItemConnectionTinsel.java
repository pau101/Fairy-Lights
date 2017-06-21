package com.pau101.fairylights.server.item;

import com.pau101.fairylights.FairyLights;
import com.pau101.fairylights.server.fastener.connection.ConnectionType;
import com.pau101.fairylights.util.Utils;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.translation.I18n;

public final class ItemConnectionTinsel extends ItemConnection {
	public ItemConnectionTinsel() {
		setCreativeTab(FairyLights.fairyLightsTab);
		setHasSubtypes(true);
	}

	@Override
	public String getItemStackDisplayName(ItemStack itemStack) {
		String localizedTinselName = I18n.translateToLocal(super.getUnlocalizedName(itemStack) + ".name");
		if (itemStack.hasTagCompound()) {
			NBTTagCompound compound = itemStack.getTagCompound();
			return Utils.formatColored(EnumDyeColor.byDyeDamage(compound.getByte("color")), localizedTinselName);
		}
		return localizedTinselName;
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		for (int color = 0; color < ItemLight.COLOR_COUNT; color++) {
			ItemStack tinsel = new ItemStack(this, 1);
			NBTTagCompound compound = new NBTTagCompound();
			compound.setByte("color", (byte) color);
			tinsel.setTagCompound(compound);
			items.add(tinsel);
		}
	}

	@Override
	public ConnectionType getConnectionType() {
		return ConnectionType.TINSEL;
	}
}
