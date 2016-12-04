package com.pau101.fairylights.server.jingle;

import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.collect.Sets;
import com.pau101.fairylights.server.fastener.connection.type.hanginglights.Light;
import com.pau101.fairylights.server.jingle.Jingle.PlayTick;

import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public final class JinglePlayer {
	private static final int NOT_PLAYING = -2;

	private static final Set<String> WITH_LOVE = Sets.newHashSet("my_anthem", "im_fine_thank_you");

	@Nullable
	private Jingle jingle;

	@Nullable
	private List<PlayTick> jinglePlayTicks;

	private int jingleLength;

	private int lightOffset;

	private boolean isPlaying;

	private int currentTickIndex;

	private int rest;

	private EnumParticleTypes noteParticle;

	@Nullable
	public Jingle getJingle() {
		return jingle;
	}

	public void start(Jingle jingle, int lightOffset) {
		this.jingle = jingle;
		this.lightOffset = lightOffset;
		jinglePlayTicks = jingle.getPlayTicks();
		jingleLength = 0;
		for (PlayTick playTick : jinglePlayTicks) {
			jingleLength += playTick.getLength();
		}
		isPlaying = true;
		currentTickIndex = 0;
		rest = 0;
		noteParticle = "playing_with_fire".equals(jingle.getId()) ? EnumParticleTypes.LAVA : WITH_LOVE.contains(jingle.getId()) ? EnumParticleTypes.HEART : EnumParticleTypes.NOTE;
	}

	private void stop() {
		jingle = null;
		lightOffset = 0;
		jinglePlayTicks = null;
		jingleLength = 0;
		isPlaying = false;
		currentTickIndex = 0;
		rest = 0;
	}

	public boolean isPlaying() {
		return isPlaying;
	}

	public void tick(World world, Vec3d origin, Light[] lights, boolean isClient) {
		if (rest <= 0) {
			if (currentTickIndex >= jinglePlayTicks.size()) {
				stop();
			} else {
				PlayTick playTick = jinglePlayTicks.get(currentTickIndex++);
				rest = playTick.getLength() - 1;
				if (isClient) {
					play(world, origin, lights, playTick);
				}
			}
		} else {
			rest--;
		}
	}

	private void play(World world, Vec3d origin, Light[] lights, PlayTick playTick) {
		for (int note : playTick.getNotes()) {
			int idx = note - jingle.getLowestNote() + lightOffset;
			if (idx >= 0 && idx < lights.length) {
				lights[idx].jingle(world, origin, note, noteParticle);
			}
		}
	}
}
