package me.paulf.fairylights.server.entity;

import me.paulf.fairylights.FairyLights;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class FLEntities {
    private FLEntities() {}

    public static final DeferredRegister<EntityType<?>> REG = DeferredRegister.create(ForgeRegistries.ENTITIES, FairyLights.ID);

    public static final RegistryObject<EntityType<FenceFastenerEntity>> FASTENER = REG.register("fastener", () ->
        EntityType.Builder.<FenceFastenerEntity>of(FenceFastenerEntity::new, MobCategory.MISC)
            .sized(1.15F, 2.8F)
            .setTrackingRange(10)
            .setUpdateInterval(Integer.MAX_VALUE)
            .setShouldReceiveVelocityUpdates(false)
            .setCustomClientFactory((message, world) -> new FenceFastenerEntity(world))
            .build(FairyLights.ID + ":fastener")
    );
}
