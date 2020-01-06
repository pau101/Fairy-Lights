package me.paulf.fairylights.client.model.connection;

import com.mojang.blaze3d.platform.GlStateManager;
import me.paulf.fairylights.client.model.AdvancedRendererModel;
import me.paulf.fairylights.client.model.RotationOrder;
import me.paulf.fairylights.server.fastener.Fastener;
import me.paulf.fairylights.server.fastener.connection.type.pennant.Pennant;
import me.paulf.fairylights.server.fastener.connection.type.pennant.PennantBuntingConnection;
import me.paulf.fairylights.util.Mth;
import me.paulf.fairylights.util.styledstring.StyledString;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class PennantBuntingConnectionModel extends ConnectionModel<PennantBuntingConnection> {
    private final AdvancedRendererModel cordModel;

    private final AdvancedRendererModel[] pennantModels = {this.createPennant(63), this.createPennant(72), this.createPennant(81)};

    public PennantBuntingConnectionModel() {
        this.cordModel = new AdvancedRendererModel(this, 0, 17);
        this.cordModel.addBox(-0.5F, -0.5F, 0, 1, 1, 1);
        this.cordModel.scaleX = 1.5F;
        this.cordModel.scaleY = 1.5F;
    }

    private AdvancedRendererModel createPennant(final int u) {
        final AdvancedRendererModel pennant = new AdvancedRendererModel(this, u, 16);
        pennant.add3DTexture(-4.5F, -10, 0.5F, 9, 10);
        pennant.setRotationOrder(RotationOrder.YXZ);
        pennant.secondaryRotateAngleY = Mth.HALF_PI;
        return pennant;
    }

    @Override
    public boolean hasTexturedRender() {
        return true;
    }

    @Override
    public void render(final Fastener<?> fastener, final PennantBuntingConnection bunting, final World world, final int skylight, final int moonlight, final float delta) {
        super.render(fastener, bunting, world, skylight, moonlight, delta);
        final Pennant[] pennants = bunting.getFeatures();
        final Pennant[] prevPennants = bunting.getPrevFeatures();
        GlStateManager.disableCull();
        for (int i = 0, count = Math.min(pennants.length, prevPennants.length); i < count; i++) {
            final AdvancedRendererModel model = this.preparePennantModel(pennants, prevPennants, i, delta);
            final int rgb = pennants[i].getColor();
            GlStateManager.color3f(((rgb >> 16) & 0xFF) / 255F, ((rgb >> 8) & 0xFF) / 255F, (rgb & 0xFF) / 255F);
            model.render(0.0625F);
        }
        GlStateManager.enableCull();
        GlStateManager.color3f(1, 1, 1);
    }

    @Override
    public void renderTexturePass(final Fastener<?> fastener, final PennantBuntingConnection bunting, final World world, final int skylight, final int moonlight, final float delta) {
        final Pennant[] pennants = bunting.getFeatures();
        final Pennant[] prevPennants = bunting.getPrevFeatures();
        StyledString text = bunting.getText();
        final int pennantCount = Math.min(pennants.length, prevPennants.length);
        final int offset;
        if (text.length() > pennantCount) {
            final int over = text.length() - pennantCount;
            final int lower = over / 2;
            text = text.substring(lower, text.length() - over + lower);
            offset = 0;
        } else {
            offset = pennantCount / 2 - text.length() / 2;
        }
        final FontRenderer font = Minecraft.getInstance().fontRenderer;
        for (int i = 0; i < text.length(); i++) {
            final int pennantIndex = i + offset;
            final StyledString chrA = text.substring(i, i + 1);
            final StyledString chrB = text.substring(text.length() - i - 1, text.length() - i);
            final String charAStr = chrA.toString();
            final String charBStr = chrB.toString();
            final AdvancedRendererModel model = this.preparePennantModel(pennants, prevPennants, pennantIndex, delta);
            GlStateManager.pushMatrix();
            model.postRender(0.0625F);
            final float s = 0.03075F;
            GlStateManager.pushMatrix();
            GlStateManager.translatef(0, -0.25F, -0.04F);
            GlStateManager.scalef(-s, -s, s);
            GlStateManager.translatef(-font.getStringWidth(charAStr) / 2F + 0.5F, -4, 0);
            GlStateManager.normal3f(0, 0, 1);
            font.drawString(charAStr, 0, 0, 0xFFFFFFFF);
            GlStateManager.popMatrix();
            GlStateManager.translatef(0, -0.25F, 0.04F);
            GlStateManager.scalef(s, -s, s);
            GlStateManager.translatef(-font.getStringWidth(charBStr) / 2F + 0.5F, -4, 0);
            GlStateManager.normal3f(0, 0, -1);
            font.drawString(charBStr, 0, 0, 0xFFFFFFFF);
            GlStateManager.popMatrix();
        }
    }

    private AdvancedRendererModel preparePennantModel(final Pennant[] pennants, final Pennant[] prevPennants, final int index, final float delta) {
        final Pennant pennant = pennants[index];
        final Vec3d point = Mth.lerp(prevPennants[index].getPoint(), pennant.getPoint(), delta);
        final Vec3d rotation = Mth.lerpAngles(prevPennants[index].getRotation(), pennant.getRotation(), delta);
        final AdvancedRendererModel model = this.pennantModels[index % this.pennantModels.length];
        model.setRotationPoint(point.x, point.y, point.z);
        model.setRotationAngles(rotation.y, rotation.x, rotation.z);
        return model;
    }

    @Override
    protected void renderSegment(final PennantBuntingConnection connection, final int index, final double angleX, final double angleY, final double length, final double x, final double y, final double z, final float delta) {
        this.cordModel.rotateAngleX = (float) angleX;
        this.cordModel.rotateAngleY = (float) angleY;
        this.cordModel.scaleZ = (float) length;
        this.cordModel.setRotationPoint(x, y, z);
        this.cordModel.render(0.0625F);
    }
}
