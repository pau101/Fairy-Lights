package me.paulf.fairylights.util;

import me.paulf.fairylights.server.connection.Connection;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

import java.util.NoSuchElementException;

public final class Catenary {
    private static final int MIN_VERTEX_COUNT = 8;

    private final int count;

    private final float yaw;

    private final float dx;

    private final float dz;

    private final float[] x;

    private final float[] y;

    private final float length;

    private Catenary(final int count, final float yaw, final float dx, final float dz, final float[] x, final float[] y, final float length) {
        this.count = count;
        this.yaw = yaw;
        this.dx = dx;
        this.dz = dz;
        this.x = x;
        this.y = y;
        this.length = length;
    }

    public int getCount() {
        return this.count;
    }

    public float getX() {
        return this.x[this.count - 1] * this.dx;
    }

    public float getY() {
        return this.y[this.count - 1];
    }

    public float getZ() {
        return this.x[this.count - 1] * this.dz;
    }

    public float getX(final int i) {
        return this.x[i] * this.dx;
    }

    public float getY(final int i) {
        return this.y[i];
    }

    public float getZ(final int i) {
        return this.x[i] * this.dz;
    }

    public float getDx(final int i) {
        return (this.x[i + 1] - this.x[i]) * this.dx;
    }

    public float getDy(final int i) {
        return (this.y[i + 1] - this.y[i]);
    }

    public float getDz(final int i) {
        return (this.x[i + 1] - this.x[i]) * this.dz;
    }

    public SegmentIterator iterator() {
        return this.iterator(false);
    }

    public Catenary lerp(final Catenary other, final float delta) {
        if (this == other) {
            return this;
        }
        if (this.count > other.count) {
            return other.lerp(this, 1.0F - delta);
        }
        final float[] nx = new float[this.count];
        final float[] ny = new float[this.count];
        for (int i = 0; i < this.count; i++) {
            final boolean end = this.count != other.count && i == this.count - 1;
            nx[i] = MathHelper.func_219799_g(delta, this.x[i], other.x[end ? other.count - 1 : i]);
            ny[i] = MathHelper.func_219799_g(delta, this.y[i], other.y[end ? other.count - 1 : i]);
        }
        final float angle = Mth.lerpAngle(this.yaw, other.yaw, delta);
        final float vx = MathHelper.func_76134_b(angle);
        final float vz = MathHelper.func_76126_a(angle);
        return new Catenary(this.count, angle, vx, vz, nx, ny, MathHelper.func_219799_g(delta, this.length, other.length));
    }

