package me.paulf.fairylights.client.renderer.block.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import me.paulf.fairylights.FairyLights;
import me.paulf.fairylights.util.Catenary;
import me.paulf.fairylights.server.feature.Letter;
import me.paulf.fairylights.server.connection.LetterBuntingConnection;
import me.paulf.fairylights.util.styledstring.StyledString;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

import java.util.Locale;

public class LetterBuntingRenderer extends ConnectionRenderer<LetterBuntingConnection> {
    public static final Int2ObjectMap<ResourceLocation> MODELS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ&!?".chars()
        .collect(
            Int2ObjectOpenHashMap::new,
            (map, cp) -> map.put(cp, new ResourceLocation(FairyLights.ID, "entity/letter/" + Character.getName(cp).toLowerCase(Locale.ROOT).replaceAll("[^a-z]", "_"))),
            Int2ObjectOpenHashMap::putAll
        );

    public LetterBuntingRenderer() {
        super(0, 17, 1.0F);
    }

    @Override
    protected void render(final LetterBuntingConnection conn, final Catenary catenary, final float delta, final MatrixStack matrix, final IRenderTypeBuffer source, final int packedLight, final int packedOverlay) {
        super.render(conn, catenary, delta, matrix, source, packedLight, packedOverlay);
        final Letter[] letters = conn.getLetters();
        if (letters == null) {
            return;
        }
        final int count = letters.length;
        if (count == 0) {
            return;
        }
        final IVertexBuilder buf = source.getBuffer(Atlases.func_228783_h_());
        for (final Letter letter : letters) {
            final ResourceLocation path = MODELS.get(letter.getLetter());
            if (path == null) {
                continue;
            }
            final int color = StyledString.getColor(letter.getStyle().getColor());
            final float r = ((color >> 16) & 0xFF) / 255.0F;
            final float g = ((color >> 8) & 0xFF) / 255.0F;
            final float b = (color & 0xFF) / 255.0F;
            final Vector3d pos = letter.getPoint(delta);
            matrix.func_227860_a_();
            matrix.func_227861_a_(pos.field_72450_a, pos.field_72448_b, pos.field_72449_c);
            matrix.func_227863_a_(Vector3f.field_229181_d_.func_229193_c_(-letter.getYaw(delta)));
            matrix.func_227863_a_(Vector3f.field_229183_f_.func_229193_c_(letter.getPitch(delta)));
            matrix.func_227863_a_(Vector3f.field_229179_b_.func_229193_c_(letter.getRoll(delta)));
            matrix.func_227861_a_(-0.5F, -1.0F - 0.5F / 16.0F, -0.5F);
            FastenerRenderer.renderBakedModel(path, matrix, buf, r, g, b, packedLight, packedOverlay);
            matrix.func_227865_b_();
        }
    }
}
