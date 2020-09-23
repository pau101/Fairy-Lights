package com.pau101.fairylights.server.integration.valkyrienskies;

import com.pau101.fairylights.server.fastener.BlockView;
import com.pau101.fairylights.server.fastener.CollectFastenersEvent;
import com.pau101.fairylights.server.fastener.CreateBlockViewEvent;
import com.pau101.fairylights.util.matrix.Matrix;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.joml.AxisAngle4d;
import org.joml.Quaterniond;
import org.valkyrienskies.mod.common.ships.chunk_claims.VSChunkClaim;
import org.valkyrienskies.mod.common.ships.ship_world.IHasShipManager;
import org.valkyrienskies.mod.common.ships.ship_world.PhysicsObject;
import org.valkyrienskies.mod.common.ships.ship_world.WorldClientShipManager;
import valkyrienwarfare.api.IPhysicsEntity;
import valkyrienwarfare.api.IPhysicsEntityManager;
import valkyrienwarfare.api.TransformType;

public class ValkyrienSkies {
	@SubscribeEvent
	public static void onCollectFasteners(final CollectFastenersEvent event) {
        WorldClientShipManager manager = ((WorldClientShipManager) ((IHasShipManager) event.getWorld()).getManager());
        manager.getAllLoadedPhysObj()
			.forEach(obj -> {
				VSChunkClaim claim = obj.getChunkClaim();
				claim.forEach((x, z) -> event.accept(obj.getChunkAt(x, z)));
			});

	}

	@SubscribeEvent
	public static void onCreateBlockView(final CreateBlockViewEvent event) {
		event.setView(new ValkyrienSkies.VSView(event.getView()));
	}

	private static class VSView implements BlockView {
		private final BlockView parent;

		private VSView(final BlockView parent) {
			this.parent = parent;
		}

		@Override
		public boolean isMoving(final World world, final BlockPos source) {
			return IPhysicsEntityManager.INSTANCE.isBlockPosManagedByPhysicsEntity(world, source) || this.parent.isMoving(world, source);
		}

		@Override
		public Vec3d getPosition(final World world, final BlockPos source, final Vec3d pos) {
			final IPhysicsEntity entity = IPhysicsEntityManager.INSTANCE.getPhysicsEntityFromShipSpace(world, source);
			if (entity != null) {
				return entity.transformVector(pos, TransformType.SUBSPACE_TO_GLOBAL);
			}
			return this.parent.getPosition(world, source, pos);
		}

		@Override
		public void unrotate(final World world, final BlockPos source, final Matrix matrix, final float delta) {
			this.parent.unrotate(world, source, matrix, delta);
			final PhysicsObject entity = (PhysicsObject) IPhysicsEntityManager.INSTANCE.getPhysicsEntityFromShipSpace(world, source);
			if (entity != null) {
				final Quaterniond rot = getRotation(entity, delta);
                AxisAngle4d rot2 = rot.get(new AxisAngle4d());
				matrix.rotate((float) Math.toDegrees(rot2.angle), (float) rot2.x, (float) rot2.y, (float) rot2.z);
			}
		}

		private Quaterniond getRotation(final PhysicsObject object, final float delta) {
			final Quaterniond prevRotation = object.getShipTransformationManager().getPrevTickTransform().rotationQuaternion(TransformType.GLOBAL_TO_SUBSPACE);
			final Quaterniond rotation = object.getShipTransformationManager().getCurrentTickTransform().rotationQuaternion(TransformType.GLOBAL_TO_SUBSPACE);
			return prevRotation.slerp(rotation, delta);
		}
	}
}
