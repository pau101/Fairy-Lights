package com.pau101.fairylights.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import com.pau101.fairylights.FairyLights;
import com.pau101.fairylights.block.BlockConnectionFastener;
import com.pau101.fairylights.block.BlockConnectionFastenerFence;
import com.pau101.fairylights.client.renderer.ConnectionRenderer;
import com.pau101.fairylights.item.LightVariant;
import com.pau101.fairylights.tileentity.TileEntityConnectionFastener;
import com.pau101.fairylights.tileentity.connection.Connection;
import com.pau101.fairylights.util.vectormath.Point3f;

public class ClientProxy extends CommonProxy {
	@Override
	public ItemStack getConnectionFastenerPickBlock(MovingObjectPosition target, World world, BlockPos pos, BlockConnectionFastener block) {
		ItemStack itemStack = new ItemStack(block.getItem(world, pos));
		NBTTagCompound tagCompound = new NBTTagCompound();
		TileEntity tileEntity = world.getTileEntity(pos);
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		double playerX = player.posX, playerY = player.posY, playerZ = player.posZ;
		if (tileEntity instanceof TileEntityConnectionFastener) {
			TileEntityConnectionFastener lightsFastener = (TileEntityConnectionFastener) tileEntity;
			Connection closetConnection = null;
			float smallestDistance = Float.MAX_VALUE;
			for (Connection connection : lightsFastener.getConnections()) {
				Point3f to = connection.getTo();
				if (to == null) {
					continue;
				}
				float tx = to.x, ty = to.y, tz = to.z;
				Point3f offset = ((BlockConnectionFastener) lightsFastener.getBlockType()).getOffsetForData(lightsFastener.getBlockType() instanceof BlockConnectionFastenerFence ? null : (EnumFacing) world.getBlockState(pos).getValue(BlockConnectionFastener.FACING_PROP), 0.125F);
				float dist = (float) Math.abs((offset.x + tx) * (offset.x + tx) - playerX * playerX + (offset.y + ty) * (offset.y + ty) - playerY * playerY + (offset.z + tz) * (offset.z + tz) - playerZ * playerZ);
				if (dist < smallestDistance) {
					smallestDistance = dist;
					closetConnection = connection;
				}
			}
			if (closetConnection != null) {
				closetConnection.writeDetailsToNBT(tagCompound);
				itemStack.setItem(closetConnection.getType().getItem());
			}
		}
		if (!tagCompound.hasNoTags()) {
			itemStack.setTagCompound(tagCompound);
		}
		return itemStack;
	}

	@Override
	public void initRenders() {
		MinecraftForge.EVENT_BUS.register(new ConnectionRenderer());
		ItemModelMesher itemModelMesh = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
		LightVariant[] lightVariants = LightVariant.values();
		String[] lightVaraintNames = new String[lightVariants.length];
		for (int meta = 0; meta < lightVariants.length; meta++) {
			LightVariant variant = lightVariants[meta];
			itemModelMesh.register(FairyLights.light, meta, new ModelResourceLocation(FairyLights.MODID + ":" + variant.getName(), "inventory"));
			lightVaraintNames[meta] = FairyLights.MODID + ':' + variant.getName();
		}
		ModelBakery.addVariantName(FairyLights.light, lightVaraintNames);
		itemModelMesh.register(FairyLights.fairyLights, 0, new ModelResourceLocation(FairyLights.MODID + ":fairy_lights", "inventory"));
		itemModelMesh.register(FairyLights.garland, 0, new ModelResourceLocation(FairyLights.MODID + ":garland", "inventory"));
		ModelResourceLocation tinselModel = new ModelResourceLocation(FairyLights.MODID + ":tinsel", "inventory");
		for (int color = 0, max = EnumDyeColor.values().length; color < max; color++) {
			itemModelMesh.register(FairyLights.tinsel, color, tinselModel);
		}
		itemModelMesh.register(Item.getItemFromBlock(FairyLights.connectionFastener), 0, new ModelResourceLocation(FairyLights.MODID + ":fairy_lights_fastener", "inventory"));
	}
}
