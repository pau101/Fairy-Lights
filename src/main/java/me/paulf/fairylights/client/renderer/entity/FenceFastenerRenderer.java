package me.paulf.fairylights.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import me.paulf.fairylights.FairyLights;
import me.paulf.fairylights.client.renderer.block.entity.FastenerRenderer;
import me.paulf.fairylights.server.capability.CapabilityHandler;
import me.paulf.fairylights.server.entity.FenceFastenerEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LightLayer;

public final class FenceFastenerRenderer extends EntityRenderer<FenceFastenerEntity> {
    public static final ResourceLocation MODEL = new ResourceLocation(FairyLights.ID, "block/fence_fastener");

    private final FastenerRenderer renderer;

    public FenceFastenerRenderer(final EntityRendererProvider.Context context) {
        super(context);
        this.renderer = new FastenerRenderer(context::bakeLayer);
    }

    @Override
    protected int getBlockLightLevel(final FenceFastenerEntity entity, final BlockPos delta) {
        return entity.level.getBrightness(LightLayer.BLOCK, entity.blockPosition());
    }

    @Override
    public void render(final FenceFastenerEntity entity, final float yaw, final float delta, final PoseStack matrix, final MultiBufferSource source, final int packedLight) {
        final VertexConsumer buf = source.getBuffer(Sheets.cutoutBlockSheet());
        matrix.pushPose();
        FastenerRenderer.renderBakedModel(MODEL, matrix, buf, 1.0F, 1.0F, 1.0F, packedLight, OverlayTexture.NO_OVERLAY);
        matrix.popPose();
        entity.getCapability(CapabilityHandler.FASTENER_CAP).ifPresent(f -> this.renderer.render(f, delta, matrix, source, packedLight, OverlayTexture.NO_OVERLAY));
        super.render(entity, yaw, delta, matrix, source, packedLight);
    }

    @SuppressWarnings("deprecation")
    @Override
    public ResourceLocation getTextureLocation(final FenceFastenerEntity entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
