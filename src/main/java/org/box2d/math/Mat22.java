package org.box2d.math;

import org.box2d.NativeLoader;
import org.box2d.internal.b2Mat22;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

/**
 * 2x2 column-major matrix, stored as two column vectors.
 * <p>
 * Components are named {@code mIJ} where {@code I} is the column and {@code J} is the row: {@code m00 = cx.x}, {@code m01 = cx.y}, {@code m10 = cy.x},
 * {@code m11 = cy.y}. The {@code get}/{@code set} index accessors take {@code (row, column)}.
 * </p>
 * Wrapper for native {@link b2Mat22}.
 */
public class Mat22 {
    private final MemorySegment segment;

    static {
        if (!NativeLoader.isLoaded()) NativeLoader.load();
    }

    /**
     * Create a new {@link Mat22} initialized to the identity matrix.
     */
    public Mat22() {
        this(1.0f, 0.0f, 0.0f, 1.0f);
    }

    /**
     * Create a new {@link Mat22} as the rotation matrix of the given angle in Radians.
     * @param angle the angle to build the rotation matrix from
     */
    public Mat22(float angle) {
        this(new Rot(angle));
    }

    /**
     * Create a new {@link Mat22} as the rotation matrix of the given rotation.
     * @param rotation the rotation to build the matrix from
     */
    public Mat22(Rot rotation) {
        this(rotation.c(), rotation.s(), -rotation.s(), rotation.c());
    }

    /**
     * Create a new {@link Mat22} from the given column vectors.
     * @param columnX the first column
     * @param columnY the second column
     */
    public Mat22(Vec2 columnX, Vec2 columnY) {
        segment = Arena.ofAuto().allocate(b2Mat22.layout());
        cx(columnX);
        cy(columnY);
    }

    /**
     * Create a new {@link Mat22} from the given component values.
     * @param m00 column 0, row 0
     * @param m01 column 0, row 1
     * @param m10 column 1, row 0
     * @param m11 column 1, row 1
     */
    public Mat22(float m00, float m01, float m10, float m11) {
        segment = Arena.ofAuto().allocate(b2Mat22.layout());
        set(m00, m01, m10, m11);
    }

    /**
     * Create a new {@link Mat22} and initialize its components to the one of the given matrix.
     * @param other the {@link Mat22} to copy the value from
     */
    public Mat22(Mat22 other) {
        this(other.cx(), other.cy());
    }

    /**
     * Wrap an existing {@link b2Mat22} memory segment.
     * The segment is not owned by this {@link Mat22} instance.
     * @param segment the memory segment to wrap
     */
    public Mat22(MemorySegment segment) {
        this.segment = segment;
    }

    /**
     * Create a new {@link Mat22} in the given arena, from the given column vectors.
     * @param arena the arena to allocate in
     * @param cx the first column
     * @param cy the second column
     */
    public Mat22(Arena arena, Vec2 cx, Vec2 cy) {
        segment = arena.allocate(b2Mat22.layout());
        cx(cx);
        cy(cy);
    }

    /**
     * Create a new {@link Mat22} in the given arena, from the given component values.
     * @param arena the arena to allocate in
     * @param m00 column 0, row 0
     * @param m01 column 0, row 1
     * @param m10 column 1, row 0
     * @param m11 column 1, row 1
     */
    public Mat22(Arena arena, float m00, float m01, float m10, float m11) {
        segment = arena.allocate(b2Mat22.layout());
        set(m00, m01, m10, m11);
    }

    /**
     * Get the component at column 0, row 0.
     * @return value of the component
     */
    public float m00() {
        return cx().x();
    }

    /**
     * Set the component at column 0, row 0.
     * @param m00 the value to set
     * @return this
     */
    public Mat22 m00(float m00) {
        cx().setX(m00);
        return this;
    }

    /**
     * Get the component at column 0, row 1.
     * @return value of the component
     */
    public float m01() {
        return cx().y();
    }

    /**
     * Set the component at column 0, row 1.
     * @param m01 the value to set
     * @return this
     */
    public Mat22 m01(float m01) {
        cx().setY(m01);
        return this;
    }

    /**
     * Get the component at column 1, row 0.
     * @return value of the component
     */
    public float m10() {
        return cy().x();
    }

    /**
     * Set the component at column 1, row 0.
     * @param m10 the value to set
     * @return this
     */
    public Mat22 m10(float m10) {
        cy().setX(m10);
        return this;
    }

    /**
     * Get the component at column 1, row 1.
     * @return value of the component
     */
    public float m11() {
        return cy().y();
    }

    /**
     * Set the component at column 1, row 1.
     * @param m11 the value to set
     * @return this
     */
    public Mat22 m11(float m11) {
        cy().setY(m11);
        return this;
    }

    /**
     * Get the first column of this matrix.
     * This returns a wrapper, not a new copy.
     * @return a {@link Vec2} that wrap the first column
     */
    public Vec2 cx() {
        return new Vec2(b2Mat22.cx(segment));
    }

    /**
     * Set the first column to the given vector.
     * @param cx the first column
     * @return this
     */
    public Mat22 cx(Vec2 cx) {
        b2Mat22.cx(segment, cx.segment());
        return this;
    }

    /**
     * Set the first column to the given values.
     * @param x the column's x component
     * @param y the column's y component
     * @return this
     */
    public Mat22 cx(float x, float y) {
        cx().set(x, y);
        return this;
    }

    /**
     * Get the second column of this matrix.
     * This returns a wrapper, not a new copy.
     * @return a {@link Vec2} that wrap the second column
     */
    public Vec2 cy() {
        return new Vec2(b2Mat22.cy(segment));
    }

