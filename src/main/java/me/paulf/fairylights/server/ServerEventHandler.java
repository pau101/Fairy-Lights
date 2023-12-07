package me.paulf.fairylights.server;

import me.paulf.fairylights.FairyLights;
import me.paulf.fairylights.server.block.FLBlocks;
import me.paulf.fairylights.server.block.FastenerBlock;
import me.paulf.fairylights.server.block.entity.FastenerBlockEntity;
import me.paulf.fairylights.server.capability.CapabilityHandler;
import me.paulf.fairylights.server.connection.HangingLightsConnection;
import me.paulf.fairylights.server.entity.FenceFastenerEntity;
import me.paulf.fairylights.server.fastener.BlockFastener;
import me.paulf.fairylights.server.fastener.FenceFastener;
import me.paulf.fairylights.server.fastener.PlayerFastener;
import me.paulf.fairylights.server.feature.light.Light;
import me.paulf.fairylights.server.item.ConnectionItem;
import me.paulf.fairylights.server.jingle.Jingle;
import me.paulf.fairylights.server.jingle.JingleLibrary;
import me.paulf.fairylights.server.jingle.JingleManager;
import me.paulf.fairylights.server.net.clientbound.JingleMessage;
import me.paulf.fairylights.server.net.clientbound.UpdateEntityFastenerMessage;
import me.paulf.fairylights.server.sound.FLSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockEventPacket;
import net.minecraft.server.players.PlayerList;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.NoteBlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public final class ServerEventHandler {

    @SubscribeEvent
    public void onEntityJoinWorld(final EntityJoinLevelEvent event) {
        final Entity entity = event.getEntity();
        if (entity instanceof Player || entity instanceof FenceFastenerEntity) {
            entity.getCapability(CapabilityHandler.FASTENER_CAP).ifPresent(f -> f.setWorld(event.getLevel()));
        }
    }

    @SubscribeEvent
    public void onAttachEntityCapability(final AttachCapabilitiesEvent<Entity> event) {
        final Entity entity = event.getObject();
        if (entity instanceof Player) {
            event.addCapability(CapabilityHandler.FASTENER_ID, new PlayerFastener((Player) entity));
        } else if (entity instanceof FenceFastenerEntity) {
            event.addCapability(CapabilityHandler.FASTENER_ID, new FenceFastener((FenceFastenerEntity) entity));
        }
    }

    @SubscribeEvent
    public void onAttachBlockEntityCapability(final AttachCapabilitiesEvent<BlockEntity> event) {
        final BlockEntity entity = event.getObject();
        if (entity instanceof FastenerBlockEntity) {
            event.addCapability(CapabilityHandler.FASTENER_ID, new BlockFastener((FastenerBlockEntity) entity, ServerProxy.buildBlockView()));
        }
    }

    @SubscribeEvent
    public void onTick(final TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            event.player.getCapability(CapabilityHandler.FASTENER_CAP).ifPresent(fastener -> {
                if (fastener.update() && !event.player.level().isClientSide()) {
                    ServerProxy.sendToPlayersWatchingEntity(new UpdateEntityFastenerMessage(event.player, fastener.serializeNBT()), event.player);
                }
            });
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onNoteBlockPlay(final NoteBlockEvent.Play event) {
        final Level world = (Level) event.getLevel();
        final BlockPos pos = event.getPos();
        final Block noteBlock = world.getBlockState(pos).getBlock();
        final BlockState below = world.getBlockState(pos.below());
        if (below.getBlock() == FLBlocks.FASTENER.get() && below.getValue(FastenerBlock.FACING) == Direction.DOWN) {
            final int note = event.getVanillaNoteId();
            final float pitch = (float) Math.pow(2, (note - 12) / 12D);
            world.playSound(null, pos, FLSounds.JINGLE_BELL.get(), SoundSource.RECORDS, 3, pitch);
            world.addParticle(ParticleTypes.NOTE, pos.getX() + 0.5, pos.getY() + 1.2, pos.getZ() + 0.5, note / 24D, 0, 0);
            if (!world.isClientSide()) {
                final Packet<?> pkt = new ClientboundBlockEventPacket(pos, noteBlock, event.getInstrument().ordinal(), note);
                final PlayerList players = world.getServer().getPlayerList();
                players.broadcast(null, pos.getX(), pos.getY(), pos.getZ(), 64, world.dimension(), pkt);
            }
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onRightClickBlock(final PlayerInteractEvent.RightClickBlock event) {
        final Level world = event.getLevel();
        final BlockPos pos = event.getPos();
        if (!(world.getBlockState(pos).getBlock() instanceof FenceBlock)) {
            return;
        }
        final ItemStack stack = event.getItemStack();
        boolean checkHanging = stack.getItem() == Items.LEAD;
        final Player player = event.getEntity();
        if (event.getHand() == InteractionHand.MAIN_HAND) {
            final ItemStack offhandStack = player.getOffhandItem();
            if (offhandStack.getItem() instanceof ConnectionItem) {
                if (checkHanging) {
                    event.setCanceled(true);
                    return;
                } else {
                    event.setUseBlock(Event.Result.DENY);
                }
            }
        }
        if (!checkHanging && !world.isClientSide()) {
            final double range = 7;
            final int x = pos.getX();
            final int y = pos.getY();
            final int z = pos.getZ();
            final AABB area = new AABB(x - range, y - range, z - range, x + range, y + range, z + range);
            for (final Mob entity : world.getEntitiesOfClass(Mob.class, area)) {
                if (entity.isLeashed() && entity.getLeashHolder() == player) {
                    checkHanging = true;
                    break;
                }
            }
        }
        if (checkHanging) {
            final HangingEntity entity = FenceFastenerEntity.findHanging(world, pos);
            if (entity != null && !(entity instanceof LeashFenceKnotEntity)) {
                event.setCanceled(true);
            }
        }
    }

    public static boolean tryJingle(final Level world, final HangingLightsConnection hangingLights) {
        String lib;
        if (FairyLights.CHRISTMAS.isOccurringNow()) {
            lib = JingleLibrary.CHRISTMAS;
        } else if (FairyLights.HALLOWEEN.isOccurringNow()) {
            lib = JingleLibrary.HALLOWEEN;
        } else {
            lib = JingleLibrary.RANDOM;
        }
        return tryJingle(world, hangingLights, lib);
    }

    public static boolean tryJingle(final Level world, final HangingLightsConnection hangingLights, final String lib) {
        if (world.isClientSide()) return false;
        final Light<?>[] lights = hangingLights.getFeatures();
        final Jingle jingle = JingleManager.INSTANCE.get(lib).getRandom(world.random, lights.length);
        if (jingle != null) {
            final int lightOffset = lights.length / 2 - jingle.getRange() / 2;
            hangingLights.play(jingle, lightOffset);
            ServerProxy.sendToPlayersWatchingChunk(new JingleMessage(hangingLights, lightOffset, jingle), world, hangingLights.getFastener().getPos());
            return true;
        }
        return false;
    }
}
