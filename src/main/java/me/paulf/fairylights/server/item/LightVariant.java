package me.paulf.fairylights.server.item;

import me.paulf.fairylights.util.EmptyProvider;
import me.paulf.fairylights.util.SimpleProvider;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

public interface LightVariant {
    enum Placement {
        UPRIGHT,
        OUTWARD,
        ONWARD
    }

    final class Holder {
        @CapabilityInject(LightVariant.class)
        public static Capability<LightVariant> CAPABILITY;
    }

    String getName();

    boolean parallelsCord();

    float getSpacing();

    float getWidth();

    float getHeight();

    float getTwinkleChance();

    int getTickCycle();

    boolean alwaysDoTwinkleLogic();

    Placement getPlacement();

    static LazyOptional<LightVariant> get(final ICapabilityProvider provider) {
        return provider.getCapability(Holder.CAPABILITY);
    }

    static ICapabilityProvider provider(final LightVariant variant) {
        return Holder.CAPABILITY == null ? new EmptyProvider() : new SimpleProvider<>(Holder.CAPABILITY, variant);
    }
}
