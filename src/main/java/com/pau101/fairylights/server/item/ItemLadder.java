package com.pau101.fairylights.server.item;

import com.pau101.fairylights.FairyLights;
import com.pau101.fairylights.server.entity.EntityLadder;
import com.pau101.fairylights.server.sound.FLSounds;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemLadder extends Item {
	public ItemLadder() {
		setCreativeTab(FairyLights.fairyLightsTab);
	}

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		boolean replaceable = world.getBlockState(pos).getBlock().isReplaceable(world, pos);
		if (!replaceable && side != EnumFacing.UP) {
			return EnumActionResult.FAIL;
		}
		BlockPos location = replaceable ? pos : pos.offset(side);
		ItemStack heldStack = player.getHeldItem(hand);
		if (player.canPlayerEdit(location, side, heldStack)) {
			EntityLadder ladder = new EntityLadder(world);
			ladder.rotationYawHead = ladder.renderYawOffset = ladder.rotationYaw = player.rotationYaw + 180;
			float dx, dz;
			if (replaceable && side != EnumFacing.UP) {
				dx = dz = 0.5F;
			} else {
				dx = hitX;
				dz = hitZ;
			}
			ladder.setPosition(location.getX() + dx, location.getY() + 0.001, location.getZ() + dz);
			if (world.getCollisionBoxes(ladder, ladder.getEntityBoundingBox()).size() > 0) {
				return EnumActionResult.FAIL;
			}
			if (!world.isRemote) {
				ItemMonsterPlacer.applyItemEntityDataToEntity(world, player, heldStack, ladder);
				world.spawnEntityInWorld(ladder);
				world.playSound(null, ladder.posX, ladder.posY, ladder.posZ, FLSounds.LADDER_PLACE, ladder.getSoundCategory(), 0.75F, 0.8F);
			}
			heldStack.stackSize--;
			return EnumActionResult.SUCCESS;
		}
		return EnumActionResult.FAIL;
	}
}
