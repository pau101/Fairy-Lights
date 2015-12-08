package com.pau101.fairylights.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.StatCollector;

import com.pau101.fairylights.FairyLights;
import com.pau101.fairylights.connection.ConnectionLogic;
import com.pau101.fairylights.connection.ConnectionType;

public class ItemConnectionFairyLights extends ItemConnection {
	public ItemConnectionFairyLights() {
		setCreativeTab(null);
	}

	@Override
	public ConnectionType getConnectionType() {
		return ConnectionType.FAIRY_LIGHTS;
	}

	@Override
	public void addInformation(ItemStack itemStack, EntityPlayer holder, List stringLines, boolean currentlyHeld) {
		if (itemStack.hasTagCompound()) {
			NBTTagCompound tagCompound = itemStack.getTagCompound();
			if (tagCompound.getBoolean("twinkle")) {
				stringLines.add(StatCollector.translateToLocal("item.fireworksCharge.flicker"));
			}
			if (tagCompound.getBoolean("tight")) {
				stringLines.add(StatCollector.translateToLocal("item.fairy_lights.tight"));
			}
			if (tagCompound.hasKey("pattern", 9)) {
				NBTTagList tagList = tagCompound.getTagList("pattern", 10);
				int tagCount = tagList.tagCount();
				if (tagCount > 0) {
					stringLines.add(StatCollector.translateToLocal("item.fairy_lights.pattern"));
				}
				for (int i = 0; i < tagCount; i++) {
					NBTTagCompound lightCompound = tagList.getCompoundTagAt(i);
					String localizedLightVariant = StatCollector.translateToLocal(FairyLights.light.getUnlocalizedName() + '.' + LightVariant.getLightVariant(lightCompound.getInteger("light")).getName() + ".name");
					String localizedColor = StatCollector.translateToLocal("item.fireworksCharge." + EnumDyeColor.byDyeDamage(lightCompound.getByte("color")).getUnlocalizedName());
					stringLines.add("  " + StatCollector.translateToLocalFormatted("item.fairy_lights.colored_light", localizedColor, localizedLightVariant));
				}
			}
		}
	}

	@Override
	public int getColorFromItemStack(ItemStack stack, int renderPass) {
		if (renderPass == 0) {
			return super.getColorFromItemStack(stack, renderPass);
		}
		if (stack.hasTagCompound()) {
			NBTTagList tagList = stack.getTagCompound().getTagList("pattern", 10);
			if (tagList.tagCount() > 0) {
				return EnumDyeColor.byDyeDamage(tagList.getCompoundTagAt((renderPass - 1) % tagList.tagCount()).getByte("color")).getMapColor().colorValue;
			}
		}
		return FairyLights.christmas.isOcurringNow() ? renderPass % 2 == 0 ? 0x993333 : 0x7FCC19 : 0xFFD584;
	}
}
