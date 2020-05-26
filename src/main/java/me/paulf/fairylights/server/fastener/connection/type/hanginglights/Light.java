package me.paulf.fairylights.server.fastener.connection.type.hanginglights;

import me.paulf.fairylights.server.config.*;
import me.paulf.fairylights.server.fastener.connection.type.*;
import me.paulf.fairylights.server.item.*;
import me.paulf.fairylights.server.sound.*;
import me.paulf.fairylights.util.*;
import net.minecraft.particles.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;

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

    private int tick;

    private int lastJingledTick = -1;

    public Light(final int index, final Vec3d point, final float yaw, final float pitch, final boolean isOn) {
        super(index, point, yaw, pitch, 0.0F);
        this.isTwinkling = !isOn;
    }

    public boolean isTwinkling() {
        return this.isTwinkling;
    }

    public float getBrightness(final float delta) {
        return this.twinkleTime == NORMAL_LIGHT ? this.isTwinkling ? 0 : 1 : this.brightnessFunc((this.prevTwinkleTime + (this.twinkleTime - this.prevTwinkleTime) * delta) / this.getVariant().getTickCycle());
    }

    public float getTwinkleTimePercent(final float delta) {
        return this.twinkleTime == NORMAL_LIGHT ? 0 : (this.prevTwinkleTime + (this.twinkleTime - this.prevTwinkleTime) * delta) / this.getVariant().getTickCycle();
    }

    private float brightnessFunc(final float x) {
        return x < 0.25F ? EASE_IN_OUT.eval(x / 0.25F) : 1 - EASE_IN_OUT.eval(Mth.transform(x, 0.25F, 1, 0, 1));
    }

    public LightVariant getVariant() {
        return this.variant;
    }

    public Vec3d getLight() {
        return this.color;
    }

    public void setVariant(final LightVariant variant) {
        this.variant = variant;
    }

    public void setColor(final int colorValue) {
        this.color = new Vec3d((colorValue >> 16 & 0xFF) / 255F, (colorValue >> 8 & 0xFF) / 255F, (colorValue & 0xFF) / 255F);
    }

    public void setTwinkleTime(final int twinkleTime) {
        this.twinkleTime = twinkleTime;
    }

    public int getTwinkleTime() {
        return this.twinkleTime;
    }

    @Override
    public void inherit(final Light parent) {
        super.inherit(parent);
        this.twinkleTime = parent.twinkleTime;
        this.swayDirection = parent.swayDirection;
        this.swaying = parent.swaying;
        this.sway = parent.sway;
        this.tick = parent.tick;
        this.lastJingledTick = parent.lastJingledTick;
    }

    public void jingle(final World world, final Vec3d origin, final int note) {
        this.jingle(world, origin, note, ParticleTypes.NOTE);
    }

    public void jingle(final World world, final Vec3d origin, final int note, final BasicParticleType particle) {
        this.jingle(world, origin, note, FLSounds.JINGLE_BELL.orElseThrow(IllegalStateException::new), particle);
    }

    public void jingle(final World world, final Vec3d origin, final int note, final SoundEvent sound, final BasicParticleType... particles) {
        if (world.isRemote) {
            final double x = origin.x + this.point.x;
            final double y = origin.y + this.point.y;
            final double z = origin.z + this.point.z;
            for (final BasicParticleType particle : particles) {
                double vx = world.rand.nextGaussian();
                double vy = world.rand.nextGaussian();
                double vz = world.rand.nextGaussian();
                final double t = world.rand.nextDouble() * (0.4 - 0.2) + 0.2;
                final double mag = t / Math.sqrt(vx * vx + vy * vy + vz * vz);
                vx *= mag;
                vy *= mag;
                vz *= mag;
                world.addParticle(particle, x + vx, y + vy, z + vz, particle == ParticleTypes.NOTE ? note / 24D : 0, 0, 0);
            }
            if (this.lastJingledTick != this.tick) {
                world.playSound(x, y, z, sound, SoundCategory.BLOCKS, FLConfig.getJingleAmplitude() / 16F, (float) Math.pow(2, (note - 12) / 12F), false);
                this.startSwaying(world.rand.nextBoolean());
                this.lastJingledTick = this.tick;
            }
        }
    }

    public void startSwaying(final boolean swayDirection) {
        this.swayDirection = swayDirection;
        this.swaying = true;
        this.sway = 0;
    }

    public void stopSwaying() {
        this.sway = 0;
        this.roll = 0.0F;
        this.swaying = false;
    }

    public void setOn(final boolean on) {
        this.twinkleTime = NORMAL_LIGHT;
        this.isTwinkling = !on;
    }

    public boolean isOn() {
        return this.twinkleTime == NORMAL_LIGHT && !this.isTwinkling;
    }

    public void tick(final HangingLightsConnection lights, final boolean twinkle, final boolean isOn) {
        this.prevYaw = this.yaw;
        this.prevPitch = this.pitch;
        this.prevRoll = this.roll;
        this.prevTwinkleTime = this.twinkleTime;
        if (isOn) {
            if (this.isTwinkling || this.variant.alwaysDoTwinkleLogic()) {
                if (lights.getWorld().rand.nextFloat() < this.getVariant().getTwinkleChance() && this.twinkleTime == NORMAL_LIGHT) {
                    this.twinkleTime = 0;
                }
                if (this.twinkleTime >= 0) {
                    this.twinkleTime++;
                }
                if (this.twinkleTime == this.variant.getTickCycle()) {
                    this.twinkleTime = NORMAL_LIGHT;
                }
            } else {
                this.twinkleTime = NORMAL_LIGHT;
            }
            this.isTwinkling = twinkle;
        } else {
            this.twinkleTime = NORMAL_LIGHT;
            this.isTwinkling = true;
        }
        if (this.swaying) {
            if (this.sway >= SWAY_CYCLE) {
                this.stopSwaying();
            } else {
                this.roll = (float) (Math.sin((this.swayDirection ? 1 : -1) * 2 * Math.PI / SWAY_RATE * this.sway) * Math.pow(180 / Math.PI * 2, -this.sway / (float) SWAY_CYCLE));
                this.sway++;
            }
        }
        this.tick++;
    }

    @Override
    public double getWidth() {
        return this.variant.getWidth();
    }

    @Override
    public double getHeight() {
        return this.variant.getHeight();
    }

    @Override
    public boolean parallelsCord() {
        return this.variant.parallelsCord();
    }
}
