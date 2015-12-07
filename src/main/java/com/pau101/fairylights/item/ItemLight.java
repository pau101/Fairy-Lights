package com.pau101.fairylights.item;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import com.pau101.fairylights.FairyLights;

public class ItemLight extends Item {
	private List<String> theBetweenlandsInfo;

	private static final int THE_BETWEENLANDS_INFO_LINE_COUNT = 3;

	public ItemLight() {
		setHasSubtypes(true);
		setCreativeTab(FairyLights.fairyLightsTab);
	}

	@Override
	public String getItemStackDisplayName(ItemStack itemStack) {
		String localizedLightName = StatCollector.translateToLocal(super.getUnlocalizedName(itemStack) + '.' + LightVariant.getLightVariant(itemStack.getMetadata()).getName() + ".name");
		if (itemStack.hasTagCompound()) {
			NBTTagCompound tagCompound = itemStack.getTagCompound();
			String localizedColor = StatCollector.translateToLocal("item.fireworksCharge." + EnumDyeColor.byDyeDamage(tagCompound.getInteger("color")).getUnlocalizedName());
			return StatCollector.translateToLocalFormatted("item.fairy_lights.colored_light", localizedColor, localizedLightName);
		}
		return localizedLightName;
	}

	@Override
	public int getColorFromItemStack(ItemStack itemStack, int renderPass) {
		if (renderPass == 0 || itemStack.getMetadata() == LightVariant.LUXO_BALL.ordinal() || !itemStack.hasTagCompound()) {
			return super.getColorFromItemStack(itemStack, renderPass);
		}
		return EnumDyeColor.byDyeDamage(itemStack.getTagCompound().getInteger("color")).getMapColor().colorValue;
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List tooltip, boolean advanced) {
		if (LightVariant.getLightVariant(stack.getMetadata()) == LightVariant.WEEDWOOD_LANTERN) {
			if (theBetweenlandsInfo == null) {
				theBetweenlandsInfo = new ArrayList<String>(THE_BETWEENLANDS_INFO_LINE_COUNT);
				String formatting = EnumChatFormatting.GRAY + "" + EnumChatFormatting.ITALIC;
				String lineKey = "item.light." + LightVariant.WEEDWOOD_LANTERN.getName() + ".line.";
				for (int line = 0; line < THE_BETWEENLANDS_INFO_LINE_COUNT; line++) {
					theBetweenlandsInfo.add(formatting + StatCollector.translateToLocal(lineKey + line));
				}
			}
			tooltip.addAll(theBetweenlandsInfo);
		}
	}

	@Override
	public void getSubItems(Item baseItem, CreativeTabs tab, List subItems) {
		LightVariant[] variants = LightVariant.values();
		for (int meta = 0; meta < variants.length; meta++) {
			LightVariant variant = variants[meta];
			if (variant != LightVariant.LUXO_BALL) {
				ItemStack baseVariantItemStack = new ItemStack(baseItem, 1, meta);
				for (int color = 0; color < 16; color++) {
					ItemStack variantItemStack = baseVariantItemStack.copy();
					variantItemStack.setTagInfo("color", new NBTTagInt(color));
					subItems.add(variantItemStack);
				}
			}
		}
	}
}
