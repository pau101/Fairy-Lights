package com.pau101.fairylights.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

import com.pau101.fairylights.FairyLights;
import com.pau101.fairylights.config.Configurator;
import com.pau101.fairylights.connection.ConnectionLogicFairyLights;
import com.pau101.fairylights.connection.Light;
import com.pau101.fairylights.eggs.Jingle;
import com.pau101.fairylights.network.play.server.S00FLPacketJingle;
import com.pau101.fairylights.player.PlayerData;
import com.pau101.fairylights.tileentity.TileEntityConnectionFastener;
import com.pau101.fairylights.tileentity.connection.Connection;
import com.pau101.fairylights.tileentity.connection.ConnectionPlayer;
import com.pau101.fairylights.util.vectormath.Point3f;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class TickHandler {
	private Random random;

	// Every 5 minutes on average a jingle will attempt to play
	private float jingleProbability = 1F / (5 * 60 * 20);

	public TickHandler() {
		random = new Random();
	}

	private List<Point3f> getPlayingLightSources(Map<TileEntityConnectionFastener, List<Entry<UUID, Connection>>> feasibleConnections, TileEntityConnectionFastener fastener) {
		List<Point3f> points = new ArrayList<Point3f>();
		double expandAmount = Configurator.jingleAmplitude;
		AxisAlignedBB listenerRegion = fastener.getBoundingBox().expand(expandAmount, expandAmount, expandAmount);
		List<EntityPlayer> nearPlayers = fastener.getWorldObj().getEntitiesWithinAABB(EntityPlayer.class, listenerRegion);
		boolean arePlayersNear = nearPlayers.size() > 0;
		for (Entry<UUID, Connection> connectionEntry : fastener.getConnectionEntrySet()) {
			Connection connection = connectionEntry.getValue();
			if (connection.isOrigin() && !(connection instanceof ConnectionPlayer) && connection.getLogic() instanceof ConnectionLogicFairyLights) {
				ConnectionLogicFairyLights connectionLogic = (ConnectionLogicFairyLights) connection.getLogic();
				Light[] lightPoints = connectionLogic.getLightPoints();
				int range = lightPoints == null ? 0 : lightPoints.length;
				if (range >= Jingle.getMinRange()) {
					if (connectionLogic.canCurrentlyPlayAJingle()) {
						if (arePlayersNear) {
							if (feasibleConnections.containsKey(fastener)) {
								feasibleConnections.get(fastener).add(connectionEntry);
							} else {
								List<Entry<UUID, Connection>> connections = new ArrayList<Entry<UUID, Connection>>();
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
		}
		return points;
	}

	public boolean isTooCloseTo(TileEntityConnectionFastener fastener, Light[] lights, List<Point3f> playingSources) {
		for (Light light : lights) {
			for (Point3f point : playingSources) {
				if (light.getAbsolutePoint(fastener).distance(point) <= Configurator.jingleAmplitude) {
					return true;
				}
			}
		}
		return false;
	}

	@SubscribeEvent
	public void onTick(TickEvent.ServerTickEvent event) {
		if (event.phase == TickEvent.Phase.START) {
			return;
		}
		PlayerData.update();
	}

	@SubscribeEvent
	public void onWorldTick(TickEvent.WorldTickEvent event) {
		if (event.phase == TickEvent.Phase.START) {
			return;
		}
		if (event.world != null) {
			if (FairyLights.christmas.isOcurringNow() && Configurator.jingleEnabled && random.nextFloat() < jingleProbability) {
				List<TileEntity> tileEntities = event.world.loadedTileEntityList;
				List<Point3f> playingSources = new ArrayList<Point3f>();
				Map<TileEntityConnectionFastener, List<Entry<UUID, Connection>>> feasibleConnections = new HashMap<TileEntityConnectionFastener, List<Entry<UUID, Connection>>>();
				for (TileEntity tileEntity : tileEntities) {
					if (tileEntity instanceof TileEntityConnectionFastener) {
						TileEntityConnectionFastener fastener = (TileEntityConnectionFastener) tileEntity;
						List<Point3f> newPlayingSources = getPlayingLightSources(feasibleConnections, fastener);
						if (newPlayingSources != null && newPlayingSources.size() > 0) {
							playingSources.addAll(newPlayingSources);
						}
					}
				}
				Iterator<TileEntityConnectionFastener> feasibleFasteners = feasibleConnections.keySet().iterator();
				while (feasibleFasteners.hasNext()) {
					TileEntityConnectionFastener fastener = feasibleFasteners.next();
					List<Entry<UUID, Connection>> connections = feasibleConnections.get(fastener);
					Iterator<Entry<UUID, Connection>> connectionIterator = connections.iterator();
					while (connectionIterator.hasNext()) {
						Connection connection = connectionIterator.next().getValue();
						if (isTooCloseTo(fastener, ((ConnectionLogicFairyLights) connection.getLogic()).getLightPoints(), playingSources)) {
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
				TileEntityConnectionFastener fastener = feasibleConnections.keySet().toArray(new TileEntityConnectionFastener[0])[random.nextInt(feasibleConnections.size())];
				List<Entry<UUID, Connection>> connections = feasibleConnections.get(fastener);
				Entry<UUID, Connection> connectionEntry = connections.get(random.nextInt(connections.size()));
				UUID uuid = connectionEntry.getKey();
				Connection connection = connectionEntry.getValue();
				Light[] lightPoints = ((ConnectionLogicFairyLights) connection.getLogic()).getLightPoints();
				if (lightPoints != null) {
					int lightOffset = lightPoints.length;
					Jingle jingle = Jingle.getRandomJingle(lightOffset);
					if (jingle != null) {
						lightOffset = lightOffset / 2 - jingle.getRange() / 2;
						((ConnectionLogicFairyLights) connection.getLogic()).play(jingle, lightOffset);
						FairyLights.networkManager.sendPacketToClientsWatchingChunk(fastener.xCoord, fastener.zCoord, event.world, new S00FLPacketJingle(fastener, uuid, lightOffset, jingle));
					}
				}
			}
		}
	}
}