    public void visitPoints(final float spacing, final boolean center, final PointVisitor visitor) {
        float distance = center ? (this.length % spacing + spacing) / 2.0F : 0;
        int index = 0;
        final Catenary.SegmentIterator it = this.iterator();
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

    public SegmentIterator iterator(final boolean inclusive) {
        return new SegmentIterator() {
            private int index = -1;

            public boolean hasNext() {
                return this.index + 1 + (inclusive ? 0 : 1) < Catenary.this.count;
            }

            @Override
            public boolean next() {
                final int nextIndex = this.index + 1;
                if (inclusive ? nextIndex > Catenary.this.count : nextIndex >= Catenary.this.count) {
                    throw new NoSuchElementException();
                }
                this.index = nextIndex;
                return nextIndex + (inclusive ? 0 : 1) < Catenary.this.count;
            }

            private void checkIndex(final float t) {
                if (this.index + (inclusive && t == 0.0F ? 0 : 1) >= Catenary.this.count) {
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
                    return Catenary.this.x[this.index] * Catenary.this.dx;
                }
                if (t == 1.0F) {
                    return Catenary.this.x[this.index + 1] * Catenary.this.dx;
                }
                return MathHelper.func_219799_g(t, Catenary.this.x[this.index], Catenary.this.x[this.index + 1]) * Catenary.this.dx;
            }

            @Override
            public float getY(final float t) {
                this.checkIndex(t);
                if (t == 0.0F) {
                    return Catenary.this.y[this.index];
                }
                if (t == 1.0F) {
                    return Catenary.this.y[this.index + 1];
                }
                return MathHelper.func_219799_g(t, Catenary.this.y[this.index], Catenary.this.y[this.index + 1]);
            }

            @Override
            public float getZ(final float t) {
                this.checkIndex(t);
                if (t == 0.0F) {
                    return Catenary.this.x[this.index] * Catenary.this.dz;
                }
                if (t == 1.0F) {
                    return Catenary.this.x[this.index + 1] * Catenary.this.dz;
                }
                return MathHelper.func_219799_g(t, Catenary.this.x[this.index], Catenary.this.x[this.index + 1]) * Catenary.this.dz;
            }

            @Override
            public Vector3d getPos() {
                return new Vector3d(Catenary.this.x[this.index] * Catenary.this.dx, Catenary.this.y[this.index], Catenary.this.x[this.index] * Catenary.this.dz);
            }

            @Override
            public float getYaw() {
                return Catenary.this.yaw;
            }

            @Override
            public float getPitch() {
                this.checkIndex(1.0F);
                if (inclusive) {
                    throw new IllegalStateException();
                }
                final float dx = Catenary.this.x[this.index + 1] - Catenary.this.x[this.index];
                final float dy = Catenary.this.y[this.index + 1] - Catenary.this.y[this.index];
                return (float) MathHelper.func_181159_b(dy, dx);
            }

            @Override
            public float getLength() {
                this.checkIndex(1.0F);
                if (inclusive) {
                    throw new IllegalStateException();
                }
                final float dx = Catenary.this.x[this.index + 1] - Catenary.this.x[this.index];
                final float dy = Catenary.this.y[this.index + 1] - Catenary.this.y[this.index];
                return MathHelper.func_76129_c(dx * dx + dy * dy);
            }
        };
    }

    public interface SegmentIterator extends SegmentView {
        boolean hasNext();

        boolean next();
    }

    public interface SegmentView {
        int getIndex();

        float getX(final float t);

        float getY(final float t);

        float getZ(final float t);

        Vector3d getPos();

        float getYaw();

        float getPitch();

        float getLength();
    }

    public interface PointVisitor {
        void visit(final int index, final float x, final float y, final float z, final float yaw, final float pitch);
    }

    public float getLength() {
        return this.length;
    }

    public static Catenary from(final Vector3d direction, final float verticalYaw, final CubicBezier bezier, final float slack) {
        final float dist = (float) direction.func_72433_c();
        final float length;
        if (slack < 1e-2 || Math.abs(direction.field_72450_a) < 1e-6 && Math.abs(direction.field_72449_c) < 1e-6) {
            length = dist;
        } else {
            length = dist + (lengthFunc(bezier, dist) - dist) * slack;
        }
        return from(direction, verticalYaw, length);
    }

    private static float lengthFunc(final CubicBezier bezier, final double length) {
        return bezier.eval(MathHelper.func_76131_a((float) length / Connection.MAX_LENGTH, 0, 1)) * Connection.MAX_LENGTH;
    }

    public static Catenary from(final Vector3d dir, final float verticalYaw, final float ropeLength) {
        final float endX = MathHelper.func_76133_a(dir.field_72450_a * dir.field_72450_a + dir.field_72449_c * dir.field_72449_c);
        final float endY = (float) dir.field_72448_b;
        final float angle = endX < 1e-3F ? endY < 0.0F ? verticalYaw + Mth.PI : verticalYaw : (float) MathHelper.func_181159_b(dir.field_72449_c, dir.field_72450_a);
        final float vx = MathHelper.func_76134_b(angle);
        final float vz = MathHelper.func_76126_a(angle);
        if (dir.func_72433_c() > 2.0F * Connection.MAX_LENGTH) {
            return new Catenary(2, angle, vx, vz, new float[]{0.0F, endX}, new float[]{0.0F, endY}, MathHelper.func_76129_c(endX * endX + endY * endY));
        }
        final int count = Math.max((int) (ropeLength * CatenaryUtils.SEG_LENGTH), MIN_VERTEX_COUNT);
        final float[] x = new float[count];
        final float[] y = new float[count];
        CatenaryUtils.catenary(0.0F, 0.0F, endX, endY, ropeLength, count, x, y);
        float length = 0.0F;
        for (int i = 1; i < count; i++) {
            final float dx = x[i] - x[i - 1];
            final float dy = y[i] - y[i - 1];
            length += MathHelper.func_76129_c(dx * dx + dy * dy);
        }
        return new Catenary(count, angle, vx, vz, x, y, length);
    }
}
