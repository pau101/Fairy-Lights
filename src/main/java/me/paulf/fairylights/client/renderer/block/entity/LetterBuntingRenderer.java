package me.paulf.fairylights.client.renderer.block.entity;

import com.mojang.blaze3d.matrix.*;
import com.mojang.blaze3d.vertex.*;
import it.unimi.dsi.fastutil.ints.*;
import me.paulf.fairylights.*;
import me.paulf.fairylights.server.fastener.connection.*;
import me.paulf.fairylights.server.fastener.connection.type.letter.*;
import me.paulf.fairylights.util.*;
import me.paulf.fairylights.util.styledstring.*;
import net.minecraft.client.renderer.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;

import java.util.*;

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
        final Letter[] currLetters = conn.getLetters();
        final Letter[] prevLetters = conn.getPrevLetters();
        if (currLetters == null || prevLetters == null) {
            return;
        }
        final int count = Math.min(currLetters.length, prevLetters.length);
        ;
        if (count == 0) {
            return;
        }
        final IVertexBuilder buf = source.getBuffer(Atlases.getCutoutBlockType());
        for (int i = 0; i < count; i++) {
            final Letter prevPennant = prevLetters[i];
            final Letter currPennant = currLetters[i];
            final ResourceLocation path = MODELS.get(currPennant.getLetter());
            if (path == null) {
                continue;
            }
            final int color = StyledString.getColor(currPennant.getStyle().getColor());
            final float r = ((color >> 16) & 0xFF) / 255.0F;
            final float g = ((color >> 8) & 0xFF) / 255.0F;
            final float b = (color & 0xFF) / 255.0F;
            final Vec3d pos = Mth.lerp(prevPennant.getPoint(), currPennant.getPoint(), delta);
            matrix.push();
            matrix.translate(pos.x, pos.y, pos.z);
            matrix.rotate(Vector3f.YP.rotation(-currPennant.getYaw(delta)));
            matrix.rotate(Vector3f.ZP.rotation(currPennant.getPitch(delta)));
            matrix.rotate(Vector3f.XP.rotation(currPennant.getRoll(delta)));
            matrix.translate(-0.5F, -1.0F - 0.5F / 16.0F, -0.5F);
            this.renderBakedModel(path, matrix, buf, r, g, b, packedLight, packedOverlay);
            matrix.pop();
        }
    }
}
