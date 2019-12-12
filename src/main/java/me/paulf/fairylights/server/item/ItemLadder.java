package me.paulf.fairylights.server.item;

import me.paulf.fairylights.server.entity.EntityLadder;
import me.paulf.fairylights.server.entity.FLEntities;
import me.paulf.fairylights.server.sound.FLSounds;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemLadder extends Item {
	public ItemLadder(Item.Properties properties) {
		super(properties);
	}

	@Override
	public ActionResultType onItemUse(final ItemUseContext context) {
		World world = context.getWorld();
		BlockPos pos = context.getPos();
		Direction side = context.getFace();
		BlockState state = world.getBlockState(pos);
		PlayerEntity player = context.getPlayer();
		boolean replaceable = state.isReplaceable(new BlockItemUseContext(context));
		if (!replaceable && side != Direction.UP) {
			return ActionResultType.FAIL;
		}
		BlockPos location = replaceable ? pos : pos.offset(side);
		ItemStack heldStack = context.getItem();
		double x, z;
		if (replaceable && side != Direction.UP) {
			x = location.getX() + 0.5D;
			z = location.getZ() + 0.5D;
		} else {
			x = context.getHitVec().x;
			z = context.getHitVec().z;
		}
		double y = location.getY() + 0.001;
		if (!world.areCollisionShapesEmpty(FLEntities.LADDER.orElseThrow(IllegalStateException::new).func_220328_a(x, y, z))) {
			return ActionResultType.FAIL;
		}
		if (!world.isRemote) {
			EntityLadder ladder = new EntityLadder(world);
			ladder.rotationYawHead = ladder.renderYawOffset = ladder.rotationYaw = context.getPlacementYaw() + 180;
			ladder.setPosition(x, y, z);
			EntityType.applyItemNBT(world, player, ladder, heldStack.getTag());
			world.addEntity(ladder);
			world.playSound(null, ladder.posX, ladder.posY, ladder.posZ, FLSounds.LADDER_PLACE.orElseThrow(IllegalStateException::new), ladder.getSoundCategory(), 0.75F, 0.8F);
		}
		heldStack.shrink(1);
		return ActionResultType.SUCCESS;
	}
}
