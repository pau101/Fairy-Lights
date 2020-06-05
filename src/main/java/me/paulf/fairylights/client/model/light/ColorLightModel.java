package me.paulf.fairylights.client.model.light;

import me.paulf.fairylights.server.fastener.connection.type.hanginglights.Light;
import me.paulf.fairylights.server.fastener.connection.type.hanginglights.StandardLightBehavior;

public class ColorLightModel extends LightModel<StandardLightBehavior> {
    @Override
    public void animate(final Light<StandardLightBehavior> light, final float delta) {
        super.animate(light, delta);
        this.brightness = light.getBehavior().getBrightness(delta);
        this.red = light.getBehavior().getRed(delta);
        this.green = light.getBehavior().getGreen(delta);
        this.blue = light.getBehavior().getBlue(delta);
    }
}
