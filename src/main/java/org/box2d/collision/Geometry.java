package org.box2d.collision;

/**
 * Geometry interface enables pattern matching on the shape's geometry:
 * {@snippet lang=java:
 * switch (shape.geometry()) {
 *     case Circle circle -> drawCircle(circle);
 *     case Capsule capsule -> drawCapsule(capsule);
 *     case Segment segment -> drawSegment(segment);
 *     case Polygon polygon -> drawPolygon(polygon);
 * }
 * }
 * @apiNote More geometry types might be permitted in the future, which will make a switch without the {@code default} branch incomplete.
 */
public sealed interface Geometry permits Circle, Capsule, Segment, Polygon {
}
