package com.pau101.fairylights.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Timer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import com.pau101.fairylights.block.BlockConnectionFastener;
import com.pau101.fairylights.client.renderer.ConnectionRenderer;
import com.pau101.fairylights.tileentity.TileEntityConnectionFastener;
import com.pau101.fairylights.tileentity.connection.Connection;
import com.pau101.fairylights.util.vectormath.Point3f;

import cpw.mods.fml.client.registry.ClientRegistry;

public class ClientProxy extends CommonProxy {
	public static Timer mcTimer;

	@Override
	public float getCatenaryOffset(EntityPlayer player) {
		if (player == Minecraft.getMinecraft().thePlayer) {
			return -player.height * 0.4F;
		} else {
			return player.height - player.height * 0.4F;
		}
	}

	@Override
	public ItemStack getFairyLightsFastenerPickBlock(MovingObjectPosition target, World world, int x, int y, int z, BlockConnectionFastener block) {
		ItemStack itemStack = new ItemStack(block.getItem(world, x, y, z));
		NBTTagCompound tagCompound = new NBTTagCompound();
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		double playerX = Minecraft.getMinecraft().thePlayer.posX, playerY = Minecraft.getMinecraft().thePlayer.posY, playerZ = Minecraft.getMinecraft().thePlayer.posZ;
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
				Point3f offset = ((BlockConnectionFastener) lightsFastener.getBlockType()).getOffsetForData(lightsFastener.getBlockMetadata(), 0.125F);
				float dist = (float) Math.abs((offset.x + tx) * (offset.x + tx) - playerX * playerX + (offset.y + ty) * (offset.y + ty) - playerY * playerY + (offset.z + tz) * (offset.z + tz) - playerZ * playerZ);
				if (dist < smallestDistance) {
					smallestDistance = dist;
					closetConnection = connection;
				}
			}
			if (closetConnection != null) {
				closetConnection.writeDetailsToNBT(tagCompound);
				itemStack.func_150996_a(closetConnection.getType().getItem());
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
//		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityConnectionFastener.class, new TileEntityFairyLightsFastenerRenderer());
	}
}
