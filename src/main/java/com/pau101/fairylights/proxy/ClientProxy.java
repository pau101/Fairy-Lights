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

import com.pau101.fairylights.FairyLights;
import com.pau101.fairylights.block.BlockFairyLightsFastener;
import com.pau101.fairylights.client.renderer.FairyLightsRenderer;
import com.pau101.fairylights.client.renderer.tileentity.TileEntityFairyLightsFastenerRenderer;
import com.pau101.fairylights.tileentity.TileEntityFairyLightsFastener;
import com.pau101.fairylights.tileentity.connection.Connection;
import com.pau101.fairylights.util.vectormath.Point3f;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.relauncher.ReflectionHelper;

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
	public ItemStack getFairyLightsFastenerPickBlock(MovingObjectPosition target, World world, int x, int y, int z, BlockFairyLightsFastener block) {
		ItemStack itemStack = new ItemStack(block.getItem(world, x, y, z));
		NBTTagCompound tagCompound = new NBTTagCompound();
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		double playerX = Minecraft.getMinecraft().thePlayer.posX, playerY = Minecraft.getMinecraft().thePlayer.posY, playerZ = Minecraft.getMinecraft().thePlayer.posZ;
		if (tileEntity instanceof TileEntityFairyLightsFastener) {
			TileEntityFairyLightsFastener lightsFastener = (TileEntityFairyLightsFastener) tileEntity;
			Connection closetConnection = null;
			float smallestDistance = Float.MAX_VALUE;
			for (Connection connection : lightsFastener.getConnections()) {
				Point3f to = connection.getTo();
				if (to == null) {
					continue;
				}
				float tx = to.x, ty = to.y, tz = to.z;
				Point3f offset = ((BlockFairyLightsFastener) lightsFastener.getBlockType()).getOffsetForData(lightsFastener.getBlockMetadata(), 0.125F);
				float dist = (float) Math.abs((offset.x + tx) * (offset.x + tx) - playerX * playerX + (offset.y + ty) * (offset.y + ty) - playerY * playerY
					+ (offset.z + tz) * (offset.z + tz) - playerZ * playerZ);
				if (dist < smallestDistance) {
					smallestDistance = dist;
					closetConnection = connection;
				}
			}
			if (closetConnection != null) {
				closetConnection.writeDetailsToNBT(tagCompound);
			}
		}
		itemStack.setTagCompound(tagCompound);
		return itemStack;
	}

	@Override
	public void initRenders() {
		MinecraftForge.EVENT_BUS.register(new FairyLightsRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFairyLightsFastener.class, new TileEntityFairyLightsFastenerRenderer());
//		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(FairyLights.fairyLightsFastener), new ItemRendererFairyLights());
	}
}
