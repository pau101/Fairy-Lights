package com.pau101.fairylights.server.item;

import com.pau101.fairylights.server.fastener.connection.ConnectionType;
import com.pau101.fairylights.util.styledstring.StyledString;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;

import java.util.List;

public class ItemConnectionLetterBunting extends ItemConnection {
	public ItemConnectionLetterBunting(Item.Properties properties) {
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
	}

	@Override
	public void fillItemGroup(ItemGroup tab, NonNullList<ItemStack> items) {
		if (isInGroup(tab)) {
			ItemStack bunting = new ItemStack(this, 1);
			bunting.getOrCreateTag().put("text", StyledString.serialize(new StyledString()));
			items.add(bunting);
		}
	}

	@Override
	public ConnectionType getConnectionType() {
		return ConnectionType.LETTER_BUNTING;
	}
}
