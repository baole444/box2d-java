package org.box2d.collision;

import org.box2d.internal.b2CastOutput;
import org.box2d.internal.b2Vec2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

import static org.junit.jupiter.api.Assertions.*;

class CastOutputTest {
    private static final float delta = 0.0001f;

    private static MemorySegment output(float pointX, float fraction, boolean hit) {
        MemorySegment segment = Arena.ofAuto().allocate(b2CastOutput.layout());
        MemorySegment normal = b2CastOutput.normal(segment);
        b2Vec2.x(normal, -1.0f);
        b2Vec2.y(normal, 0.0f);
        MemorySegment point = b2CastOutput.point(segment);
        b2Vec2.x(point, pointX);
        b2Vec2.y(point, 2.0f);
        b2CastOutput.fraction(segment, fraction);
        b2CastOutput.iterations(segment, 3);
        b2CastOutput.hit(segment, hit);
        return segment;
    }

    @Test
    @DisplayName("CastOutput reads the native struct")
    void testRead() {
        CastOutput output = new CastOutput(output(1.0f, 0.4f, true));
        assertEquals(-1.0f, output.normal().x(), delta, "normal x expected from the struct");
        assertEquals(0.0f, output.normal().y(), delta, "normal y expected from the struct");
        assertEquals(1.0f, output.point().x(), delta, "point x expected from the struct");
        assertEquals(2.0f, output.point().y(), delta, "point y expected from the struct");
        assertEquals(0.4f, output.fraction(), delta, "fraction expected from the struct");
        assertEquals(3, output.iterations(), "iterations expected from the struct");
        assertTrue(output.hit(), "hit expected from the struct");
    }

    @Test
    @DisplayName("CastOutput accessors return copies and copy() is independent")
    void testReadOnly() {
        MemorySegment segment = output(1.0f, 0.4f, true);
        CastOutput output = new CastOutput(segment);
        output.point().x(9.0f);
        output.normal().y(9.0f);
        assertEquals(1.0f, output.point().x(), delta, "changing a returned point expected not to change the output");
        assertEquals(0.0f, output.normal().y(), delta, "changing a returned normal expected not to change the output");

        CastOutput copy = output.copy();
        b2CastOutput.fraction(segment, 0.9f);
        assertEquals(0.4f, copy.fraction(), delta, "copy expected independent of the original segment");
    }

    @Test
    @DisplayName("CastOutput equality and hash code are by value")
    void testEquality() {
        CastOutput a = new CastOutput(output(1.0f, 0.4f, true));
        CastOutput b = new CastOutput(output(1.0f, 0.4f, true));
        assertEquals(a, b, "outputs with the same values expected equal");
        assertEquals(a.hashCode(), b.hashCode(), "equal outputs expected the same hash code");
        assertNotEquals(a, new CastOutput(output(1.0f, 0.4f, false)), "outputs with different hit expected not equal");
        assertNotEquals(a, new CastOutput(output(2.0f, 0.4f, true)), "outputs with different points expected not equal");
    }
}