    /**
     * Set the second column to the given vector.
     * @param cy the second column
     * @return this
     */
    public Mat22 cy(Vec2 cy) {
        b2Mat22.cy(segment, cy.segment());
        return this;
    }

    /**
     * Set the second column to the given values.
     * @param x the column's x component
     * @param y the column's y component
     * @return this
     */
    public Mat22 cy(float x, float y) {
        cy().set(x, y);
        return this;
    }

    /**
     * Get the component at the given row and column.
     * @param row the row index, 0 or 1
     * @param column the column index, 0 or 1
     * @return value of the component
     */
    public float get(int row, int column) {
        checkIndices(row, column);
        Vec2 c = column == 0 ? cx() : cy();
        return row == 0 ? c.x() : c.y();
    }

    /**
     * Set this matrix to the rotation matrix of the given angle in Radians.
     * @param angle the angle to build the rotation matrix from
     * @return this
     */
    public Mat22 angle(float angle) {
        return rotation(new Rot(angle));
    }

    /**
     * Set this matrix to the rotation matrix of the given rotation.
     * @param rotation the rotation to build the matrix from
     * @return this
     */
    public Mat22 rotation(Rot rotation) {
        float c = rotation.c();
        float s = rotation.s();
        return set(c, s, -s, c);
    }

    /**
     * Set the components of this matrix to that of the given matrix.
     * @param other the other {@link Mat22} to set the values from
     * @return this
     */
    public Mat22 set(Mat22 other) {
        set(other.cx(), other.cy());
        return this;
    }

    /**
     * Set the columns of this matrix to the given vectors.
     * @param cx the first column
     * @param cy the second column
     * @return this
     */
    public Mat22 set(Vec2 cx, Vec2 cy) {
        cx(cx);
        cy(cy);
        return this;
    }

    /**
     * Set the component at the given row and column.
     * @param row the row index, 0 or 1
     * @param column the column index, 0 or 1
     * @param value the value to set
     * @return this
     */
    public Mat22 set(int row, int column, float value) {
        checkIndices(row, column);
        Vec2 c = column == 0 ? cx() : cy();
        if (row == 0) {
            c.setX(value);
            return this;
        }
        c.setY(value);
        return this;
    }

    /**
     * Set the components of this matrix to the given values.
     * @param m00 column 0, row 0
     * @param m01 column 0, row 1
     * @param m10 column 1, row 0
     * @param m11 column 1, row 1
     * @return this
     */
    public Mat22 set(float m00, float m01, float m10, float m11) {
        cx(m00, m01);
        cy(m10, m11);
        return this;
    }

    /**
     * Set this matrix to the identity matrix.
     * @return this
     */
    public Mat22 toIdentity() {
        return set(1.0f, 0.0f, 0.0f, 1.0f);
    }

    /**
     * Set all components of this matrix to zero.
     * @return this
     */
    public Mat22 toZero() {
        return set(0.0f, 0.0f, 0.0f, 0.0f);
    }

    /**
     * Add the given matrix to this matrix component-wise.
     * @param other the matrix to add
     * @return this
     */
    public Mat22 add(Mat22 other) {
        cx().add(other.cx());
        cy().add(other.cy());
        return this;
    }

    /**
     * Subtract the given matrix from this matrix component-wise.
     * @param other the matrix to subtract
     * @return this
     */
    public Mat22 sub(Mat22 other) {
        cx().sub(other.cx());
        cy().sub(other.cy());
        return this;
    }

    /**
     * Multiply all components of this matrix by the given scalar.
     * @param scalar the value to multiply by
     * @return this
     */
    public Mat22 mul(float scalar) {
        cx().mul(scalar);
        cy().mul(scalar);
        return this;
    }

    /**
     * Transpose this matrix in place.
     * @return this
     */
    public Mat22 transpose() {
        return set(m00(), m10(), m01(), m11());
    }

    /**
     * Get the determinant of this matrix.
     * @return the determinant value
     */
    public float determinant() {
        return m00() * m11() - m10() * m01();
    }

    /**
     * Create a new {@link Mat22} with its components initialized to this matrix's component values.
     * @return a new {@link Mat22}
     */
    public Mat22 copy() {
        return new Mat22(cx(), cy());
    }

    /**
     * Get the memory segment of this {@link Mat22}.
     * @return the underlying memory segment
     */
    public MemorySegment segment() {
        return segment;
    }

    /**
     * Get a new {@link Mat22} as the identity matrix.
     * @return a new identity {@link Mat22}
     */
    public static Mat22 identity() {
        return new Mat22();
    }

    private static void checkIndices(int row , int column) {
        if (row >= 0 && row <= 1 && column >= 0 && column <= 1) return;
        throw new IndexOutOfBoundsException("Indices for 2x2 matrix must be 0 or 1");
    }

    @Override
    public String toString() {
        return String.format("Mat22[[%.3f, %.3f], [%.3f, %.3f]]", m00(), m10(), m01(), m11());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Mat22 mat22)) return false;
        return Float.compare(m00(), mat22.m00()) == 0
                && Float.compare(m01(), mat22.m01()) == 0
                && Float.compare(m10(), mat22.m10()) == 0
                && Float.compare(m11(), mat22.m11()) == 0;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Float.floatToIntBits(m00());
        result = prime * result + Float.floatToIntBits(m01());
        result = prime * result + Float.floatToIntBits(m10());
        result = prime * result + Float.floatToIntBits(m11());
        return result;
    }
}
