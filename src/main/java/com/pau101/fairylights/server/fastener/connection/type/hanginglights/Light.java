package com.pau101.fairylights.server.fastener.connection.type.hanginglights;

import com.pau101.fairylights.server.config.Configurator;
import com.pau101.fairylights.server.fastener.connection.type.ConnectionHangingFeature.HangingFeature;
import com.pau101.fairylights.server.item.LightVariant;
import com.pau101.fairylights.server.sound.FLSounds;
import com.pau101.fairylights.util.CubicBezier;
import com.pau101.fairylights.util.Mth;

import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public final class Light extends HangingFeature<Light> {
	private static final CubicBezier EASE_IN_OUT = new CubicBezier(0.4F, 0, 0.6F, 1);

	private static final int NORMAL_LIGHT = -1;

	private static final int SWAY_RATE = 10;

	private static final int SWAY_PEAK_COUNT = 5;

	private static final int SWAY_CYCLE = SWAY_RATE * SWAY_PEAK_COUNT;

	private LightVariant variant = LightVariant.FAIRY;

	private Vec3d color = new Vec3d(1, 0.92, 0.76);

	private int prevTwinkleTime;

	private int twinkleTime = NORMAL_LIGHT;

	private boolean isTwinkling;

	private int sway;

	private boolean swaying;

	private boolean swayDirection;

	public Light(int index, Vec3d point, Vec3d rotation) {
		super(index, point, rotation);
	}

	public boolean isTwinkling() {
		return isTwinkling;
	}

	public float getBrightness(float delta) {
		return twinkleTime == NORMAL_LIGHT ? isTwinkling ? 0 : 1 : brightnessFunc((prevTwinkleTime + (twinkleTime - prevTwinkleTime) * delta) / getVariant().getTickCycle());
	}

	public float getTwinkleTimePercent(float delta) {
		return twinkleTime == NORMAL_LIGHT ? 0 : (prevTwinkleTime + (twinkleTime - prevTwinkleTime) * delta) / getVariant().getTickCycle();
	}

	private float brightnessFunc(float x) {
		return x < 0.25F ? EASE_IN_OUT.eval(x / 0.25F) : 1 - EASE_IN_OUT.eval(Mth.transform(x, 0.25F, 1, 0, 1));
	}

	public LightVariant getVariant() {
		return variant;
	}

	public Vec3d getLight() {
		return color;
	}

	public void setVariant(LightVariant variant) {
		this.variant = variant;
	}

	public void setColor(int colorValue) {
		color = new Vec3d((colorValue >> 16 & 0xFF) / 255F, (colorValue >> 8 & 0xFF) / 255F, (colorValue & 0xFF) / 255F);
	}

	public void setRotation(Vec3d rotation) {
		this.rotation = rotation;
	}

	public void setTwinkleTime(int twinkleTime) {
		this.twinkleTime = twinkleTime;
	}

	public int getTwinkleTime() {
		return twinkleTime;
	}

	@Override
	public void inherit(Light parent) {
		super.inherit(parent);
		twinkleTime = parent.twinkleTime;
		swayDirection = parent.swayDirection;
		swaying = parent.swaying;
		sway = parent.sway;
	}

	public void jingle(World world, Vec3d origin, int note) {
		jingle(world, origin, note, EnumParticleTypes.NOTE);
	}

	public void jingle(World world, Vec3d origin, int note, EnumParticleTypes particle) {
		jingle(world, origin, note, FLSounds.JINGLE_BELL, particle);
	}

	public void jingle(World world, Vec3d origin, int note, SoundEvent sound, EnumParticleTypes... particles) {
		if (world.isRemote) {
			double x = origin.xCoord + point.xCoord / 16;
			double y = origin.yCoord + point.yCoord / 16;
			double z = origin.zCoord + point.zCoord / 16;
			for (EnumParticleTypes particle : particles) {
				double vx = world.rand.nextGaussian();
				double vy = world.rand.nextGaussian();
				double vz = world.rand.nextGaussian();
				double t = world.rand.nextDouble() * (0.4 - 0.2) + 0.2;
				double mag = t / Math.sqrt(vx * vx + vy * vy + vz * vz);
				vx *= mag;
				vy *= mag;
				vz *= mag;
				world.spawnParticle(particle, x + vx, y + vy, z + vz, particle == EnumParticleTypes.NOTE ? note / 24D : 0, 0, 0);	
			}
			world.playSound(x, y, z, sound, SoundCategory.BLOCKS, Configurator.getJingleAmplitude() / 16F, (float) Math.pow(2, (note - 12) / 12F), false);
			startSwaying(world.rand.nextBoolean());
		}
	}

	public void startSwaying(boolean swayDirection) {
		this.swayDirection = swayDirection;
		swaying = true;
		sway = 0;
	}

	public void stopSwaying() {
		sway = 0;
		rotation = new Vec3d(rotation.xCoord, rotation.yCoord, 0);
		swaying = false;
	}

	public void tick(ConnectionHangingLights lights, boolean twinkle) {
		prevRotation = rotation;
		prevTwinkleTime = twinkleTime;
		isTwinkling = twinkle;
		if (isTwinkling || variant.alwaysDoTwinkleLogic()) {
			if (lights.getWorld().rand.nextFloat() < getVariant().getTwinkleChance() && twinkleTime == NORMAL_LIGHT) {
				twinkleTime = 0;
			}
			if (twinkleTime >= 0) {
				twinkleTime++;
			}
			if (twinkleTime == variant.getTickCycle()) {
				twinkleTime = NORMAL_LIGHT;
			}
		}
		if (swaying) {
			if (sway >= SWAY_CYCLE) {
				stopSwaying();
			} else {
				rotation = new Vec3d(rotation.xCoord, rotation.yCoord, (float) (Math.sin((swayDirection ? 1 : -1) * 2 * Math.PI / SWAY_RATE * sway) * Math.pow(180 / Math.PI * 2, -sway / (float) SWAY_CYCLE)));
				sway++;
			}
		}
	}

	@Override
	public double getWidth() {
		return variant.getWidth();
	}

	@Override
	public double getHeight() {
		return variant.getHeight();
	}

	@Override
	public boolean parallelsCord() {
		return variant.parallelsCord();
	}
}
