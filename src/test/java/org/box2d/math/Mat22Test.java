package org.box2d.math;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class Mat22Test {
    private static final float delta = 0.0001f;

    @Test
    @DisplayName("Mat22 default constructor creates identity")
    void testDefaultConstructor() {
        Mat22 m = new Mat22();
        assertEquals(1.0f, m.m00(), "m00 expected 1.0");
        assertEquals(0.0f, m.m01(), "m01 expected 0.0");
        assertEquals(0.0f, m.m10(), "m10 expected 0.0");
        assertEquals(1.0f, m.m11(), "m11 expected 1.0");
    }

    @Test
    @DisplayName("Mat22 component constructor create matrix with values")
    void testComponentConstructor() {
        Mat22 m = new Mat22(1.0f, 2.0f, 3.0f, 4.0f);
        assertEquals(1.0f, m.m00(), "m00 expected 1.0");
        assertEquals(2.0f, m.m01(), "m01 expected 2.0");
        assertEquals(3.0f, m.m10(), "m10 expected 3.0");
        assertEquals(4.0f, m.m11(), "m11 expected 4.0");
    }

    @Test
    @DisplayName("Mat22 column constructor create matrix with values")
    void testColumnConstructor() {
        Mat22 m = new Mat22(new Vec2(1.0f, 2.0f), new Vec2(3.0f, 4.0f));
        assertEquals(1.0f, m.cx().x(), "cx.x expected 1.0");
        assertEquals(2.0f, m.cx().y(), "cx.y expected 2.0");
        assertEquals(3.0f, m.cy().x(), "cy.x expected 3.0");
        assertEquals(4.0f, m.cy().y(), "cy.y expected 4.0");
    }

    @Test
    @DisplayName("Mat22 from rotation is the rotation matrix [[c,-s],[s,c]]")
    void testRotationMatrix() {
        float angle = (float) Math.PI / 2.0f; // c = 0, s = 1
        Mat22 m = new Mat22(new Rot(angle));
        assertEquals(0.0f, m.get(0, 0), delta, "(0,0) expected c = 0.0");
        assertEquals(-1.0f, m.get(0, 1), delta, "(0,1) expected -s = -1.0");
        assertEquals(1.0f, m.get(1, 0), delta, "(1,0) expected s = 1.0");
        assertEquals(0.0f, m.get(1, 1), delta, "(1,1) expected c = 0.0");
    }

    @Test
    @DisplayName("Mat22 angle and rotation setters agree")
    void testAngleMatchesRotation() {
        float angle = (float) Math.PI / 3.0f;
        Mat22 fromAngle = new Mat22().angle(angle);
        Mat22 fromRot = new Mat22().rotation(new Rot(angle));
        assertEquals(fromRot, fromAngle, "angle() expected equal to rotation()");
        assertEquals(new Mat22(new Rot(angle)), fromAngle, "constructor expected equal to angle()");
    }

    @Test
    @DisplayName("Mat22 transpose swaps off-diagonal in place")
    void testTranspose() {
        Mat22 m = new Mat22(1.0f, 2.0f, 3.0f, 4.0f);
        Mat22 result = m.transpose();
        assertSame(m, result, "transpose expected to return this");
        assertEquals(1.0f, m.m00(), "m00 expected unchanged 1.0");
        assertEquals(3.0f, m.m01(), "m01 expected swapped to 3.0");
        assertEquals(2.0f, m.m10(), "m10 expected swapped to 2.0");
        assertEquals(4.0f, m.m11(), "m11 expected unchanged 4.0");
    }

    @Test
    @DisplayName("Mat22 determinant")
    void testDeterminant() {
        assertEquals(1.0f, new Mat22().determinant(), delta, "identity determinant expected 1.0");
        assertEquals(-2.0f, new Mat22(1.0f, 2.0f, 3.0f, 4.0f).determinant(), delta, "determinant expected -2.0");
        assertEquals(1.0f, new Mat22((float) Math.PI / 4.0f).determinant(), delta, "rotation determinant expected 1.0");
    }

    @Test
    @DisplayName("Mat22 index get and set")
    void testIndexAccess() {
        Mat22 m = new Mat22();
        m.set(0, 1, 9.0f);
        assertEquals(9.0f, m.get(0, 1), "(0,1) expected 9.0");
        assertThrows(IndexOutOfBoundsException.class, () -> m.get(2, 0), "out of range index expected to throw");
        assertThrows(IndexOutOfBoundsException.class, () -> m.set(0, -1, 0.0f), "out of range index expected to throw");
    }

    @Test
    @DisplayName("Mat22 identity and zero")
    void testIdentityAndZero() {
        Mat22 m = new Mat22(1.0f, 2.0f, 3.0f, 4.0f).toZero();
        assertEquals(0.0f, m.determinant(), "zero matrix determinant expected 0.0");
        m.toIdentity();
        assertEquals(Mat22.identity(), m, "expected identity matrix");
    }

    @Test
    @DisplayName("Mat22 copy independent")
    void testCopyIndependent() {
        Mat22 og = new Mat22(1.0f, 2.0f, 3.0f, 4.0f);
        Mat22 copy = og.copy();
        assertEquals(og, copy, "copy expected equal");

        copy.m00(9.0f);
        assertEquals(1.0f, og.m00(), "original m00 expected unchanged 1.0");
    }

    @Test
    @DisplayName("Mat22 memory segment access")
    void testMemSeg() {
        assertNotNull(new Mat22().segment(), "Segment expected not null");
    }

    @Test
    @DisplayName("Mat22 equals and hashCode")
    void testEqualsHashCode() {
        Mat22 m1 = new Mat22(1.0f, 2.0f, 3.0f, 4.0f);
        Mat22 m2 = new Mat22(1.0f, 2.0f, 3.0f, 4.0f);
        Mat22 m3 = new Mat22(4.0f, 3.0f, 2.0f, 1.0f);

        assertEquals(m1, m2, "Equal matrices expected equal");
        assertEquals(m1.hashCode(), m2.hashCode(), "Equal matrices expected same hash code");
        assertNotEquals(m1, m3, "Different matrices expected not equal");
        assertEquals(m1, m1, "Same reference expected equal");
        assertNotEquals(null, m1, "Matrix and null expected not equal");
    }
}
