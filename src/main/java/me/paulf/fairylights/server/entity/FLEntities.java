package me.paulf.fairylights.server.entity;

import me.paulf.fairylights.*;
import net.minecraft.entity.*;
import net.minecraftforge.fml.*;
import net.minecraftforge.registries.*;

public final class FLEntities {
    private FLEntities() {}

    public static final DeferredRegister<EntityType<?>> REG = new DeferredRegister<>(ForgeRegistries.ENTITIES, FairyLights.ID);

    public static final RegistryObject<EntityType<FenceFastenerEntity>> FASTENER = REG.register("fastener", () ->
        EntityType.Builder.<FenceFastenerEntity>create(FenceFastenerEntity::new, EntityClassification.MISC)
            .size(1.15F, 2.8F)
            .setTrackingRange(10)
            .setUpdateInterval(Integer.MAX_VALUE)
            .setShouldReceiveVelocityUpdates(false)
            .setCustomClientFactory((message, world) -> new FenceFastenerEntity(world))
            .build(FairyLights.ID + ":fastener")
    );

    public static final RegistryObject<EntityType<LadderEntity>> LADDER = REG.register("ladder", () ->
        EntityType.Builder.<LadderEntity>create(LadderEntity::new, EntityClassification.MISC)
            .size(1.15F, 2.8F)
            .setTrackingRange(10)
            .setUpdateInterval(3)
            .setCustomClientFactory((message, world) -> new LadderEntity(world))
            .build(FairyLights.ID + ":ladder")
    );
}
