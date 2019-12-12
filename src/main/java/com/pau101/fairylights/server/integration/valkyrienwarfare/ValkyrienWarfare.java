package com.pau101.fairylights.server.integration.valkyrienwarfare;

public class ValkyrienWarfare {
	/*@SubscribeEvent
	public static void onCollectFasteners(final CollectFastenersEvent event) {
		event.getWorld().loadedEntityList.stream()
			.filter(PhysicsWrapperEntity.class::isInstance)
			.map(PhysicsWrapperEntity.class::cast)
			.map(PhysicsWrapperEntity::getPhysicsObject)
			.forEach(obj -> {
				int minX = obj.ownedChunks.getMinX();
				int maxX = obj.ownedChunks.getMaxX();
				int minZ = obj.ownedChunks.getMinZ();
				int maxZ = obj.ownedChunks.getMaxZ();
				for (int x = minX; x < maxX; x++) {
					for (int z = minZ; z < maxZ; z++) {
						event.accept(obj.shipChunks.getChunkAt(x, z));
					}
				}
			});

	}

	@SubscribeEvent
	public static void onCreateBlockView(final CreateBlockViewEvent event) {
		event.setView(new ValkyrienWarfare.VWView(event.getView()));
	}

	private static class VWView implements BlockView {
		private final BlockView parent;

		private VWView(final BlockView parent) {
			this.parent = parent;
		}

		@Override
		public boolean isMoving(final World world, final BlockPos source) {
			return new RealMethods().isBlockPartOfShip(world, source) || this.parent.isMoving(world, source);
		}

		@Override
		public Vec3d getPosition(final World world, final BlockPos source, final Vec3d pos) {
			final PhysicsWrapperEntity entity = new RealMethods().getShipEntityManagingPos(world, source);
			if (entity != null) {
				final Vector p = new Vector(pos);
				entity.getPhysicsObject().coordTransform.fromLocalToGlobal(p);
				return p.toVec3d();
			}
			return this.parent.getPosition(world, source, pos);
		}

		@Override
		public void unrotate(final World world, final BlockPos source, final Matrix matrix, final float delta) {
			this.parent.unrotate(world, source, matrix, delta);
			final PhysicsWrapperEntity entity = new RealMethods().getShipEntityManagingPos(world, source);
			if (entity != null) {
				final Quaternion rot = getRotation(entity.getPhysicsObject(), delta);
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
			final Quaternion prevRotation = object.coordTransform.getPrevTickTransform().createRotationQuaternion(TransformType.GLOBAL_TO_LOCAL);
			final Quaternion rotation = object.coordTransform.getCurrentTickTransform().createRotationQuaternion(TransformType.GLOBAL_TO_LOCAL);
			return Quaternion.slerpInterpolate(prevRotation, rotation, delta);
		}
	}*/
}
