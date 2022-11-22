package me.paulf.fairylights.util;

import net.minecraft.world.phys.Vec3;

import java.util.NoSuchElementException;

public interface Curve {

    public int getCount();

    public float getX();

    public float getY();

    public float getZ();

    public float getX(final int i);

    public float getX(final int i, float lerp);

    public float getY(final int i);

    public float getY(final int i, float lerp);

    public float getZ(final int i);

    public float getZ(final int i, float lerp);

    public float getDx(final int i);

    public float getDy(final int i);

    public float getDz(final int i);

    public float getLength();

    default SegmentIterator iterator() {
        return this.iterator(false);
    }

    public Curve lerp(final Curve other, final float delta);

    default void visitPoints(final float spacing, final boolean center, final PointVisitor visitor) {
        float distance = center ? (this.getLength() % spacing + spacing) / 2.0F : 0;
        int index = 0;
        final SegmentIterator it = this.iterator();
        while (it.next()) {
            final float length = it.getLength();
            while (distance < length) {
                final float t = distance / length;
                visitor.visit(index++, it.getX(t), it.getY(t), it.getZ(t), it.getYaw(), it.getPitch());
                distance += spacing;
            }
            distance -= length;
            if (!center && !it.hasNext()) {
                visitor.visit(index++, it.getX(1.0F), it.getY(1.0F), it.getZ(1.0F), it.getYaw(), it.getPitch());
            }
        }
    }

    SegmentIterator iterator(final boolean inclusive);

    public interface SegmentIterator extends SegmentView {
        boolean hasNext();

        boolean next();
    }

    public interface SegmentView {
        int getIndex();

        float getX(final float t);

        float getY(final float t);

        float getZ(final float t);

        Vec3 getPos();

        float getYaw();

        float getPitch();

        float getLength();
    }

    public interface PointVisitor {
        void visit(final int index, final float x, final float y, final float z, final float yaw, final float pitch);
    }

    abstract class CurveSegmentIterator<C extends Curve> implements SegmentIterator {
        protected final C curve;
        protected final boolean inclusive;
        protected final int count;
        protected int index;

        public CurveSegmentIterator(C curve, boolean inclusive) {
            this.curve = curve;
            this.inclusive = inclusive;
            this.count = curve.getCount();
            this.index = -1;
        }

        public boolean hasNext() {
            return this.index + 1 + (inclusive ? 0 : 1) < count;
        }

        @Override
        public boolean next() {
            final int nextIndex = this.index + 1;
            if (inclusive ? nextIndex > count : nextIndex >= count) {
                throw new NoSuchElementException();
            }
            this.index = nextIndex;
            return nextIndex + (inclusive ? 0 : 1) < count;
        }

        protected void checkIndex(final float t) {
            if (this.index + (inclusive && t == 0.0F ? 0 : 1) >= count) {
                throw new IllegalStateException();
            }
        }

        @Override
        public int getIndex() {
            this.checkIndex(0.0F);
            return this.index;
        }

        @Override
        public float getX(final float t) {
            this.checkIndex(t);
            if (t == 0.0F) {
                return this.curve.getX(index);
            }
            if (t == 1.0F) {
                return this.curve.getX(index + 1);
            }
            return this.curve.getX(index, t);
        }

        @Override
        public float getY(final float t) {
            this.checkIndex(t);
            if (t == 0.0F) {
                return this.curve.getY(this.index);
            }
            if (t == 1.0F) {
                return this.curve.getY(this.index + 1);
            }
            return this.curve.getY(index, t);
        }

        @Override
        public float getZ(final float t) {
            this.checkIndex(t);
            if (t == 0.0F) {
                return this.curve.getZ(index);
            }
            if (t == 1.0F) {
                return this.curve.getZ(index);
            }
            return this.curve.getZ(index, t);
        }

        @Override
        public Vec3 getPos() {
            return new Vec3(this.curve.getX(this.index), this.curve.getY(this.index), this.curve.getZ(this.index));
        }

        @Override
        public abstract float getYaw();

        @Override
        public float getPitch() {
            this.checkIndex(1.0F);
            if (inclusive) {
                throw new IllegalStateException();
            }
            return this.getPitch(this.index);
        }

        protected abstract float getPitch(int index);

        @Override
        public float getLength() {
            this.checkIndex(1.0F);
            if (inclusive) {
                throw new IllegalStateException();
            }
            return this.getLength(this.index);
        }

        protected abstract float getLength(int index);
    }
}
