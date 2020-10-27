package me.paulf.fairylights.server.item;

import me.paulf.fairylights.FairyLights;
import me.paulf.fairylights.server.connection.ConnectionTypes;
import me.paulf.fairylights.server.item.crafting.FLCraftingRecipes;
import me.paulf.fairylights.server.string.StringType;
import me.paulf.fairylights.server.string.StringTypes;
import me.paulf.fairylights.util.RegistryObjects;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public final class HangingLightsConnectionItem extends ConnectionItem {
    public HangingLightsConnectionItem(final Properties properties) {
        super(properties, ConnectionTypes.HANGING_LIGHTS);
    }

    @Override
    public void addInformation(final ItemStack stack, @Nullable final World world, final List<ITextComponent> tooltip, final ITooltipFlag flag) {
        final CompoundNBT compound = stack.getTag();
        if (compound != null) {
            final ResourceLocation name = RegistryObjects.getName(getString(compound));
            tooltip.add(new TranslationTextComponent("item." + name.getNamespace() + "." + name.getPath()).mergeStyle(TextFormatting.GRAY));
        }
        if (compound != null && compound.contains("pattern", NBT.TAG_LIST)) {
            final ListNBT tagList = compound.getList("pattern", NBT.TAG_COMPOUND);
            final int tagCount = tagList.size();
            if (tagCount > 0) {
                tooltip.add(new StringTextComponent(""));
            }
            for (int i = 0; i < tagCount; i++) {
                final ItemStack lightStack = ItemStack.read(tagList.getCompound(i));
                tooltip.add(lightStack.getDisplayName());
                lightStack.getItem().addInformation(lightStack, world, tooltip, flag);
            }
        }
    }

    @Override
    public void fillItemGroup(final ItemGroup tab, final NonNullList<ItemStack> subItems) {
        if (!StringTypes.BLACK_STRING.isPresent() && FairyLights.isForgeInStupidState()) return;
        if (this.isInGroup(tab)) {
            for (final DyeColor color : DyeColor.values()) {
                subItems.add(FLCraftingRecipes.makeHangingLights(new ItemStack(this), color));
            }
        }
    }

    public static StringType getString(final CompoundNBT tag) {
        return Objects.requireNonNull(FairyLights.STRING_TYPES.getValue(ResourceLocation.tryCreate(tag.getString("string"))));
    }

    public static void setString(final CompoundNBT tag, final StringType string) {
        tag.putString("string", RegistryObjects.getName(string).toString());
    }
}
