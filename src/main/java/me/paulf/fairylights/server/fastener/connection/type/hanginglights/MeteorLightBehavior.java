package me.paulf.fairylights.server.fastener.connection.type.hanginglights;

import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class MeteorLightBehavior implements ColorLightBehavior {
    private final float red;

    private final float green;

    private final float blue;

    private final TwinkleLogic logic = new TwinkleLogic(0.02F, 100);

    public MeteorLightBehavior(final float red, final float green, final float blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    @Override
    public float getRed(final float delta) {
        return this.red;
    }

    @Override
    public float getGreen(final float delta) {
        return this.green;
    }

    @Override
    public float getBlue(final float delta) {
        return this.blue;
    }

    @Override
    public void tick(final World world, final Vec3d origin, final Light<?> light, final boolean powered) {
        this.logic.tick(world.rand, powered);
    }

    public float getProgress(final float delta) {
        return this.logic.get(delta);
    }
}
