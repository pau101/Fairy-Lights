package me.paulf.fairylights.util;

import javax.annotation.Nullable;

import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

public final class EmptyProvider implements ICapabilityProvider {
    @Override
    public <T> LazyOptional<T> getCapability(final Capability<T> cap, @Nullable final Direction side) {
        return LazyOptional.empty();
    }
}
