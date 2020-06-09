package me.paulf.fairylights.server;

import me.paulf.fairylights.FairyLights;
import me.paulf.fairylights.server.block.FLBlocks;
import me.paulf.fairylights.server.block.FastenerBlock;
import me.paulf.fairylights.server.block.entity.FastenerBlockEntity;
import me.paulf.fairylights.server.capability.CapabilityHandler;
import me.paulf.fairylights.server.config.FLConfig;
import me.paulf.fairylights.server.entity.FenceFastenerEntity;
import me.paulf.fairylights.server.entity.LadderEntity;
import me.paulf.fairylights.server.fastener.BlockFastener;
import me.paulf.fairylights.server.fastener.Fastener;
import me.paulf.fairylights.server.fastener.FenceFastener;
import me.paulf.fairylights.server.fastener.PlayerFastener;
import me.paulf.fairylights.server.fastener.connection.type.Connection;
import me.paulf.fairylights.server.fastener.connection.type.hanginglights.HangingLightsConnection;
import me.paulf.fairylights.server.fastener.connection.type.hanginglights.Light;
import me.paulf.fairylights.server.item.ConnectionItem;
import me.paulf.fairylights.server.jingle.Jingle;
import me.paulf.fairylights.server.jingle.JingleLibrary;
import me.paulf.fairylights.server.net.clientbound.JingleMessage;
import me.paulf.fairylights.server.net.clientbound.UpdateEntityFastenerMessage;
import me.paulf.fairylights.server.sound.FLSounds;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FenceBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.item.HangingEntity;
import net.minecraft.entity.item.LeashKnotEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SBlockActionPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.server.management.PlayerList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.GetCollisionBoxesEvent;
import net.minecraftforge.event.world.NoteBlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;

public final class ServerEventHandler {
    private final Random rng = new Random();

    // Every 5 minutes on average a jingle will attempt to play
    private final float jingleProbability = 1F / (5 * 60 * 20);

