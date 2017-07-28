package com.pau101.fairylights.server.item;

import java.util.List;

import com.pau101.fairylights.FairyLights;
import com.pau101.fairylights.server.fastener.connection.ConnectionType;
import com.pau101.fairylights.server.item.crafting.Recipes;
import com.pau101.fairylights.util.Utils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;

public final class ItemConnectionHangingLights extends ItemConnection {
	public ItemConnectionHangingLights() {
		setCreativeTab(FairyLights.fairyLightsTab);
	}

	@Override
	public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
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

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
		if (isInCreativeTab(tab)) {
			for (EnumDyeColor color : EnumDyeColor.values()) {
				subItems.add(Recipes.makeHangingLights(new ItemStack(this), color));
			}
		}
	}

	@Override
	public ConnectionType getConnectionType() {
		return ConnectionType.HANGING_LIGHTS;
	}
}
