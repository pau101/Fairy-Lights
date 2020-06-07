package me.paulf.fairylights.server.item;

import me.paulf.fairylights.server.fastener.connection.type.hanginglights.LightBehavior;
import me.paulf.fairylights.util.EmptyProvider;
import me.paulf.fairylights.util.SimpleProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

public interface LightVariant<T extends LightBehavior> {
    final class Holder {
        @CapabilityInject(LightVariant.class)
        public static Capability<LightVariant<?>> CAPABILITY;
    }

    boolean parallelsCord();

    float getSpacing();

    AxisAlignedBB getBounds();

    T createBehavior(final ItemStack stack);

    boolean isOrientable();

    static LazyOptional<LightVariant<?>> get(final ICapabilityProvider provider) {
        return provider.getCapability(Holder.CAPABILITY);
    }

    static ICapabilityProvider provider(final LightVariant<?> variant) {
        return Holder.CAPABILITY == null ? new EmptyProvider() : new SimpleProvider<>(Holder.CAPABILITY, variant);
    }
}