    // TODO: ladder collision
    @SubscribeEvent
    public void onGetCollisionBoxes(final GetCollisionBoxesEvent event) {
        final Entity entity = event.getEntity();
        if (entity instanceof PlayerEntity) {
            final AxisAlignedBB bounds = event.getAabb();
            final List<LadderEntity> ladders = event.getWorld().getEntitiesWithinAABB(LadderEntity.class, bounds.grow(1));
            final List<AxisAlignedBB> boxes = event.getCollisionBoxesList();
            for (final LadderEntity ladder : ladders) {
                if (entity == ladder) {
                    continue;
                }
                final List<AxisAlignedBB> surfaces = ladder.getCollisionSurfaces();
                for (final AxisAlignedBB surface : surfaces) {
                    if (surface.intersects(bounds)) {
                        boxes.add(surface);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onAttachEntityCapability(final AttachCapabilitiesEvent<Entity> event) {
        final Entity entity = event.getObject();
        if (entity instanceof PlayerEntity) {
            event.addCapability(CapabilityHandler.FASTENER_ID, new PlayerFastener((PlayerEntity) entity));
        } else if (entity instanceof FenceFastenerEntity) {
            event.addCapability(CapabilityHandler.FASTENER_ID, new FenceFastener((FenceFastenerEntity) entity));
        }
    }

    @SubscribeEvent
    public void onAttachBlockEntityCapability(final AttachCapabilitiesEvent<TileEntity> event) {
        final TileEntity entity = event.getObject();
        if (entity instanceof FastenerBlockEntity) {
            event.addCapability(CapabilityHandler.FASTENER_ID, new BlockFastener((FastenerBlockEntity) entity, ServerProxy.buildBlockView()));
        }
    }

    @SubscribeEvent
    public void onTick(final TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            event.player.getCapability(CapabilityHandler.FASTENER_CAP).ifPresent(fastener -> {
                if (fastener.update() && !event.player.world.isRemote) {
                    ServerProxy.sendToPlayersWatchingEntity(new UpdateEntityFastenerMessage(event.player, fastener.serializeNBT()), event.player);
                }
            });
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onNoteBlockPlay(final NoteBlockEvent.Play event) {
        final World world = (World) event.getWorld();
        final BlockPos pos = event.getPos();
        final Block noteBlock = world.getBlockState(pos).getBlock();
        final BlockState below = world.getBlockState(pos.down());
        if (below.getBlock() == FLBlocks.FASTENER.get() && below.get(FastenerBlock.FACING) == Direction.DOWN) {
            final int note = event.getVanillaNoteId();
            final float pitch = (float) Math.pow(2, (note - 12) / 12D);
            world.playSound(null, pos, FLSounds.JINGLE_BELL.get(), SoundCategory.RECORDS, 3, pitch);
            world.addParticle(ParticleTypes.NOTE, pos.getX() + 0.5, pos.getY() + 1.2, pos.getZ() + 0.5, note / 24D, 0, 0);
            if (!world.isRemote) {
                final IPacket<?> pkt = new SBlockActionPacket(pos, noteBlock, event.getInstrument().ordinal(), note);
                final PlayerList players = world.getServer().getPlayerList();
                players.sendToAllNearExcept(null, pos.getX(), pos.getY(), pos.getZ(), 64, world.getDimension().getType(), pkt);
            }
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onRightClickBlock(final PlayerInteractEvent.RightClickBlock event) {
        final World world = event.getWorld();
        final BlockPos pos = event.getPos();
        if (!(world.getBlockState(pos).getBlock() instanceof FenceBlock)) {
            return;
        }
        final ItemStack stack = event.getItemStack();
        boolean checkHanging = stack.getItem() == Items.LEAD;
        final PlayerEntity player = event.getPlayer();
        if (event.getHand() == Hand.MAIN_HAND) {
            final ItemStack offhandStack = player.getHeldItemOffhand();
            if (offhandStack.getItem() instanceof ConnectionItem) {
                if (checkHanging) {
                    event.setCanceled(true);
                    return;
                } else {
                    event.setUseBlock(Event.Result.DENY);
                }
            }
        }
        if (!checkHanging && !world.isRemote) {
            final double range = 7;
            final int x = pos.getX();
            final int y = pos.getY();
            final int z = pos.getZ();
            final AxisAlignedBB area = new AxisAlignedBB(x - range, y - range, z - range, x + range, y + range, z + range);
            for (final MobEntity entity : world.getEntitiesWithinAABB(MobEntity.class, area)) {
                if (entity.getLeashed() && entity.getLeashHolder() == player) {
                    checkHanging = true;
                    break;
                }
            }
        }
        if (checkHanging) {
            final HangingEntity entity = FenceFastenerEntity.findHanging(world, pos);
            if (entity != null && !(entity instanceof LeashKnotEntity)) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onWorldTick(final TickEvent.WorldTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            return;
        }
        if (FairyLights.CHRISTMAS.isOccurringNow() && FLConfig.isJingleEnabled() && this.rng.nextFloat() < this.jingleProbability) {
            final List<TileEntity> tileEntities = event.world.loadedTileEntityList;
            final List<Vec3d> playingSources = new ArrayList<>();
            final Map<Fastener<?>, List<HangingLightsConnection>> feasibleConnections = new HashMap<>();
            for (final TileEntity tileEntity : tileEntities) {
                tileEntity.getCapability(CapabilityHandler.FASTENER_CAP).ifPresent(fastener -> {
                    final List<Vec3d> newPlayingSources = this.getPlayingLightSources(event.world, feasibleConnections, fastener);
                    if (!newPlayingSources.isEmpty()) {
                        playingSources.addAll(newPlayingSources);
                    }
                });
            }
            ((ServerWorld) event.world).getEntities().forEach(entity -> {
                entity.getCapability(CapabilityHandler.FASTENER_CAP).ifPresent(fastener -> {
                    final List<Vec3d> newPlayingSources = this.getPlayingLightSources(event.world, feasibleConnections, fastener);
                    if (!newPlayingSources.isEmpty()) {
                        playingSources.addAll(newPlayingSources);
                    }
                });
            });
            final Iterator<Fastener<?>> feasibleFasteners = feasibleConnections.keySet().iterator();
            while (feasibleFasteners.hasNext()) {
                final Fastener<?> fastener = feasibleFasteners.next();
                final List<HangingLightsConnection> connections = feasibleConnections.get(fastener);
                connections.removeIf(connection -> this.isTooCloseTo(fastener, connection.getFeatures(), playingSources));
                if (connections.size() == 0) {
                    feasibleFasteners.remove();
                }
            }
            if (feasibleConnections.size() == 0) {
                return;
            }
            final Fastener<?> fastener = feasibleConnections.keySet().toArray(new Fastener[0])[this.rng.nextInt(feasibleConnections.size())];
            final List<HangingLightsConnection> connections = feasibleConnections.get(fastener);
            final HangingLightsConnection connection = connections.get(this.rng.nextInt(connections.size()));
            tryJingle(event.world, connection, JingleLibrary.CHRISTMAS);
        }
    }

    private List<Vec3d> getPlayingLightSources(final World world, final Map<Fastener<?>, List<HangingLightsConnection>> feasibleConnections, final Fastener<?> fastener) {
        final List<Vec3d> points = new ArrayList<>();
        final double expandAmount = FLConfig.getJingleAmplitude();
        final AxisAlignedBB listenerRegion = fastener.getBounds().expand(expandAmount, expandAmount, expandAmount);
        final List<PlayerEntity> nearPlayers = world.getEntitiesWithinAABB(PlayerEntity.class, listenerRegion);
        final boolean arePlayersNear = nearPlayers.size() > 0;
        for (final Entry<UUID, Connection> connectionEntry : fastener.getConnections().entrySet()) {
            final Connection connection = connectionEntry.getValue();
            if (connection.isOrigin() && connection.getDestination().get(world, false).isPresent() && connection instanceof HangingLightsConnection) {
                final HangingLightsConnection connectionLogic = (HangingLightsConnection) connection;
                final Light<?>[] lightPoints = connectionLogic.getFeatures();
                if (connectionLogic.canCurrentlyPlayAJingle()) {
                    if (arePlayersNear) {
                        if (feasibleConnections.containsKey(fastener)) {
                            feasibleConnections.get(fastener).add((HangingLightsConnection) connection);
                        } else {
                            final List<HangingLightsConnection> connections = new ArrayList<>();
                            connections.add((HangingLightsConnection) connection);
                            feasibleConnections.put(fastener, connections);
                        }
                    }
                } else {
                    for (final Light<?> light : lightPoints) {
                        points.add(light.getAbsolutePoint(fastener));
                    }
                }
            }
        }
        return points;
    }

    public boolean isTooCloseTo(final Fastener<?> fastener, final Light<?>[] lights, final List<Vec3d> playingSources) {
        for (final Light<?> light : lights) {
            for (final Vec3d point : playingSources) {
                if (light.getAbsolutePoint(fastener).distanceTo(point) <= FLConfig.getJingleAmplitude()) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean tryJingle(final World world, final HangingLightsConnection hangingLights, final JingleLibrary library) {
        final Light<?>[] lights = hangingLights.getFeatures();
        final Jingle jingle = library.getRandom(world.rand, lights.length);
        if (jingle != null) {
            final int lightOffset = lights.length / 2 - jingle.getRange() / 2;
            hangingLights.play(library, jingle, lightOffset);
            ServerProxy.sendToPlayersWatchingChunk(new JingleMessage(hangingLights, lightOffset, library, jingle), world, hangingLights.getFastener().getPos());
            return true;
        }
        return false;
    }
}
