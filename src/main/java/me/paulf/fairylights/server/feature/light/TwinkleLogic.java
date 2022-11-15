package me.paulf.fairylights.server.feature.light;

import net.minecraft.util.Mth;

import java.util.Random;

public class TwinkleLogic {
    private final float chance;

    private final int duration;

    private int time = -1;

    private int prevTime = -1;

    public TwinkleLogic(final float chance, final int duration) {
        this.chance = chance;
        this.duration = duration;
    }

    public float get(final float delta) {
        return this.time == -1 ? 0.0F : Mth.lerp(delta, this.prevTime, this.time) / this.duration;
    }

    public void tick(final Random rng, final boolean powered) {
        this.prevTime = this.time;
        if (this.time != -1 || rng.nextFloat() < this.chance) this.time++;
        if (this.time >= this.duration || !powered) this.time = -1;
    }
}
