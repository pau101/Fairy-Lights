package me.paulf.fairylights.client.model.light;

import me.paulf.fairylights.util.*;
import net.minecraft.client.renderer.model.*;

import java.util.*;

public class SnowflakeLightModel extends LightModel {
    public SnowflakeLightModel() {
        final ModelRenderer connector = new ModelRenderer(this, 90, 40);
        connector.addBox(-1.0F, 0.2F, -1.0F, 2.0F, 1.0F, 2.0F, -0.05F);
        this.unlit.addChild(connector);
        final BulbBuilder bulb = this.createBulb();
        final Random rng = new Random(0xFE337752);
        final float size = 8.0F;
        final int branches = 6;
        for (int n = 0; n < branches; n++) {
            final BulbBuilder branch = bulb.createChild(10, 37);
            branch.addBox(-0.5F, 0.0F, -0.5F, 1.0F, size, 1.0F, rng.nextFloat() * 0.01F + 0.1F);
            branch.setPosition(0.0F, -size, 0.0F);
            branch.setAngles(0.0F, 0.0F, n * Mth.TAU / branches);
            for (int side = -1; side <= 1; side += 2) {
                final BulbBuilder sub = branch.createChild(10, 39);
                sub.addBox(-0.5F, 0.0F, -0.5F, 1.0F, size * 0.4F, 1.0F, rng.nextFloat() * 0.01F - 0.05F);
                sub.setAngles(0.0F, 0.0F, Mth.PI * 0.75F * side);
                sub.setPosition(-0.2F * side, -size * 0.55F, 0.0F);
            }
        }
    }
}
