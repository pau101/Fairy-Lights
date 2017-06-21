package com.pau101.fairylights.client.model;

import com.pau101.fairylights.util.Mth;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.TextureOffset;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public final class AdvancedModelRenderer extends ModelRenderer {
	protected int textureOffsetX;

	protected int textureOffsetY;

	public boolean compiled;

	public int displayList = -1;

	protected ModelBase modelBase;

	public float scaleX;

	public float scaleY;

	public float scaleZ;

	public float aftMoveX;

	public float aftMoveY;

	public float aftMoveZ;

	protected RotationOrder rotationOrder;

	public float secondaryRotateAngleX;

	public float secondaryRotateAngleY;

	public float secondaryRotateAngleZ;

	protected RotationOrder secondaryRotationOrder;

	public boolean isGlowing;

	public boolean shouldntGlow;

	public boolean isMeteorLightGlow;

	@Nullable
	public List<AdvancedModelRenderer> childModels;

	public float glowExpandAmount = 0.7F;

	public AdvancedModelRenderer(ModelBase modelBase) {
		this(modelBase, null);
	}

	public AdvancedModelRenderer(ModelBase modelBase, int textureOffsetX, int textureOffsetY) {
		this(modelBase);
		setTextureOffset(textureOffsetX, textureOffsetY);
	}

	public AdvancedModelRenderer(ModelBase modelBase, String name) {
		super(modelBase, name);
		this.modelBase = modelBase;
		setTextureSize(modelBase.textureWidth, modelBase.textureHeight);
		scaleX = scaleY = scaleZ = 1;
		rotationOrder = RotationOrder.ZYX;
		secondaryRotationOrder = RotationOrder.ZYX;
	}

	public void addChild(AdvancedModelRenderer modelRenderer) {
		if (childModels == null) {
			childModels = new ArrayList<>();
		}
		childModels.add(modelRenderer);
	}

	@Override
	public AdvancedModelRenderer addBox(float posX, float posY, float posZ, int width, int height, int depth) {
		cubeList.add(new ModelBox(this, textureOffsetX, textureOffsetY, posX, posY, posZ, width, height, depth, 0));
		return this;
	}

	@Override
	public void addBox(float posX, float posY, float posZ, int width, int height, int depth, float scale) {
		cubeList.add(new ModelBox(this, textureOffsetX, textureOffsetY, posX, posY, posZ, width, height, depth, scale));
	}

	@Override
	public AdvancedModelRenderer addBox(String name, float posX, float posY, float posZ, int width, int height, int depth) {
		name = boxName + "." + name;
		TextureOffset textureoffset = modelBase.getTextureOffset(name);
		setTextureOffset(textureoffset.textureOffsetX, textureoffset.textureOffsetY);
		cubeList.add(new ModelBox(this, textureOffsetX, textureOffsetY, posX, posY, posZ, width, height, depth, 0.0F).setBoxName(name));
		return this;
	}

	public AdvancedModelRenderer add3DTexture(float posX, float posY, float posZ, int width, int height) {
		cubeList.add(new Model3DTexture(this, textureOffsetX, textureOffsetY, posX, posY, posZ, width, height));
		return this;
	}

	public void addMeteorLightBox(float posX, float posY, float posZ, int width, int height, int depth, int type) {
		cubeList.add(new ModelMeteorLightBox(this, textureOffsetX, textureOffsetY, posX, posY, posZ, width, height, depth, type));
	}

	protected void compileDisplayList(float scale) {
		displayList = GLAllocation.generateDisplayLists(1);
		GlStateManager.glNewList(displayList, GL11.GL_COMPILE);
		BufferBuilder buf = Tessellator.getInstance().getBuffer();
		for (ModelBox box : cubeList) {
			box.render(buf, scale);
		}
		GlStateManager.glEndList();
		compiled = true;
	}

	@Override
	public void postRender(float scale) {
		if (!isHidden && showModel) {
			if (!compiled) {
				compileDisplayList(scale);
			}
			GlStateManager.translate(rotationPointX * scale, rotationPointY * scale, rotationPointZ * scale);
			rotationOrder.rotate(rotateAngleX * Mth.RAD_TO_DEG, rotateAngleY * Mth.RAD_TO_DEG, rotateAngleZ * Mth.RAD_TO_DEG);
			secondaryRotationOrder.rotate(secondaryRotateAngleX * Mth.RAD_TO_DEG, secondaryRotateAngleY * Mth.RAD_TO_DEG, secondaryRotateAngleZ * Mth.RAD_TO_DEG);
			GlStateManager.translate(aftMoveX, aftMoveY, aftMoveZ);
			GlStateManager.scale(scaleX, scaleY, scaleZ);
		}
	}

	private void baseRender(float scale) {
		if (isGlowing && shouldntGlow) {
			return;
		}
		if (isGlowing) {
			List<ModelBox> boxModels = cubeList;
			BufferBuilder buf = Tessellator.getInstance().getBuffer();
			for (ModelBox box : boxModels) {
				int bri = 1;
				float meteorExpandY = 0;
				for (int i = 0; i < bri; i++) {
					float width = box.posX2 - box.posX1, height = box.posY2 - box.posY1, depth = box.posZ2 - box.posZ1;
					float localExpand = glowExpandAmount * (i + 1);
					float localMeteorExpandY = meteorExpandY * (i + 1); 
					float newWidth = width + 2 * localExpand;
					float newHeight = height + 2 * (isMeteorLightGlow ? localMeteorExpandY : localExpand);
					float newDepth = depth + 2 * localExpand;
					float scaleX = newWidth / width;
					float scaleY = newHeight / height;
					float scaleZ = newDepth / depth;
					GlStateManager.pushMatrix();
					GlStateManager.translate((box.posX1 - glowExpandAmount - scaleX * box.posX1) / 16, (box.posY1 - (isMeteorLightGlow ? meteorExpandY : glowExpandAmount) - scaleY * box.posY1) / 16, (box.posZ1 - glowExpandAmount - scaleZ * box.posZ1) / 16 * (box instanceof Model3DTexture ? -1 : 1));
					GlStateManager.scale(scaleX, scaleY, scaleZ);
					box.render(buf, scale);
					GlStateManager.popMatrix();
				}
			}
		} else {
			GlStateManager.callList(displayList);
		}

		if (childModels != null) {
			for (int i = 0; i < childModels.size(); i++) {
				AdvancedModelRenderer modelRenderer = childModels.get(i);
				modelRenderer.isGlowing = isGlowing;
				modelRenderer.render(scale);
			}
		}
	}

	@Override
	public void render(float scale) {
		if (!isHidden && showModel) {
			if (!compiled) {
				compileDisplayList(scale);
			}
			GlStateManager.translate(offsetX, offsetY, offsetZ);
			if (rotateAngleX == 0 && rotateAngleY == 0 && rotateAngleZ == 0) {
				if (rotationPointX == 0 && rotationPointY == 0 && rotationPointZ == 0) {
					if (scaleX == 1 && scaleY == 1 && scaleZ == 1) {
						GlStateManager.pushMatrix();
						secondaryRotationOrder.rotate(secondaryRotateAngleX * Mth.RAD_TO_DEG, secondaryRotateAngleY * Mth.RAD_TO_DEG, secondaryRotateAngleZ * Mth.RAD_TO_DEG);
						GlStateManager.translate(aftMoveX, aftMoveY, aftMoveZ);
						baseRender(scale);
						GlStateManager.popMatrix();
					} else {
						GlStateManager.pushMatrix();
						secondaryRotationOrder.rotate(secondaryRotateAngleX * Mth.RAD_TO_DEG, secondaryRotateAngleY * Mth.RAD_TO_DEG, secondaryRotateAngleZ * Mth.RAD_TO_DEG);
						GlStateManager.translate(aftMoveX, aftMoveY, aftMoveZ);
						GlStateManager.scale(scaleX, scaleY, scaleZ);
						baseRender(scale);
						GlStateManager.popMatrix();
					}
				} else {
					GlStateManager.pushMatrix();
					GlStateManager.translate(rotationPointX * scale, rotationPointY * scale, rotationPointZ * scale);
					secondaryRotationOrder.rotate(secondaryRotateAngleX * Mth.RAD_TO_DEG, secondaryRotateAngleY * Mth.RAD_TO_DEG, secondaryRotateAngleZ * Mth.RAD_TO_DEG);
					GlStateManager.translate(aftMoveX, aftMoveY, aftMoveZ);
					GlStateManager.scale(scaleX, scaleY, scaleZ);
					baseRender(scale);
					GlStateManager.popMatrix();
				}
			} else {
				GlStateManager.pushMatrix();
				GlStateManager.translate(rotationPointX * scale, rotationPointY * scale, rotationPointZ * scale);
				rotationOrder.rotate(rotateAngleX * Mth.RAD_TO_DEG, rotateAngleY * Mth.RAD_TO_DEG, rotateAngleZ * Mth.RAD_TO_DEG);
				secondaryRotationOrder.rotate(secondaryRotateAngleX * Mth.RAD_TO_DEG, secondaryRotateAngleY * Mth.RAD_TO_DEG, secondaryRotateAngleZ * Mth.RAD_TO_DEG);
				GlStateManager.translate(aftMoveX, aftMoveY, aftMoveZ);
				GlStateManager.scale(scaleX, scaleY, scaleZ);
				baseRender(scale);
				GlStateManager.popMatrix();
			}
			GlStateManager.translate(-offsetX, -offsetY, -offsetZ);
		}
	}

	@Override
	public void renderWithRotation(float scale) {
		if (!isHidden && showModel) {
			if (!compiled) {
				compileDisplayList(scale);
			}
			GlStateManager.pushMatrix();
			GlStateManager.translate(rotationPointX * scale, rotationPointY * scale, rotationPointZ * scale);
			rotationOrder.rotate(rotateAngleX * Mth.RAD_TO_DEG, rotateAngleY * Mth.RAD_TO_DEG, rotateAngleZ * Mth.RAD_TO_DEG);
			secondaryRotationOrder.rotate(secondaryRotateAngleX * Mth.RAD_TO_DEG, secondaryRotateAngleY * Mth.RAD_TO_DEG, secondaryRotateAngleZ * Mth.RAD_TO_DEG);
			GlStateManager.scale(scaleX, scaleY, scaleZ);
			baseRender(scale);
			GlStateManager.popMatrix();
		}
	}

	public void setRotationPoint(double rotationPointX, double rotationPointY, double rotationPointZ) {
		setRotationPoint((float) rotationPointX, (float) rotationPointY, (float) rotationPointZ);
	}

	public void setRotationAngles(double rotateAngleX, double rotateAngleY, double rotateAngleZ) {
		this.rotateAngleX = (float) rotateAngleX;
		this.rotateAngleY = (float) rotateAngleY;
		this.rotateAngleZ = (float) rotateAngleZ;
	}

	@Override
	public AdvancedModelRenderer setTextureOffset(int textureOffsetX, int textureOffsetY) {
		this.textureOffsetX = textureOffsetX;
		this.textureOffsetY = textureOffsetY;
		return this;
	}

	@Override
	public AdvancedModelRenderer setTextureSize(int textureWidth, int textureHeight) {
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
		return this;
	}

	public void setRotationOrder(RotationOrder rotationOrder) {
		this.rotationOrder = rotationOrder;
	}

	public void setSecondaryRotationOrder(RotationOrder secondaryRotationOrder) {
		this.secondaryRotationOrder = secondaryRotationOrder;
	}
}
