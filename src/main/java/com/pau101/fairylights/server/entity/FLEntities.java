package com.pau101.fairylights.server.entity;

import com.pau101.fairylights.FairyLights;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class FLEntities {
	private FLEntities() {}

	public static final DeferredRegister<EntityType<?>> REG = new DeferredRegister<>(ForgeRegistries.ENTITIES, FairyLights.ID);

	public static final RegistryObject<EntityType<EntityFenceFastener>> FASTENER = REG.register("fastener", () ->
		EntityType.Builder.<EntityFenceFastener>create(EntityFenceFastener::new, EntityClassification.MISC)
			.size(1.15F, 2.8F)
			.setTrackingRange(10)
			.setUpdateInterval(Integer.MAX_VALUE)
			.setShouldReceiveVelocityUpdates(false)
			.setCustomClientFactory((message, world) -> new EntityFenceFastener(world))
			.build(FairyLights.ID + ":fastener")
	);

	public static final RegistryObject<EntityType<EntityLadder>> LADDER = REG.register("ladder", () ->
		EntityType.Builder.<EntityLadder>create(EntityLadder::new, EntityClassification.MISC)
			.size(1.15F, 2.8F)
			.setTrackingRange(10)
			.setUpdateInterval(3)
			.setCustomClientFactory((message, world) -> new EntityLadder(world))
			.build(FairyLights.ID + ":ladder")
	);
}
