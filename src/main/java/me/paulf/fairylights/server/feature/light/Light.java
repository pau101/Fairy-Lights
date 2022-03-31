package me.paulf.fairylights.server.feature.light;

import me.paulf.fairylights.server.config.FLConfig;
import me.paulf.fairylights.server.feature.HangingFeature;
import me.paulf.fairylights.server.item.LightVariant;
import me.paulf.fairylights.server.sound.FLSounds;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

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

    public Light(final int index, final Vector3d point, final float yaw, final float pitch, final ItemStack item, final LightVariant<T> variant, final float descent) {
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

    public void jingle(final World world, final Vector3d origin, final int note) {
        this.jingle(world, origin, note, ParticleTypes.field_197597_H);
    }

    public void jingle(final World world, final Vector3d origin, final int note, final BasicParticleType particle) {
        this.jingle(world, origin, note, FLSounds.JINGLE_BELL.get(), particle);
    }

    public void jingle(final World world, final Vector3d origin, final int note, final SoundEvent sound, final BasicParticleType... particles) {
        if (world.field_72995_K) {
            final double x = origin.field_72450_a + this.point.field_72450_a;
            final double y = origin.field_72448_b + this.point.field_72448_b;
            final double z = origin.field_72449_c + this.point.field_72449_c;
            for (final BasicParticleType particle : particles) {
                double vx = world.field_73012_v.nextGaussian();
                double vy = world.field_73012_v.nextGaussian();
                double vz = world.field_73012_v.nextGaussian();
                final double t = world.field_73012_v.nextDouble() * (0.4 - 0.2) + 0.2;
                final double mag = t / Math.sqrt(vx * vx + vy * vy + vz * vz);
                vx *= mag;
                vy *= mag;
                vz *= mag;
                world.func_195594_a(particle, x + vx, y + vy, z + vz, particle == ParticleTypes.field_197597_H ? note / 24D : 0, 0, 0);
            }
            if (this.lastJingledTick != this.tick) {
                world.func_184134_a(x, y, z, sound, SoundCategory.BLOCKS, FLConfig.getJingleAmplitude() / 16F, (float) Math.pow(2, (note - 12) / 12F), false);
                this.startSwaying(world.field_73012_v.nextBoolean());
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

    public void tick(final World world, final Vector3d origin) {
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
    public AxisAlignedBB getBounds() {
        return this.getVariant().getBounds();
    }

    @Override
    public boolean parallelsCord() {
        return this.getVariant().parallelsCord();
    }
}
