package com.pau101.fairylights.server.entity;

import java.util.function.Function;

import com.pau101.fairylights.FairyLights;
import com.pau101.fairylights.util.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.registries.IForgeRegistry;

@EventBusSubscriber(modid = FairyLights.ID)
public final class FLEntities {
	private FLEntities() {}

	@SubscribeEvent
	public static void register(RegistryEvent.Register<EntityEntry> event) {
		new Registrar(event.getRegistry())
			.register(EntityFenceFastener.class, EntityFenceFastener::new, "fastener",
				160, Integer.MAX_VALUE, false
			)
			.register(EntityLadder.class, EntityLadder::new, "ladder",
				160, 3, true
			);
	}

	private static final class Registrar {
		private final IForgeRegistry<EntityEntry> registry;

		private int nextId;

		private Registrar(IForgeRegistry<EntityEntry> registry) {
			this.registry = registry;
		}

		private <E extends Entity> Registrar register(Class<E> cls, Function<World, E> factory, String id, int trackingRange, int updateFrequency, boolean sendsVelocityUpdates) {
			registry.registerAll(new Entry<>(cls, id, factory));
			EntityRegistry.registerModEntity(new ResourceLocation(FairyLights.ID, id), cls, id, nextId++, FairyLights.instance(), trackingRange, updateFrequency, sendsVelocityUpdates);
			return this;
		}
	}

	private static final class Entry<E extends Entity> extends EntityEntry {
		private final Function<World, E> factory;

		private Entry(Class<E> cls, String name, Function<World, E> factory) {
			super(cls, name);
			this.factory = factory;
			setRegistryName(Utils.underScoreToCamel(name));
		}

		@Override
		protected void init() {}

		@Override
		public Entity newInstance(World world) {
			return factory.apply(world);
		}
	}
}
