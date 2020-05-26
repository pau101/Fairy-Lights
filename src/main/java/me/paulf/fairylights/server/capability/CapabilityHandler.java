package me.paulf.fairylights.server.capability;

import me.paulf.fairylights.*;
import me.paulf.fairylights.server.fastener.*;
import net.minecraft.nbt.*;
import net.minecraft.util.*;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.capabilities.Capability.*;

public final class CapabilityHandler {
    private CapabilityHandler() {}

    public static final ResourceLocation FASTENER_ID = new ResourceLocation(FairyLights.ID, "fastener");

    @CapabilityInject(Fastener.class)
    public static Capability<Fastener<?>> FASTENER_CAP = null;

    public static void register() {
        CapabilityManager.INSTANCE.register(Fastener.class, new FastenerStorage<>(), () -> {
            throw new UnsupportedOperationException();
        });
    }

    public static class FastenerStorage<T extends Fastener<?>> implements IStorage<T> {
        @Override
        public CompoundNBT writeNBT(final Capability<T> capability, final T instance, final Direction side) {
            return instance.serializeNBT();
        }

        @Override
        public void readNBT(final Capability<T> capability, final T instance, final Direction side, final INBT nbt) {
            instance.deserializeNBT((CompoundNBT) nbt);
        }
    }
}
