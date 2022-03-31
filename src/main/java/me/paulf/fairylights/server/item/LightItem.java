package me.paulf.fairylights.server.item;

import me.paulf.fairylights.server.block.LightBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.item.Item.Properties;

public class LightItem extends BlockItem {
    private final LightBlock light;

    public LightItem(final LightBlock light, final Properties properties) {
        super(light, properties);
        this.light = light;
    }

    @Override
    public LightBlock func_179223_d() {
        return this.light;
    }

    @Override
    public ICapabilityProvider initCapabilities(final ItemStack stack, @Nullable final CompoundNBT nbt) {
        return LightVariant.provider(this.light.getVariant());
    }

    @Override
    public void func_77624_a(final ItemStack stack, @Nullable final World world, final List<ITextComponent> tooltip, final ITooltipFlag flag) {
        super.func_77624_a(stack, world, tooltip, flag);
        final CompoundNBT tag = stack.func_77978_p();
        if (tag != null) {
            if (tag.func_74767_n("twinkle")) {
                tooltip.add(new TranslationTextComponent("item.fairyLights.twinkle").func_240701_a_(TextFormatting.GRAY, TextFormatting.ITALIC));
            }
            if (tag.func_150297_b("colors", Constants.NBT.TAG_LIST)) {
                final ListNBT colors = tag.func_150295_c("colors", Constants.NBT.TAG_INT);
                for (int i = 0; i < colors.size(); i++) {
                    tooltip.add(DyeableItem.getColorName(colors.func_186858_c(i)).func_240699_a_(TextFormatting.GRAY));
                }
            }
        }
    }
}
