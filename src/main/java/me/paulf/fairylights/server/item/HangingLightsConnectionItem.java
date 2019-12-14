package me.paulf.fairylights.server.item;

import me.paulf.fairylights.server.fastener.connection.ConnectionType;
import me.paulf.fairylights.server.item.crafting.FLCraftingRecipes;
import me.paulf.fairylights.util.Utils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;

import javax.annotation.Nullable;
import java.util.List;

public final class HangingLightsConnectionItem extends ConnectionItem {
	public HangingLightsConnectionItem(Properties properties) {
		super(properties);
	}

	@Override
	public void addInformation(final ItemStack stack, @Nullable final World worldIn, final List<ITextComponent> tooltip, final ITooltipFlag flagIn) {
		if (!stack.hasTag()) {
			return;
		}
		CompoundNBT compound = stack.getTag();
		if (compound.getBoolean("twinkle")) {
			tooltip.add(new TranslationTextComponent("item.fairyLights.twinkle"));
		}
		if (compound.getBoolean("tight")) {
			tooltip.add(new TranslationTextComponent("item.fairyLights.tight"));
		}
		if (compound.contains("pattern", NBT.TAG_LIST)) {
			ListNBT tagList = compound.getList("pattern", NBT.TAG_COMPOUND);
			int tagCount = tagList.size();
			if (tagCount > 0) {
				tooltip.add(new TranslationTextComponent("item.fairyLights.pattern"));
			}
			for (int i = 0; i < tagCount; i++) {
				CompoundNBT lightCompound = tagList.getCompound(i);
				ITextComponent variant = LightVariant.getLightVariant(lightCompound.getInt("light")).getItem().getName();
				tooltip.add(new TranslationTextComponent("format.pattern.entry", Utils.formatColored(DyeColor.byId(lightCompound.getByte("color")), variant)));
			}
		}
	}

	@Override
	public void fillItemGroup(final ItemGroup tab, final NonNullList<ItemStack> subItems) {
		if (isInGroup(tab)) {
			for (DyeColor color : DyeColor.values()) {
				subItems.add(FLCraftingRecipes.makeHangingLights(new ItemStack(this), color));
			}
		}
	}

	@Override
	public ConnectionType getConnectionType() {
		return ConnectionType.HANGING_LIGHTS;
	}
}
