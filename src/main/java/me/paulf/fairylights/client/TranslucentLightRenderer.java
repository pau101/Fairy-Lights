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
    private static final RenderType TRANSLUCENT = ForgeRenderTypes.getUnsortedTranslucent(AtlasTexture.field_110575_b);

    public static final RenderType MASK = RenderTypeAccessor.MASK;

    static final class RenderTypeAccessor extends RenderType {
        @SuppressWarnings("ConstantConditions")
        RenderTypeAccessor() {
            super(null, null, 0, 0, false, false, null, null);
        }

        static final RenderType MASK = func_228632_a_("fairylights:mask", DefaultVertexFormats.field_181705_e, GL11.GL_QUADS, 256, RenderType.State.func_228694_a_().func_228724_a_(field_228523_o_).func_228727_a_(field_228497_G_).func_228728_a_(false));
    }

    public static void finish() {
        final IRenderTypeBuffer.Impl buf = Minecraft.func_71410_x().func_228019_au_().func_228487_b_();
        buf.func_228462_a_(MASK);
        buf.func_228462_a_(TRANSLUCENT);
    }

    public static void addFixed(final Object2ObjectLinkedOpenHashMap<RenderType, BufferBuilder> map) {
        map.put(MASK, new BufferBuilder(MASK.func_228662_o_()));
        map.put(TRANSLUCENT, new BufferBuilder(TRANSLUCENT.func_228662_o_()));
    }

    public static IVertexBuilder get(final IRenderTypeBuffer source, final RenderMaterial material) {
        return material.func_229314_c_().func_229230_a_(source.getBuffer(Atlases.func_228785_j_()));
    }
}
