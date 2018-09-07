package com.pau101.fairylights.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;

import com.pau101.fairylights.FairyLights;
import com.pau101.fairylights.server.block.BlockFastener;
import com.pau101.fairylights.server.block.FLBlocks;
import com.pau101.fairylights.server.block.entity.BlockEntityFastener;
import com.pau101.fairylights.server.capability.CapabilityHandler;
import com.pau101.fairylights.server.config.Configurator;
import com.pau101.fairylights.server.entity.EntityFenceFastener;
import com.pau101.fairylights.server.entity.EntityLadder;
import com.pau101.fairylights.server.fastener.Fastener;
import com.pau101.fairylights.server.fastener.FastenerBlock;
import com.pau101.fairylights.server.fastener.FastenerFence;
import com.pau101.fairylights.server.fastener.FastenerPlayer;
import com.pau101.fairylights.server.fastener.connection.ConnectionType;
import com.pau101.fairylights.server.fastener.connection.type.Connection;
import com.pau101.fairylights.server.fastener.connection.type.hanginglights.ConnectionHangingLights;
import com.pau101.fairylights.server.fastener.connection.type.hanginglights.Light;
import com.pau101.fairylights.server.item.ItemConnection;
import com.pau101.fairylights.server.jingle.Jingle;
import com.pau101.fairylights.server.jingle.JingleLibrary;
import com.pau101.fairylights.server.net.clientbound.MessageJingle;
import com.pau101.fairylights.server.net.clientbound.MessageUpdateFastenerEntity;
import com.pau101.fairylights.server.sound.FLSounds;
import com.pau101.fairylights.server.world.ServerWorldEventListener;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketBlockAction;
import net.minecraft.server.management.PlayerList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.GetCollisionBoxesEvent;
import net.minecraftforge.event.world.NoteBlockEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public final class ServerEventHandler {
	private final Random rng = new Random();

	// Every 5 minutes on average a jingle will attempt to play
	private float jingleProbability = 1F / (5 * 60 * 20);

	@SubscribeEvent
	public void onGetCollisionBoxes(GetCollisionBoxesEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof EntityPlayer) {
			AxisAlignedBB bounds = event.getAabb();
			List<EntityLadder> ladders = event.getWorld().getEntitiesWithinAABB(EntityLadder.class, bounds.grow(1));
			List<AxisAlignedBB> boxes = event.getCollisionBoxesList();
			for (EntityLadder ladder : ladders) {
				if (entity == ladder) {
					continue;
				}
				List<AxisAlignedBB> surfaces = ladder.getCollisionSurfaces();
				for (AxisAlignedBB surface : surfaces) {
					if (surface.intersects(bounds)) {
						boxes.add(surface);	
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load event) {
		World world = event.getWorld();
		if (!world.isRemote) {
			world.addEventListener(new ServerWorldEventListener());
		}
	}

	@SubscribeEvent
	public void onAttachEntityCapability(AttachCapabilitiesEvent<Entity> event) {
		Entity entity = event.getObject();
		if (entity instanceof EntityPlayer) {
			event.addCapability(CapabilityHandler.FASTENER_ID, new FastenerPlayer((EntityPlayer) entity));
		} else if (entity instanceof EntityFenceFastener) {
			event.addCapability(CapabilityHandler.FASTENER_ID, new FastenerFence((EntityFenceFastener) entity));
		}
	}

	@SubscribeEvent
	public void onAttachBlockEntityCapability(AttachCapabilitiesEvent<TileEntity> event) {
		TileEntity entity = event.getObject();
		if (entity instanceof BlockEntityFastener) {
			event.addCapability(CapabilityHandler.FASTENER_ID, new FastenerBlock((BlockEntityFastener) entity));
		}
	}

	@SubscribeEvent
	public void onTick(TickEvent.PlayerTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			Fastener fastener = event.player.getCapability(CapabilityHandler.FASTENER_CAP, null);
			if (fastener.update() && !event.player.world.isRemote) {
				ServerProxy.sendToPlayersWatchingEntity(new MessageUpdateFastenerEntity(event.player, fastener.serializeNBT()), event.player.world, event.player);
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onNoteBlockPlay(NoteBlockEvent.Play event) {
		World world = event.getWorld();
		BlockPos pos = event.getPos();
		Block noteBlock = world.getBlockState(pos).getBlock();
		IBlockState below = world.getBlockState(pos.down());
		if (below.getBlock() == FLBlocks.FASTENER && below.getValue(BlockFastener.FACING) == EnumFacing.DOWN) {
			int note = event.getVanillaNoteId();
	        float pitch = (float) Math.pow(2, (note - 12) / 12D);
	        world.playSound(null, pos, FLSounds.JINGLE_BELL, SoundCategory.RECORDS, 3, pitch);
	        world.spawnParticle(EnumParticleTypes.NOTE, pos.getX() + 0.5, pos.getY() + 1.2, pos.getZ() + 0.5, note / 24D, 0, 0);
	        if (!world.isRemote) {
	        	Packet<?> pkt = new SPacketBlockAction(pos, noteBlock, event.getInstrument().ordinal(), note);
	        	PlayerList players = ((WorldServer) world).getMinecraftServer().getPlayerList();
	        	players.sendToAllNearExcept(null, pos.getX(), pos.getY(), pos.getZ(), 64, world.provider.getDimension(), pkt);
	        }
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
		World world = event.getWorld();
		BlockPos pos = event.getPos();
		if (!(world.getBlockState(pos).getBlock() instanceof BlockFence)) {
			return;
		}
		ItemStack stack = event.getItemStack();
		boolean checkHanging = stack.getItem() == Items.LEAD;
		EntityPlayer player = event.getEntityPlayer();
		if (event.getHand() == EnumHand.MAIN_HAND) {
			ItemStack offhandStack = player.getHeldItemOffhand();
			if (offhandStack.getItem() instanceof ItemConnection) {
				if (checkHanging) {
					event.setCanceled(true);
					return;
				} else {
					event.setUseBlock(Result.DENY);
				}
			}
		}
		if (!checkHanging && !world.isRemote) {
			final double range = 7;
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			AxisAlignedBB area = new AxisAlignedBB(x - range, y - range, z - range, x + range, y + range, z + range);
			for (EntityLiving entity : world.getEntitiesWithinAABB(EntityLiving.class, area)) {
				if (entity.getLeashed() && entity.getLeashHolder() == player) {
					checkHanging = true;
					break;
				}
			}
		}
		if (checkHanging) {
			EntityHanging entity = EntityFenceFastener.findHanging(world, pos);
			if (entity != null && !(entity instanceof EntityLeashKnot)) {
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public void onWorldTick(TickEvent.WorldTickEvent event) {
		if (event.phase == TickEvent.Phase.START) {
			return;
		}
		if (FairyLights.christmas.isOcurringNow() && Configurator.isJingleEnabled() && rng.nextFloat() < jingleProbability) {
			List<TileEntity> tileEntities = event.world.loadedTileEntityList;
			List<Vec3d> playingSources = new ArrayList<>();
			Map<Fastener<?>, List<Entry<UUID, Connection>>> feasibleConnections = new HashMap<>();
			for (TileEntity tileEntity : tileEntities) {
				if (tileEntity.hasCapability(CapabilityHandler.FASTENER_CAP, null)) {
					Fastener<?> fastener = tileEntity.getCapability(CapabilityHandler.FASTENER_CAP, null);
					List<Vec3d> newPlayingSources = getPlayingLightSources(event.world, feasibleConnections, fastener);
					if (newPlayingSources != null && newPlayingSources.size() > 0) {
						playingSources.addAll(newPlayingSources);
					}
				}
			}
			for (Entity entity : event.world.loadedEntityList) {
				if (entity.hasCapability(CapabilityHandler.FASTENER_CAP, null)) {
					Fastener<?> fastener = entity.getCapability(CapabilityHandler.FASTENER_CAP, null);
					List<Vec3d> newPlayingSources = getPlayingLightSources(event.world, feasibleConnections, fastener);
					if (newPlayingSources != null && newPlayingSources.size() > 0) {
						playingSources.addAll(newPlayingSources);
					}
				}
			}
			Iterator<Fastener<?>> feasibleFasteners = feasibleConnections.keySet().iterator();
			while (feasibleFasteners.hasNext()) {
				Fastener fastener = feasibleFasteners.next();
				List<Entry<UUID, Connection>> connections = feasibleConnections.get(fastener);
				Iterator<Entry<UUID, Connection>> connectionIterator = connections.iterator();
				while (connectionIterator.hasNext()) {
					Connection connection = connectionIterator.next().getValue();
					if (isTooCloseTo(fastener, ((ConnectionHangingLights) connection).getFeatures(), playingSources)) {
						connectionIterator.remove();
					}
				}
				if (connections.size() == 0) {
					feasibleFasteners.remove();
				}
			}
			if (feasibleConnections.size() == 0) {
				return;
			}
			Fastener fastener = feasibleConnections.keySet().toArray(new Fastener[0])[rng.nextInt(feasibleConnections.size())];
			List<Entry<UUID, Connection>> connections = feasibleConnections.get(fastener);
			Entry<UUID, Connection> connectionEntry = connections.get(rng.nextInt(connections.size()));
			Connection connection = connectionEntry.getValue();
			tryJingle(event.world, connection, (ConnectionHangingLights) connection, FairyLights.christmasJingles);
		}
	}

	private List<Vec3d> getPlayingLightSources(World world, Map<Fastener<?>, List<Entry<UUID, Connection>>> feasibleConnections, Fastener<?> fastener) {
		List<Vec3d> points = new ArrayList<>();
		double expandAmount = Configurator.getJingleAmplitude();
		AxisAlignedBB listenerRegion = fastener.getBounds().expand(expandAmount, expandAmount, expandAmount);
		List<EntityPlayer> nearPlayers = fastener.getWorld().getEntitiesWithinAABB(EntityPlayer.class, listenerRegion);
		boolean arePlayersNear = nearPlayers.size() > 0;
		for (Entry<UUID, Connection> connectionEntry : fastener.getConnections().entrySet()) {
			Connection connection = connectionEntry.getValue();
			if (connection.isOrigin() && connection.getDestination().isLoaded(world) && !connection.getDestination().get(world).isDynamic() && connection.getType() == ConnectionType.HANGING_LIGHTS) {
				ConnectionHangingLights connectionLogic = (ConnectionHangingLights) connection;
				Light[] lightPoints = connectionLogic.getFeatures();
				if (connectionLogic.canCurrentlyPlayAJingle()) {
					if (arePlayersNear) {
						if (feasibleConnections.containsKey(fastener)) {
							feasibleConnections.get(fastener).add(connectionEntry);
						} else {
							List<Entry<UUID, Connection>> connections = new ArrayList<>();
							connections.add(connectionEntry);
							feasibleConnections.put(fastener, connections);
						}
					}
				} else {
					for (Light light : lightPoints) {
						points.add(light.getAbsolutePoint(fastener));
					}
				}
			}
		}
		return points;
	}

	public boolean isTooCloseTo(Fastener fastener, Light[] lights, List<Vec3d> playingSources) {
		for (Light light : lights) {
			for (Vec3d point : playingSources) {
				if (light.getAbsolutePoint(fastener).distanceTo(point) <= Configurator.getJingleAmplitude()) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean tryJingle(World world, Connection connection, ConnectionHangingLights hangingLights, JingleLibrary library) {
		Light[] lights = hangingLights.getFeatures();
		Jingle jingle = library.getRandom(world.rand, lights.length);
		if (jingle != null) {
			int lightOffset = lights.length / 2 - jingle.getRange() / 2;
			hangingLights.play(library, jingle, lightOffset);
			ServerProxy.sendToPlayersWatchingChunk(new MessageJingle(connection, lightOffset, library, jingle), world, connection.getFastener().getPos());
			return true;
		}
		return false;
	}
}
