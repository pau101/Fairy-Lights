package com.pau101.fairylights.eggs;

import java.util.List;

import net.minecraft.client.Minecraft;

import com.pau101.fairylights.FairyLights;
import com.pau101.fairylights.config.Configurator;
import com.pau101.fairylights.eggs.Jingle.PlayTick;
import com.pau101.fairylights.util.Light;
import com.pau101.fairylights.util.vectormath.Point3f;

public class JinglePlayer {
	private Jingle jingle;

	private List<PlayTick> jinglePlayTicks;

	private int lightOffset;

	private int tick;

	private int jingleLength;

	private static final String SOUND_FX = FairyLights.MODID + ":jinglebell";

	private static final int NOT_PLAYING = -1;

	public JinglePlayer() {
		tick = NOT_PLAYING;
	}

	public void play(Jingle jingle, int lightOffset) {
		this.jingle = jingle;
		jinglePlayTicks = jingle.getPlayTicks();
		this.lightOffset = lightOffset;
		tick = 0;
		jingleLength = 0;
		for (PlayTick playTick : jinglePlayTicks) {
			jingleLength += playTick.getLength();
		}
	}

	public boolean isPlaying() {
		return tick != NOT_PLAYING;
	}

	public void play(int index, Point3f coordinateOffset, Light light) {
		Minecraft mc = Minecraft.getMinecraft();
		int played = 0;
		for (PlayTick playTick : jinglePlayTicks) {
			if (played == tick) {
				for (int note : playTick.getNotes()) {
					if (note == index + jingle.getLowestNote() - lightOffset) {
						Point3f lightPoint = light.getPoint();
						double x = coordinateOffset.x + lightPoint.x / 16;
						double y = coordinateOffset.y + lightPoint.y / 16;
						double z = coordinateOffset.z + lightPoint.z / 16;
						mc.theWorld.playSound(x, y, z, SOUND_FX, Configurator.jingleAmplitude / 16F, (float) Math.pow(2, (note - 12) / 12F), true);
						light.startSwaying();
						break;
					}
				}
				return;
			} else if (played > tick) {
				// resting
				return;
			}
			played += playTick.getLength();
		}
		if (tick >= jingleLength) {
			tick = NOT_PLAYING;
		}
	}

	public void setLightOffset(int lightOffset) {
		this.lightOffset = lightOffset;
	}

	public void tick() {
		tick++;
	}
}
