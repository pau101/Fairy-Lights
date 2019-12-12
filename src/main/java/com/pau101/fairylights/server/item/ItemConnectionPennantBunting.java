package com.pau101.fairylights.server.item;

import com.pau101.fairylights.server.fastener.connection.ConnectionType;
import com.pau101.fairylights.server.item.crafting.Recipes;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;

import java.util.List;

public class ItemConnectionPennantBunting extends ItemConnection {
	public ItemConnectionPennantBunting(Item.Properties properties) {
		super(properties);
	}

	@Override
	public void addInformation(ItemStack stack, World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
		if (!stack.hasTag()) {
			return;
		}
		CompoundNBT compound = stack.getTag();
		if (compound.contains("text", NBT.TAG_COMPOUND)) {
			CompoundNBT text = compound.getCompound("text");
			String val = text.getString("value");
			if (val.length() > 0) {
				tooltip.add(new TranslationTextComponent("format.text", val));
			}
		}
		if (compound.contains("pattern", NBT.TAG_LIST)) {
			ListNBT tagList = compound.getList("pattern", NBT.TAG_COMPOUND);
			int tagCount = tagList.size();
			if (tagCount > 0) {
				tooltip.add(new TranslationTextComponent("item.pennantBunting.colors"));
			}
			for (int i = 0; i < tagCount; i++) {
				CompoundNBT lightCompound = tagList.getCompound(i);
				tooltip.add(new TranslationTextComponent("format.pattern.entry", new TranslationTextComponent("color." + DyeColor.byId(lightCompound.getByte("color")) + ".name")));
			}
		}
	}

	@Override
	public void fillItemGroup(ItemGroup tab, NonNullList<ItemStack> subItems) {
		if (isInGroup(tab)) {
			for (DyeColor color : DyeColor.values()) {
				ItemStack stack = new ItemStack(this);
				stack.getOrCreateTag().putByte("color", (byte) color.getId());
				subItems.add(Recipes.makePennant(stack, color));
			}
		}
	}

	@Override
	public ConnectionType getConnectionType() {
		return ConnectionType.PENNANT_BUNTING;
	}
}
