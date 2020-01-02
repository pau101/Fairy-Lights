package me.paulf.fairylights.client.model;

import com.mojang.blaze3d.platform.GlStateManager;
import me.paulf.fairylights.util.Mth;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelBox;
import org.lwjgl.opengl.GL11;

import java.util.List;

public final class AdvancedRendererModel extends RendererModel {
	protected int textureOffsetX;

	protected int textureOffsetY;

	public boolean compiled;

	public int displayList = -1;

	protected Model modelBase;

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

	public float glowExpandAmount = 0.7F;

	public AdvancedRendererModel(Model modelBase) {
		this(modelBase, null);
	}

	public AdvancedRendererModel(Model modelBase, int textureOffsetX, int textureOffsetY) {
		this(modelBase);
		setTextureOffset(textureOffsetX, textureOffsetY);
	}

	public AdvancedRendererModel(Model modelBase, String name) {
		super(modelBase, name);
		this.modelBase = modelBase;
		setTextureSize(modelBase.textureWidth, modelBase.textureHeight);
		scaleX = scaleY = scaleZ = 1;
		rotationOrder = RotationOrder.ZYX;
		secondaryRotationOrder = RotationOrder.ZYX;
	}

	@Override
	public AdvancedRendererModel addBox(float posX, float posY, float posZ, int width, int height, int depth) {
		cubeList.add(new ModelBox(this, textureOffsetX, textureOffsetY, posX, posY, posZ, width, height, depth, 0));
		return this;
	}

	@Override
	public void addBox(float posX, float posY, float posZ, int width, int height, int depth, float scale) {
		cubeList.add(new ModelBox(this, textureOffsetX, textureOffsetY, posX, posY, posZ, width, height, depth, scale));
	}

	@Override
	public RendererModel addBox(final float offX, final float offY, final float offZ, final int width, final int height, final int depth, final boolean mirrored) {
		return super.addBox(offX, offY, offZ, width, height, depth, mirrored);
	}

	@Override
	public RendererModel func_217178_a(String name, float posX, float posY, float posZ, int width, int height, int depth, float expand, int u, int v) {
		name = boxName + "." + name;
		setTextureOffset(u, v);
		cubeList.add(new ModelBox(this, textureOffsetX, textureOffsetY, posX, posY, posZ, width, height, depth, expand).setBoxName(name));
		return this;
	}

	public AdvancedRendererModel add3DTexture(float posX, float posY, float posZ, int width, int height) {
		cubeList.add(new Model3DTexture(this, textureOffsetX, textureOffsetY, posX, posY, posZ, width, height));
		return this;
	}

	public void addMeteorLightBox(float posX, float posY, float posZ, int width, int height, int depth, int type) {
		cubeList.add(new MeteorLightBoxModel(this, textureOffsetX, textureOffsetY, posX, posY, posZ, width, height, depth, type));
	}

	protected void compileDisplayList(float scale) {
		displayList = GLAllocation.generateDisplayLists(1);
		GlStateManager.newList(displayList, GL11.GL_COMPILE);
		BufferBuilder buf = Tessellator.getInstance().getBuffer();
		for (ModelBox box : cubeList) {
			box.render(buf, scale);
		}
		GlStateManager.endList();
		compiled = true;
	}

