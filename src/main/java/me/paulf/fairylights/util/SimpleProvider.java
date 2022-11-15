package me.paulf.fairylights.util;

import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;
import java.util.Objects;

public final class SimpleProvider<T> implements ICapabilityProvider {
    private final Capability<T> capability;

    private final LazyOptional<T> op;

    public SimpleProvider(final Capability<T> capability, final T instance) {
        this.capability = Objects.requireNonNull(capability, "capability");
        this.op = LazyOptional.of(() -> instance);
    }

    @Override
    public <U> LazyOptional<U> getCapability(final Capability<U> capability, @Nullable final Direction facing) {
        return this.capability.orEmpty(capability, this.op);
    }
}
