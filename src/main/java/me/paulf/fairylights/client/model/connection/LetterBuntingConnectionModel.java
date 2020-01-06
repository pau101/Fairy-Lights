package me.paulf.fairylights.client.model.connection;

import com.mojang.blaze3d.platform.GlStateManager;
import me.paulf.fairylights.FairyLights;
import me.paulf.fairylights.client.model.AdvancedRendererModel;
import me.paulf.fairylights.client.renderer.FastenerRenderer;
import me.paulf.fairylights.server.fastener.Fastener;
import me.paulf.fairylights.server.fastener.connection.type.letter.Letter;
import me.paulf.fairylights.server.fastener.connection.type.letter.LetterBuntingConnection;
import me.paulf.fairylights.util.Mth;
import me.paulf.fairylights.util.styledstring.StyledString;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class LetterBuntingConnectionModel extends ConnectionModel<LetterBuntingConnection> {
    private static final ResourceLocation LETTERS = new ResourceLocation(FairyLights.ID, "textures/entity/letters.png");

    private final AdvancedRendererModel cordModel;

    public LetterBuntingConnectionModel() {
        this.cordModel = new AdvancedRendererModel(this, 0, 17);
        this.cordModel.addBox(-0.5F, -0.5F, 0, 1, 1, 1);
        this.cordModel.scaleX = 0.9F;
        this.cordModel.scaleY = 0.9F;
    }

    @Override
    public boolean hasTexturedRender() {
        return true;
    }

    @Override
    public void renderTexturePass(final Fastener<?> fastener, final LetterBuntingConnection letterBunting, final World world, final int skylight, final int moonlight, final float delta) {
        final Letter[] letters = letterBunting.getLetters();
        final Letter[] prevLetters = letterBunting.getPrevLetters();
        GlStateManager.disableCull();
        GlStateManager.enableNormalize();
        Minecraft.getInstance().getTextureManager().bindTexture(LETTERS);
        for (int i = 0, count = Math.min(letters.length, prevLetters.length); i < count; i++) {
            final Letter letter = letters[i];
            if (Character.isWhitespace(letter.getLetter())) {
                continue;
            }
            final Vec3d point = Mth.lerp(prevLetters[i].getPoint(), letter.getPoint(), delta);
            final Vec3d rotation = letter.getRotation(delta);
            GlStateManager.pushMatrix();
            GlStateManager.translated(point.x / 16, point.y / 16, point.z / 16);
            GlStateManager.rotatef((float) rotation.x * Mth.RAD_TO_DEG, 0, 1, 0);
            GlStateManager.rotatef((float) rotation.y * Mth.RAD_TO_DEG, 1, 0, 0);
            GlStateManager.rotatef((float) rotation.z * Mth.RAD_TO_DEG, 0, 0, 1);
            final int rgb = StyledString.getColor(letter.getStyle().getColor());
            GlStateManager.color3f((rgb >> 16 & 0xFF) / 255F, (rgb >> 8 & 0xFF) / 255F, (rgb & 0xFF) / 255F);
            GlStateManager.rotatef(-90, 0, 1, 0);
            GlStateManager.scalef(1, 1, 1.2F);
            GlStateManager.translated(-letter.getWidth() / 2F, -letter.getSymbolHeight() / 16F + 0.5F / 16F, 0.0625F / 2);
            FastenerRenderer.render3DTexture(letter.getSymbolWidth(), letter.getSymbolHeight(), letter.getU(), letter.getV(), 64, 64);
            GlStateManager.popMatrix();
        }
        GlStateManager.enableCull();
        GlStateManager.disableNormalize();
        GlStateManager.color3f(1, 1, 1);
    }

    @Override
    protected void renderSegment(final LetterBuntingConnection connection, final int index, final double angleX, final double angleY, final double length, final double x, final double y, final double z, final float delta) {
        this.cordModel.rotateAngleX = (float) angleX;
        this.cordModel.rotateAngleY = (float) angleY;
        this.cordModel.scaleZ = (float) length;
        this.cordModel.setRotationPoint(x, y, z);
        this.cordModel.render(0.0625F);
    }
}
