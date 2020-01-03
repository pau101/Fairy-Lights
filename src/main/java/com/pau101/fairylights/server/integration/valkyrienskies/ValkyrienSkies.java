package com.pau101.fairylights.server.integration.valkyrienskies;

import com.pau101.fairylights.server.fastener.BlockView;
import com.pau101.fairylights.server.fastener.CollectFastenersEvent;
import com.pau101.fairylights.server.fastener.CreateBlockViewEvent;
import com.pau101.fairylights.util.matrix.Matrix;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.valkyrienskies.mod.common.entity.PhysicsWrapperEntity;
import org.valkyrienskies.mod.common.math.Quaternion;
import org.valkyrienskies.mod.common.physics.management.PhysicsObject;
import org.valkyrienskies.mod.common.physmanagement.chunk.VSChunkClaim;
import valkyrienwarfare.api.IPhysicsEntity;
import valkyrienwarfare.api.IPhysicsEntityManager;
import valkyrienwarfare.api.TransformType;

public class ValkyrienSkies {
	@SubscribeEvent
	public static void onCollectFasteners(final CollectFastenersEvent event) {
		event.getWorld().loadedEntityList.stream()
			.filter(PhysicsWrapperEntity.class::isInstance)
			.map(PhysicsWrapperEntity.class::cast)
			.map(PhysicsWrapperEntity::getPhysicsObject)
			.forEach(obj -> {
				VSChunkClaim claim = obj.getOwnedChunks();
				int minX = claim.minX();
				int maxX = claim.maxX();
				int minZ = claim.minZ();
				int maxZ = claim.maxZ();
				for (int x = minX; x < maxX; x++) {
					for (int z = minZ; z < maxZ; z++) {
						event.accept(obj.getChunkAt(x, z));
					}
				}
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
				final Quaternion rot = getRotation(entity, delta);
				final double[] radians = rot.toRadians();
				final float pitch = (float) Math.toDegrees(radians[0]);
				final float yaw = (float) Math.toDegrees(radians[1]);
				final float roll = (float) Math.toDegrees(radians[2]);
				matrix.rotate(pitch, 1.0F, 0.0F, 0.0F);
				matrix.rotate(yaw, 0.0F, 1.0F, 0.0F);
				matrix.rotate(roll, 0.0F, 0.0F, 1.0F);
			}
		}

		private Quaternion getRotation(final PhysicsObject object, final float delta) {
			final Quaternion prevRotation = object.getShipTransformationManager().getPrevTickTransform().createRotationQuaternion(TransformType.GLOBAL_TO_SUBSPACE);
			final Quaternion rotation = object.getShipTransformationManager().getCurrentTickTransform().createRotationQuaternion(TransformType.GLOBAL_TO_SUBSPACE);
			return Quaternion.slerpInterpolate(prevRotation, rotation, delta);
		}
	}
}
