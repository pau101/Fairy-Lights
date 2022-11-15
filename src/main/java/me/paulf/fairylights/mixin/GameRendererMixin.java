package me.paulf.fairylights.mixin;

import me.paulf.fairylights.client.ClientEventHandler;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(at = @At("RETURN"), method = "pick(F)V")
    public void pick(float delta, CallbackInfo ci) {
        ClientEventHandler.updateHitConnection();
    }
}
