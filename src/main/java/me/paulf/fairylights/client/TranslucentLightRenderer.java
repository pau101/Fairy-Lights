package me.paulf.fairylights.client;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.client.ForgeRenderTypes;
import org.lwjgl.opengl.GL11;

public final class TranslucentLightRenderer {
    private static final RenderType TRANSLUCENT = ForgeRenderTypes.getUnsortedTranslucent(AtlasTexture.LOCATION_BLOCKS_TEXTURE);

    public static final RenderType MASK = RenderTypeAccessor.MASK;

    static final class RenderTypeAccessor extends RenderType {
        @SuppressWarnings("ConstantConditions")
        RenderTypeAccessor() {
            super(null, null, 0, 0, false, false, null, null);
        }

        static final RenderType MASK = makeType("fairylights:mask", DefaultVertexFormats.POSITION, GL11.GL_QUADS, 256, RenderType.State.getBuilder().texture(NO_TEXTURE).writeMask(DEPTH_WRITE).build(false));
    }

    public static void finish() {
        final IRenderTypeBuffer.Impl buf = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
        buf.finish(MASK);
        buf.finish(TRANSLUCENT);
    }

    public static void addFixed(final Object2ObjectLinkedOpenHashMap<RenderType, BufferBuilder> map) {
        map.put(MASK, new BufferBuilder(MASK.getBufferSize()));
        map.put(TRANSLUCENT, new BufferBuilder(TRANSLUCENT.getBufferSize()));
    }

    public static IVertexBuilder get(final IRenderTypeBuffer source, final RenderMaterial material) {
        return material.getSprite().wrapBuffer(source.getBuffer(Atlases.getTranslucentCullBlockType()));
    }
}
