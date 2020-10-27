package me.paulf.fairylights.client;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public final class TranslucentLightRenderer {
    private static final RenderType TRANSLUCENT = get(AtlasTexture.LOCATION_BLOCKS_TEXTURE);

    public static final RenderType MASK = RenderTypeAccessor.MASK;

    static final class RenderTypeAccessor extends RenderType {
        @SuppressWarnings("ConstantConditions")
        RenderTypeAccessor() {
            super(null, null, 0, 0, false, false, null, null);
        }

        static final RenderType MASK = makeType("fairylights:mask", DefaultVertexFormats.POSITION, GL11.GL_QUADS, 256, RenderType.State.getBuilder().texture(NO_TEXTURE).writeMask(DEPTH_WRITE).build(false));

        static RenderType get(final ResourceLocation texture) {
            final RenderType.State state = RenderType.State.getBuilder()
                .texture(new RenderState.TextureState(texture, false, false))
                .transparency(TRANSLUCENT_TRANSPARENCY)
                .diffuseLighting(DIFFUSE_LIGHTING_ENABLED)
                .alpha(DEFAULT_ALPHA)
                .depthTest(DEPTH_EQUAL)
                .cull(CULL_DISABLED)
                .lightmap(LIGHTMAP_ENABLED)
                .overlay(OVERLAY_ENABLED)
                .build(true);
            return RenderType.makeType("fairylights:translucent", DefaultVertexFormats.ENTITY, GL11.GL_QUADS, 256, true, true, state);
        }
    }

    public static RenderType get(final ResourceLocation texture) {
        return RenderTypeAccessor.get(texture);
    }

    public static void finish() {
        final IRenderTypeBuffer.Impl buf = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
        buf.finish(RenderTypeAccessor.MASK);
        buf.finish(TRANSLUCENT);
    }

    public static void addFixed(final Object2ObjectLinkedOpenHashMap<RenderType, BufferBuilder> map) {
        map.put(RenderTypeAccessor.MASK, new BufferBuilder(RenderTypeAccessor.MASK.getBufferSize()));
        map.put(TRANSLUCENT, new BufferBuilder(TRANSLUCENT.getBufferSize()));
    }

    public static IVertexBuilder get(final IRenderTypeBuffer source, final RenderMaterial material) {
        return material.getBuffer(source, RenderType::getEntityTranslucent);
    }
}
