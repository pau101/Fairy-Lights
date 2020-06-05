package me.paulf.fairylights.server.fastener.connection.type.hanginglights;

import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class OilLanternBehavior implements BrightLightBehavior {
    private float value = 1.0F;

    @Override
    public float getBrightness(final float delta) {
        return this.value;
    }

    @Override
    public void tick(final World world, final Vec3d origin, final Light<?> light, final boolean powered) {
        this.value = powered ? 1.0F : 0.0F;
        if (powered && world.rand.nextFloat() < 0.08F) {
            final Vec3d p = light.getAbsolutePoint(origin); // FIXME transformed pos
            final double x = p.getX();
            final double y = p.getY() - 0.28D;
            final double z = p.getZ();
            world.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0D, 0.0D, 0.0D);
            world.addParticle(ParticleTypes.FLAME, x, y, z, 0.0D, 0.0D, 0.0D);
        }
    }
}
