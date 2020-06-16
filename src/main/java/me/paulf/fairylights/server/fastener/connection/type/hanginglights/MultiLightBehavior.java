package me.paulf.fairylights.server.fastener.connection.type.hanginglights;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.function.Supplier;

public class MultiLightBehavior implements LightBehavior {
    private final ImmutableList<StandardLightBehavior> lights;

    private final DefaultBehavior fallback;

    public MultiLightBehavior(final ImmutableList<StandardLightBehavior> lights) {
        this.lights = lights;
        this.fallback = new DefaultBehavior(1.0F, 1.0F, 1.0F);
    }

    public StandardLightBehavior get(final int index) {
        if (index >= 0 && index < this.lights.size()) {
            return this.lights.get(index);
        }
        return this.fallback;
    }

    @Override
    public void power(final boolean powered, final boolean now) {
        for (final StandardLightBehavior behvior : this.lights) {
            behvior.power(powered, now);
        }
        this.fallback.power(powered, now);
    }

    @Override
    public void tick(final World world, final Vec3d origin, final Light<?> light) {
        for (final StandardLightBehavior behavior : this.lights) {
            behavior.tick(world, origin, light);
        }
        this.fallback.tick(world, origin, light);
    }

    public static MultiLightBehavior create(final int count, final Supplier<StandardLightBehavior> factory) {
        final ImmutableList.Builder<StandardLightBehavior> behaviors = new ImmutableList.Builder<>();
        for (int n = 0;  n < count; n++) {
            behaviors.add(factory.get());
        }
        return new MultiLightBehavior(behaviors.build());
    }
}
