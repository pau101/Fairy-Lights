package com.pau101.fairylights.server.item;

import java.util.List;

import com.pau101.fairylights.FairyLights;
import com.pau101.fairylights.server.fastener.connection.ConnectionType;
import com.pau101.fairylights.server.item.crafting.Recipes;
import com.pau101.fairylights.util.Utils;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.common.util.Constants.NBT;

public final class ItemConnectionHangingLights extends ItemConnection {
	public ItemConnectionHangingLights() {
		setCreativeTab(FairyLights.fairyLightsTab);
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
		if (!stack.hasTagCompound()) {
			return;
		}
		NBTTagCompound compound = stack.getTagCompound();
		if (compound.getBoolean("twinkle")) {
			tooltip.add(I18n.translateToLocal("item.fairyLights.twinkle"));
		}
		if (compound.getBoolean("tight")) {
			tooltip.add(I18n.translateToLocal("item.fairyLights.tight"));
		}
		if (compound.hasKey("pattern", NBT.TAG_LIST)) {
			NBTTagList tagList = compound.getTagList("pattern", NBT.TAG_COMPOUND);
			int tagCount = tagList.tagCount();
			if (tagCount > 0) {
				tooltip.add(I18n.translateToLocal("item.fairyLights.pattern"));
			}
			for (int i = 0; i < tagCount; i++) {
				NBTTagCompound lightCompound = tagList.getCompoundTagAt(i);
				String variant = I18n.translateToLocal(FairyLights.light.getUnlocalizedName() + '.' + LightVariant.getLightVariant(lightCompound.getInteger("light")).getUnlocalizedName() + ".name");
				tooltip.add(I18n.translateToLocalFormatted("format.pattern.entry", Utils.formatColored(EnumDyeColor.byDyeDamage(lightCompound.getByte("color")), variant)));
			}
		}
	}
	public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> subItems) {
		for (EnumDyeColor color : EnumDyeColor.values()) {
			subItems.add(Recipes.makeHangingLights(new ItemStack(FairyLights.hangingLights), color));	
		}
	}

	@Override
	public ConnectionType getConnectionType() {
		return ConnectionType.HANGING_LIGHTS;
	}
}
