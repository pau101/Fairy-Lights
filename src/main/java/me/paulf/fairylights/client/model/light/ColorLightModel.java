package me.paulf.fairylights.client.model.light;

import me.paulf.fairylights.server.feature.light.Light;
import me.paulf.fairylights.server.feature.light.StandardLightBehavior;

public class ColorLightModel extends LightModel<StandardLightBehavior> {
    @Override
    public void animate(final Light<?> light, final StandardLightBehavior behavior, final float delta) {
        super.animate(light, behavior, delta);
        this.brightness = behavior.getBrightness(delta);
        this.red = behavior.getRed(delta);
        this.green = behavior.getGreen(delta);
        this.blue = behavior.getBlue(delta);
    }
}
