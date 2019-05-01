package com.pau101.fairylights.server.capability;

import com.pau101.fairylights.FairyLights;
import com.pau101.fairylights.server.fastener.Fastener;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
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
		public NBTTagCompound writeNBT(Capability<T> capability, T instance, EnumFacing side) {
			return instance.serializeNBT();
		}

		@Override
		public void readNBT(Capability<T> capability, T instance, EnumFacing side, NBTBase nbt) {
			instance.deserializeNBT((NBTTagCompound) nbt);
		}
	}
}