	@Override
	public void postRender(float scale) {
		if (!isHidden && showModel) {
			if (!compiled) {
				compileDisplayList(scale);
			}
			GlStateManager.translatef(rotationPointX * scale, rotationPointY * scale, rotationPointZ * scale);
			rotationOrder.rotate(rotateAngleX * Mth.RAD_TO_DEG, rotateAngleY * Mth.RAD_TO_DEG, rotateAngleZ * Mth.RAD_TO_DEG);
			secondaryRotationOrder.rotate(secondaryRotateAngleX * Mth.RAD_TO_DEG, secondaryRotateAngleY * Mth.RAD_TO_DEG, secondaryRotateAngleZ * Mth.RAD_TO_DEG);
			GlStateManager.translatef(aftMoveX, aftMoveY, aftMoveZ);
			GlStateManager.scalef(scaleX, scaleY, scaleZ);
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
					GlStateManager.translatef((box.posX1 - glowExpandAmount - scaleX * box.posX1) / 16, (box.posY1 - (isMeteorLightGlow ? meteorExpandY : glowExpandAmount) - scaleY * box.posY1) / 16, (box.posZ1 - glowExpandAmount - scaleZ * box.posZ1) / 16 * (box instanceof Model3DTexture ? -1 : 1));
					GlStateManager.scalef(scaleX, scaleY, scaleZ);
					box.render(buf, scale);
					GlStateManager.popMatrix();
				}
			}
		} else {
			GlStateManager.callList(displayList);
		}

		if (childModels != null) {
			for (int i = 0; i < childModels.size(); i++) {
				RendererModel modelRenderer = childModels.get(i);
				if (modelRenderer instanceof AdvancedRendererModel) {
					((AdvancedRendererModel) modelRenderer).isGlowing = isGlowing;
				}
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
			GlStateManager.translatef(offsetX, offsetY, offsetZ);
			if (rotateAngleX == 0 && rotateAngleY == 0 && rotateAngleZ == 0) {
				if (rotationPointX == 0 && rotationPointY == 0 && rotationPointZ == 0) {
					if (scaleX == 1 && scaleY == 1 && scaleZ == 1) {
						GlStateManager.pushMatrix();
						secondaryRotationOrder.rotate(secondaryRotateAngleX * Mth.RAD_TO_DEG, secondaryRotateAngleY * Mth.RAD_TO_DEG, secondaryRotateAngleZ * Mth.RAD_TO_DEG);
						GlStateManager.translatef(aftMoveX, aftMoveY, aftMoveZ);
						baseRender(scale);
						GlStateManager.popMatrix();
					} else {
						GlStateManager.pushMatrix();
						secondaryRotationOrder.rotate(secondaryRotateAngleX * Mth.RAD_TO_DEG, secondaryRotateAngleY * Mth.RAD_TO_DEG, secondaryRotateAngleZ * Mth.RAD_TO_DEG);
						GlStateManager.translatef(aftMoveX, aftMoveY, aftMoveZ);
						GlStateManager.scalef(scaleX, scaleY, scaleZ);
						baseRender(scale);
						GlStateManager.popMatrix();
					}
				} else {
					GlStateManager.pushMatrix();
					GlStateManager.translatef(rotationPointX * scale, rotationPointY * scale, rotationPointZ * scale);
					secondaryRotationOrder.rotate(secondaryRotateAngleX * Mth.RAD_TO_DEG, secondaryRotateAngleY * Mth.RAD_TO_DEG, secondaryRotateAngleZ * Mth.RAD_TO_DEG);
					GlStateManager.translatef(aftMoveX, aftMoveY, aftMoveZ);
					GlStateManager.scalef(scaleX, scaleY, scaleZ);
					baseRender(scale);
					GlStateManager.popMatrix();
				}
			} else {
				GlStateManager.pushMatrix();
				GlStateManager.translatef(rotationPointX * scale, rotationPointY * scale, rotationPointZ * scale);
				rotationOrder.rotate(rotateAngleX * Mth.RAD_TO_DEG, rotateAngleY * Mth.RAD_TO_DEG, rotateAngleZ * Mth.RAD_TO_DEG);
				secondaryRotationOrder.rotate(secondaryRotateAngleX * Mth.RAD_TO_DEG, secondaryRotateAngleY * Mth.RAD_TO_DEG, secondaryRotateAngleZ * Mth.RAD_TO_DEG);
				GlStateManager.translatef(aftMoveX, aftMoveY, aftMoveZ);
				GlStateManager.scalef(scaleX, scaleY, scaleZ);
				baseRender(scale);
				GlStateManager.popMatrix();
			}
			GlStateManager.translatef(-offsetX, -offsetY, -offsetZ);
		}
	}

	@Override
	public void renderWithRotation(float scale) {
		if (!isHidden && showModel) {
			if (!compiled) {
				compileDisplayList(scale);
			}
			GlStateManager.pushMatrix();
			GlStateManager.translatef(rotationPointX * scale, rotationPointY * scale, rotationPointZ * scale);
			rotationOrder.rotate(rotateAngleX * Mth.RAD_TO_DEG, rotateAngleY * Mth.RAD_TO_DEG, rotateAngleZ * Mth.RAD_TO_DEG);
			secondaryRotationOrder.rotate(secondaryRotateAngleX * Mth.RAD_TO_DEG, secondaryRotateAngleY * Mth.RAD_TO_DEG, secondaryRotateAngleZ * Mth.RAD_TO_DEG);
			GlStateManager.scalef(scaleX, scaleY, scaleZ);
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
	public AdvancedRendererModel setTextureOffset(int textureOffsetX, int textureOffsetY) {
		this.textureOffsetX = textureOffsetX;
		this.textureOffsetY = textureOffsetY;
		return this;
	}

	@Override
	public AdvancedRendererModel setTextureSize(int textureWidth, int textureHeight) {
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
