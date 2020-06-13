package me.paulf.fairylights.server.fastener.connection.type.hanginglights;

import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class OilLanternBehavior implements BrightLightBehavior {
    private float value = 1.0F;

    private boolean powered = true;

    @Override
    public float getBrightness(final float delta) {
        return this.value;
    }

    @Override
    public void power(final boolean powered, final boolean now) {
        this.powered = powered;
        this.value = this.powered ? 1.0F : 0.0F;
    }

    @Override
    public void tick(final World world, final Vec3d origin, final Light<?> light) {
        if (world.rand.nextFloat() < 0.08F) {
            this.createParticles(world, origin, light);
        }
    }

    @Override
    public void animateTick(final World world, final Vec3d origin, final Light<?> light) {
        this.createParticles(world, origin, light);
    }

    private void createParticles(final World world, final Vec3d origin, final Light<?> light) {
        if (this.powered) {
            final Vec3d p = light.getTransformedPoint(origin, new Vec3d(0.0D, -0.13D, 0.0D));
            final double x = p.getX();
            final double y = p.getY();
            final double z = p.getZ();
            world.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0D, 0.0D, 0.0D);
            world.addParticle(ParticleTypes.FLAME, x, y, z, 0.0D, 0.0D, 0.0D);
        }
    }
}
