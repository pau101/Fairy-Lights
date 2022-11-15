package me.paulf.fairylights.client.model.light;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;

public class BowModel extends Model {
    private final ModelPart root;

    public BowModel(ModelPart root) {
        super(RenderType::entityCutout);
        this.root = root;
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot().addOrReplaceChild("root", CubeListBuilder.create()
            .texOffs(6, 72)
            .addBox(-2.0F, -1.5F, -1.0F, 4.0F, 3.0F, 2.0F), PartPose.offset(0.0F, 0.5F, -3.25F));
        root.addOrReplaceChild("bone", CubeListBuilder.create()
            .texOffs(0, 77)
            .addBox(-5.0F, -4.0F, 0.0F, 5.0F, 5.0F, 1.0F), PartPose.offsetAndRotation(-1.0F, 1.0F, 0.0F, 0.0F, 0.1745F, -0.5236F));
        root.addOrReplaceChild("bone2", CubeListBuilder.create()
            .texOffs(0, 77)
            .addBox(0.0F, -4.0F, 0.0F, 5.0F, 5.0F, 1.0F), PartPose.offsetAndRotation(1.0F, 1.0F, 0.0F, 0.0F, -0.1745F, 0.5236F));
        root.addOrReplaceChild("bone3", CubeListBuilder.create()
            .texOffs(0, 72)
            .addBox(-2.0F, -4.0F, 0.0F, 2.0F, 4.0F, 1.0F), PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, 0.0873F, 0.0873F, -0.1745F));
        root.addOrReplaceChild("bone4", CubeListBuilder.create()
            .texOffs(0, 72)
            .addBox(0.0F, -4.0F, 0.0F, 2.0F, 4.0F, 1.0F), PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, 0.0873F, -0.0873F, 0.1745F));
        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public void renderToBuffer(final PoseStack matrix, final VertexConsumer builder, final int light, final int overlay, final float r, final float g, final float b, final float a) {
        this.root.render(matrix, builder, light, overlay, r, g, b, a);
    }
}
