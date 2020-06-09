package me.paulf.fairylights.server;

import me.paulf.fairylights.FairyLights;
import me.paulf.fairylights.client.tutorial.ClippyController;
import me.paulf.fairylights.server.capability.CapabilityHandler;
import me.paulf.fairylights.server.config.FLConfig;
import me.paulf.fairylights.server.fastener.BlockView;
import me.paulf.fairylights.server.fastener.CreateBlockViewEvent;
import me.paulf.fairylights.server.fastener.RegularBlockView;
import me.paulf.fairylights.server.item.LightVariant;
import me.paulf.fairylights.server.jingle.JingleLibrary;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.INBT;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.function.Consumer;

public class ServerProxy {
    public void init(final IEventBus modBus) {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, FLConfig.GENERAL_SPEC);
        MinecraftForge.EVENT_BUS.<FMLServerAboutToStartEvent>addListener(e -> {
            final MinecraftServer server = e.getServer();
            this.addListener(server.getResourceManager(), mgr -> JingleLibrary.loadAll(server));
        });
        MinecraftForge.EVENT_BUS.register(new ServerEventHandler());
        new ClippyController().init(modBus);
        modBus.addListener(this::setup);
    }

    @SuppressWarnings("deprecation")
    private void addListener(final IReloadableResourceManager manager, final Consumer<IResourceManager> listener) {
        manager.addReloadListener((IResourceManagerReloadListener) listener::accept);
    }

    private void setup(final FMLCommonSetupEvent event) {
        CapabilityHandler.register();
        CapabilityManager.INSTANCE.register(LightVariant.class,  new Capability.IStorage<LightVariant>() {
            @Override
            public INBT writeNBT(final Capability<LightVariant> capability, final LightVariant instance, final Direction side) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void readNBT(final Capability<LightVariant> capability, final LightVariant instance, final Direction side, final INBT nbt) {
                throw new UnsupportedOperationException();
            }
        }, () -> {
            throw new UnsupportedOperationException();
        });
    }

    public static void sendToPlayersWatchingChunk(final Object message, final World world, final BlockPos pos) {
        FairyLights.NETWORK.send(PacketDistributor.TRACKING_CHUNK.with(() -> world.getChunkAt(pos)), message);
    }

    public static void sendToPlayersWatchingEntity(final Object message, final Entity entity) {
        FairyLights.NETWORK.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), message);
    }

    public static BlockView buildBlockView() {
        final CreateBlockViewEvent evt = new CreateBlockViewEvent(new RegularBlockView());
        MinecraftForge.EVENT_BUS.post(evt);
        return evt.getView();
    }

    public void initIntegration() {
		/*if (Loader.isModLoaded(ValkyrienWarfareMod.MODID)) {
			final Class<?> vw;
			try {
				vw = Class.forName("ValkyrienWarfare");
			} catch (final ClassNotFoundException e) {
				throw new AssertionError(e);
			}
			MinecraftForge.EVENT_BUS.register(vw);
		}*/
    }
}
