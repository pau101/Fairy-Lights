package com.pau101.fairylights.server.jingle;

import com.google.common.collect.Sets;
import com.pau101.fairylights.server.fastener.connection.type.hanginglights.Light;
import com.pau101.fairylights.server.jingle.Jingle.PlayTick;
import com.pau101.fairylights.server.sound.FLSounds;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public final class JinglePlayer {
	private static final Set<String> WITH_LOVE = Sets.newHashSet("my_anthem", "im_fine_thank_you");

	@Nullable
	private JingleLibrary library;

	@Nullable
	private Jingle jingle;

	@Nullable
	private List<PlayTick> playTicks;

	private int length;

	private int lightOffset;

	private boolean isPlaying;

	private int currentTickIndex;

	private int rest;

	private int timePassed;

	private EnumParticleTypes[] noteParticle;

	@Nullable
	public Jingle getJingle() {
		return jingle;
	}

	public void start(JingleLibrary library, Jingle jingle, int lightOffset) {
		this.library = library;
		this.jingle = jingle;
		this.lightOffset = lightOffset;
		isPlaying = true;
		currentTickIndex = 0;
		rest = 0;
		timePassed = 0;
		init();
	}

	private void init() {
		playTicks = jingle.getPlayTicks();
		length = 0;
		for (PlayTick playTick : playTicks) {
			length += playTick.getLength();
		}
		if ("playing_with_fire".equals(jingle.getId())) {
			noteParticle = new EnumParticleTypes[] { EnumParticleTypes.NOTE, EnumParticleTypes.LAVA };
		} else if (WITH_LOVE.contains(jingle.getId())) {
			noteParticle = new EnumParticleTypes[] { EnumParticleTypes.NOTE, EnumParticleTypes.HEART };
		} else {
			noteParticle = new EnumParticleTypes[] { EnumParticleTypes.NOTE };
		}
	}

	private void stop() {
		library = null;
		jingle = null;
		lightOffset = 0;
		playTicks = null;
		length = 0;
		isPlaying = false;
		currentTickIndex = 0;
		rest = 0;
		timePassed = 0;
	}

	public boolean isPlaying() {
		return isPlaying;
	}

	public float getProgress() {
		return isPlaying ? timePassed / (float) length : 0;
	}

	public void tick(World world, Vec3d origin, Light[] lights, boolean isClient) {
		timePassed++;
		if (rest <= 0) {
			if (currentTickIndex >= playTicks.size()) {
				stop();
			} else {
				PlayTick playTick = playTicks.get(currentTickIndex++);
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
				lights[idx].jingle(world, origin, note, FLSounds.JINGLE_BELL, noteParticle);
			}
		}
	}

	public NBTTagCompound serialize() {
		NBTTagCompound compound = new NBTTagCompound();
		if (jingle != null) {
			compound.setInteger("library", library.getId());
			compound.setString("jingle", jingle.getId());
			compound.setInteger("lightOffset", lightOffset);
			compound.setBoolean("isPlaying", isPlaying);
			compound.setInteger("currentTickIndex", currentTickIndex);
			compound.setInteger("rest", rest);
			compound.setInteger("timePassed", timePassed);
		}
		return compound;
	}

	public void deserialize(NBTTagCompound compound) {
		stop();
		boolean isPlaying = compound.getBoolean("isPlaying");
		if (!isPlaying) {
			return;
		}
		JingleLibrary library = JingleLibrary.fromId(compound.getInteger("library"));
		if (library == null) {
			return;
		}
		Jingle jingle = library.get(compound.getString("jingle"));
		if (jingle == null) {
			return;
		}
		this.library = library;
		this.jingle = jingle;
		this.isPlaying = isPlaying;
		lightOffset = compound.getInteger("lightOffset");
		currentTickIndex = compound.getInteger("currentTickIndex");
		rest = compound.getInteger("rest");
		timePassed = compound.getInteger("timePassed");
		init();
	}
}
