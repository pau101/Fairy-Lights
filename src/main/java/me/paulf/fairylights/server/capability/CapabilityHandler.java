package me.paulf.fairylights.server.capability;

import me.paulf.fairylights.FairyLights;
import me.paulf.fairylights.server.fastener.Fastener;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

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
		public CompoundNBT writeNBT(Capability<T> capability, T instance, Direction side) {
			return instance.serializeNBT();
		}

		@Override
		public void readNBT(Capability<T> capability, T instance, Direction side, INBT nbt) {
			instance.deserializeNBT((CompoundNBT) nbt);
		}
	}
}
