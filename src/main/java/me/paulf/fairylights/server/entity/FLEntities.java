package me.paulf.fairylights.server.entity;

import me.paulf.fairylights.FairyLights;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class FLEntities {
    private FLEntities() {}

    public static final DeferredRegister<EntityType<?>> REG = DeferredRegister.create(ForgeRegistries.ENTITIES, FairyLights.ID);

    public static final RegistryObject<EntityType<FenceFastenerEntity>> FASTENER = REG.register("fastener", () ->
        EntityType.Builder.<FenceFastenerEntity>func_220322_a(FenceFastenerEntity::new, EntityClassification.MISC)
            .func_220321_a(1.15F, 2.8F)
            .setTrackingRange(10)
            .setUpdateInterval(Integer.MAX_VALUE)
            .setShouldReceiveVelocityUpdates(false)
            .setCustomClientFactory((message, world) -> new FenceFastenerEntity(world))
            .func_206830_a(FairyLights.ID + ":fastener")
    );
}
