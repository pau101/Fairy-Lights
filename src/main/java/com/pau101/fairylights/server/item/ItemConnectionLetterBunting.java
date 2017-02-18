package com.pau101.fairylights.server.item;

import java.util.List;

import com.pau101.fairylights.FairyLights;
import com.pau101.fairylights.server.fastener.connection.ConnectionType;
import com.pau101.fairylights.util.styledstring.StyledString;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.common.util.Constants.NBT;

public class ItemConnectionLetterBunting extends ItemConnection {
	public ItemConnectionLetterBunting() {
		setCreativeTab(FairyLights.fairyLightsTab);
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
		if (!stack.hasTagCompound()) {
			return;
		}
		NBTTagCompound compound = stack.getTagCompound();
		if (compound.hasKey("text", NBT.TAG_COMPOUND)) {
			NBTTagCompound text = compound.getCompoundTag("text");
			String val = text.getString("value");
			if (val.length() > 0) {
				tooltip.add(I18n.translateToLocalFormatted("format.text", val));
			}
		}
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> items) {
		ItemStack bunting = new ItemStack(item, 1);
		NBTTagCompound compound = new NBTTagCompound();
		compound.setTag("text", StyledString.serialize(new StyledString()));
		bunting.setTagCompound(compound);
		items.add(bunting);
	}

	@Override
	public ConnectionType getConnectionType() {
		return ConnectionType.LETTER_BUNTING;
	}
}
