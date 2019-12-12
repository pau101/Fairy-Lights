package me.paulf.fairylights.util.matrix;

public interface Matrix {
	void translate(final float x, final float y, final float z);

	void rotate(final float angle, final float x, final float y, final float z);
}
