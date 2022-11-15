package me.paulf.fairylights.server.feature.light;

import me.paulf.fairylights.server.config.FLConfig;
import me.paulf.fairylights.server.feature.HangingFeature;
import me.paulf.fairylights.server.item.LightVariant;
import me.paulf.fairylights.server.sound.FLSounds;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class Light<T extends LightBehavior> extends HangingFeature {
    private static final int SWAY_RATE = 10;

    private static final int SWAY_PEAK_COUNT = 5;

    private static final int SWAY_CYCLE = SWAY_RATE * SWAY_PEAK_COUNT;

    private final ItemStack item;

    private final LightVariant<T> variant;

    private final T behavior;

    private int sway;

    private boolean swaying;

    private boolean swayDirection;

    private int tick;

    private int lastJingledTick = -1;

    private boolean powered;

    public Light(final int index, final Vec3 point, final float yaw, final float pitch, final ItemStack item, final LightVariant<T> variant, final float descent) {
        super(index, point, yaw, pitch, 0.0F, descent);
        this.item = item;
        this.variant = variant;
        this.behavior = variant.createBehavior(item);
    }

    public T getBehavior() {
        return this.behavior;
    }

    public ItemStack getItem() {
        return this.item;
    }

    public LightVariant<T> getVariant() {
        return this.variant;
    }

    public void jingle(final Level world, final Vec3 origin, final int note) {
        this.jingle(world, origin, note, ParticleTypes.NOTE);
    }

    public void jingle(final Level world, final Vec3 origin, final int note, final ParticleOptions particle) {
        this.jingle(world, origin, note, FLSounds.JINGLE_BELL.get(), particle);
    }

    public void jingle(final Level world, final Vec3 origin, final int note, final SoundEvent sound, final ParticleOptions... particles) {
        if (world.isClientSide()) {
            final double x = origin.x + this.point.x;
            final double y = origin.y + this.point.y;
            final double z = origin.z + this.point.z;
            for (final ParticleOptions particle : particles) {
                double vx = world.random.nextGaussian();
                double vy = world.random.nextGaussian();
                double vz = world.random.nextGaussian();
                final double t = world.random.nextDouble() * (0.4 - 0.2) + 0.2;
                final double mag = t / Math.sqrt(vx * vx + vy * vy + vz * vz);
                vx *= mag;
                vy *= mag;
                vz *= mag;
                world.addParticle(particle, x + vx, y + vy, z + vz, particle == ParticleTypes.NOTE ? note / 24D : 0, 0, 0);
            }
            if (this.lastJingledTick != this.tick) {
                world.playLocalSound(x, y, z, sound, SoundSource.BLOCKS, FLConfig.getJingleAmplitude() / 16F, (float) Math.pow(2, (note - 12) / 12F), false);
                this.startSwaying(world.random.nextBoolean());
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

    public void power(final boolean powered, final boolean now) {
        this.behavior.power(powered, now, this);
        this.powered = powered;
    }

    public boolean isPowered() {
        return this.powered;
    }

    public void tick(final Level world, final Vec3 origin) {
        super.tick(world);
        this.behavior.tick(world, origin, this);
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
    public AABB getBounds() {
        return this.getVariant().getBounds();
    }

    @Override
    public boolean parallelsCord() {
        return this.getVariant().parallelsCord();
    }
}
