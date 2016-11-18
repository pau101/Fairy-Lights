package com.pau101.fairylights.server.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import com.pau101.fairylights.FairyLights;
import com.pau101.fairylights.server.fastener.Fastener;

public final class CapabilityHandler {
	private CapabilityHandler() {}

	public static final ResourceLocation FASTENER_ID = new ResourceLocation(FairyLights.ID, "fastener");

	@CapabilityInject(Fastener.class)
	public static Capability<Fastener<?>> FASTENER_CAP = null;

	public static void register() {
		CapabilityManager.INSTANCE.register((Class<Fastener<?>>) (Class<?>) Fastener.class, new FastenerStorage(), () -> {
			throw new UnsupportedOperationException();
		});
	}

	public static class FastenerStorage implements IStorage<Fastener<?>> {
		@Override
		public NBTTagCompound writeNBT(Capability<Fastener<?>> capability, Fastener<?> instance, EnumFacing side) {
			return instance.serializeNBT();
		}

		@Override
		public void readNBT(Capability<Fastener<?>> capability, Fastener<?> instance, EnumFacing side, NBTBase nbt) {
			instance.deserializeNBT((NBTTagCompound) nbt);
		}
	}
}
