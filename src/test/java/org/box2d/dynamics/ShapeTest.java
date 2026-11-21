package org.box2d.dynamics;

import org.box2d.Box2D;
import org.box2d.collision.*;
import org.box2d.math.Vec2;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ShapeTest {
    private static final float delta = 0.0001f;
    private World world;
    private Body body;

    @BeforeEach
    void setUp() {
        world = new World();
        body = world.createBody(new BodyDef().type(BodyType.DynamicBody));
    }

    @AfterEach
    void tearDown() {
        world.destroy();
    }

    private Shape box() {
        return body.createPolygonShape(new ShapeDef(), Polygon.box(1.0f, 1.0f));
    }

    @Test
    @DisplayName("Shape is valid on create and invalid after destroy")
    void testLifecycle() {
        Shape shape = box();
        assertTrue(shape.isValid(), "new shape expected valid");
        assertSame(world, shape.world(), "shape expected to return its world");
        assertEquals(body, shape.body(), "shape expected to return its body");
        shape.destroy();
        assertFalse(shape.isValid(), "destroyed shape expected invalid");
        shape.destroy();
        assertThrows(IllegalStateException.class, shape::type, "use after destroy expected to throw");
    }

    @Test
    @DisplayName("Destroying the body or the world invalidates its shapes")
    void testParentDestroyInvalidatesShapes() {
        Shape shape = box();
        body.destroy();
        assertFalse(shape.isValid(), "shape expected invalid after its body is destroyed");

        World other = new World();
        Shape orphan = other.createBody(new BodyDef()).createCircleShape(new ShapeDef(), new Circle(1.0f));
        other.destroy();
        assertFalse(orphan.isValid(), "shape expected invalid after its world is destroyed");
        assertThrows(IllegalStateException.class, orphan::body, "use after world destroy expected to throw");
    }

    @Test
    @DisplayName("Shape rejects an id from another world")
    void testForeignId() {
        World other = new World();
        try {
            Shape foreign = other.createBody(new BodyDef()).createCircleShape(new ShapeDef(), new Circle(1.0f));
            assertThrows(IllegalArgumentException.class, () -> new Shape(world, foreign.id()), "foreign id expected to be rejected");
        } finally {
            other.destroy();
        }
    }

    @Test
    @DisplayName("Each geometry type creates a valid shape whose geometry reads back")
    void testGeometryRoundTrip() {
        Circle circle = new Circle(0.5f, 0.25f, 1.0f);
        Capsule capsule = new Capsule(0.0f, -1.0f, 0.0f, 1.0f, 0.5f);
        Segment segment = new Segment(-1.0f, 0.0f, 1.0f, 0.0f);
        Polygon polygon = Polygon.box(1.0f, 2.0f);

        Shape circleShape = body.createCircleShape(new ShapeDef(), circle);
        Shape capsuleShape = body.createCapsuleShape(new ShapeDef(), capsule);
        Shape segmentShape = body.createSegmentShape(new ShapeDef(), segment);
        Shape polygonShape = body.createPolygonShape(new ShapeDef(), polygon);

        assertEquals(ShapeType.CircleShape, circleShape.type(), "circle shape type expected");
        assertEquals(ShapeType.CapsuleShape, capsuleShape.type(), "capsule shape type expected");
        assertEquals(ShapeType.SegmentShape, segmentShape.type(), "segment shape type expected");
        assertEquals(ShapeType.PolygonShape, polygonShape.type(), "polygon shape type expected");

        assertEquals(circle, circleShape.circle(), "circle expected to read back");
        assertEquals(capsule, capsuleShape.capsule(), "capsule expected to read back");
        assertEquals(segment, segmentShape.segment(), "segment expected to read back");
        assertEquals(polygon, polygonShape.polygon(), "polygon expected to read back");

        assertEquals(circle, circleShape.geometry(), "geometry() expected to give the circle");
        assertEquals(capsule, capsuleShape.geometry(), "geometry() expected to give the capsule");
        assertEquals(segment, segmentShape.geometry(), "geometry() expected to give the segment");
        assertEquals(polygon, polygonShape.geometry(), "geometry() expected to give the polygon");
    }

    @Test
    @DisplayName("geometry() supports pattern matching over the sealed Geometry")
    void testGeometryPatternMatching() {
        Shape shape = body.createCapsuleShape(new ShapeDef(), Capsule.vertical(1.0f, 3.0f));
        String kind = switch (shape.geometry()) {
            case Circle c -> "circle";
            case Capsule c -> "capsule";
            case Segment s -> "segment";
            case Polygon p -> "polygon";
        };
        assertEquals("capsule", kind, "pattern match expected to select the capsule");
    }

    @Test
    @DisplayName("A typed geometry getter on another type throws")
    void testWrongTypeGetter() {
        Shape shape = box();
        assertThrows(IllegalStateException.class, shape::circle, "circle() on a polygon expected to throw");
        assertThrows(IllegalStateException.class, shape::capsule, "capsule() on a polygon expected to throw");
        assertThrows(IllegalStateException.class, shape::segment, "segment() on a polygon expected to throw");
        assertNotNull(shape.polygon(), "polygon() on a polygon expected to succeed");
    }

    @Test
    @DisplayName("A geometry setter of another type changes the shape type")
    void testSetterChangesType() {
        Shape shape = body.createCircleShape(new ShapeDef(), new Circle(1.0f));
        Polygon polygon = Polygon.box(0.5f, 0.5f);
        shape.polygon(polygon);
        assertEquals(ShapeType.PolygonShape, shape.type(), "setting a polygon expected to change the type");
        assertEquals(polygon, shape.polygon(), "polygon expected to read back after the change");

        Circle circle = new Circle(2.0f);
        shape.circle(circle);
        assertEquals(ShapeType.CircleShape, shape.type(), "setting a circle expected to change the type back");
        assertEquals(circle, shape.circle(), "circle expected to read back after the change");
    }

    @Test
    @DisplayName("Setting a degenerate capsule is ignored")
    void testDegenerateCapsuleSetter() {
        Capsule capsule = new Capsule(0.0f, -1.0f, 0.0f, 1.0f, 0.5f);
        Shape shape = body.createCapsuleShape(new ShapeDef(), capsule);
        shape.capsule(new Capsule(0.0f, 0.0f, 0.0f, Box2D.LinearSlop * 0.5f, 0.5f));
        assertEquals(capsule, shape.capsule(), "degenerate capsule expected to be ignored");
    }

    @Test
    @DisplayName("Shape material round-trips, including a full 64-bit user material")
    void testMaterialRoundTrip() {
        Shape shape = box();
        long id = 0x8000_0000_0000_0001L;
        shape.friction(0.3f).restitution(0.4f).userMaterial(id);
        assertEquals(0.3f, shape.friction(), delta, "friction expected to round-trip");
        assertEquals(0.4f, shape.restitution(), delta, "restitution expected to round-trip");
        assertEquals(id, shape.userMaterial(), "user material expected to keep all 64 bits");

        SurfaceMaterial material = new SurfaceMaterial().friction(0.1f).restitution(0.2f).rollingResistance(0.3f).tangentSpeed(4.0f).userMaterialId(-2L).customColor(0x00FF00);
        shape.surfaceMaterial(material);
        assertEquals(material, shape.surfaceMaterial(), "surface material expected to round-trip");
        assertEquals(-2L, shape.userMaterial(), "user material expected to follow the surface material");
    }

    @Test
    @DisplayName("ShapeDef material, density, filter and flags reach the created shape")
    void testDefReachesShape() {
        ShapeDef def = new ShapeDef()
                .density(3.0f)
                .filter(new Filter(0x2L, 0x5L, -3))
                .sensor(true)
                .sensorEventsEnabled(true)
                .contactEventsEnabled(true)
                .hitEventsEnabled(true)
                .preSolveEventsEnabled(true);
        def.material().friction(0.15f).restitution(0.35f).userMaterialId(99L);
        Shape shape = body.createPolygonShape(def, Polygon.box(1.0f, 1.0f));
        assertEquals(3.0f, shape.density(), delta, "density expected from the def");
        assertEquals(0.15f, shape.friction(), delta, "friction expected from the def");
        assertEquals(0.35f, shape.restitution(), delta, "restitution expected from the def");
        assertEquals(99L, shape.userMaterial(), "user material expected from the def");
        assertEquals(new Filter(0x2L, 0x5L, -3), shape.filter(), "filter expected from the def");
        assertTrue(shape.sensor(), "sensor flag expected from the def");
        assertTrue(shape.sensorEventsEnabled(), "sensor events expected from the def");
        assertTrue(shape.contactEventsEnabled(), "contact events expected from the def");
        assertTrue(shape.hitEventsEnabled(), "hit events expected from the def");
        assertTrue(shape.preSolveEventsEnabled(), "pre-solve events expected from the def");
    }

    @Test
    @DisplayName("Shape filter and event toggles round-trip at run time")
    void testRuntimeToggles() {
        Shape shape = box();
        assertFalse(shape.sensor(), "default shape expected solid");
        assertFalse(shape.sensorEventsEnabled(), "sensor events expected off by default");
        assertFalse(shape.contactEventsEnabled(), "contact events expected off by default");
        assertFalse(shape.hitEventsEnabled(), "hit events expected off by default");
        assertFalse(shape.preSolveEventsEnabled(), "pre-solve events expected off by default");

        shape.filter(new Filter(0x8L, 0x1L, 2))
                .sensorEventsEnabled(true)
                .contactEventsEnabled(true)
                .hitEventsEnabled(true)
                .preSolveEventsEnabled(true);
        assertEquals(new Filter(0x8L, 0x1L, 2), shape.filter(), "filter expected to round-trip");
        assertTrue(shape.sensorEventsEnabled(), "sensor events expected to round-trip");
        assertTrue(shape.contactEventsEnabled(), "contact events expected to round-trip");
        assertTrue(shape.hitEventsEnabled(), "hit events expected to round-trip");
        assertTrue(shape.preSolveEventsEnabled(), "pre-solve events expected to round-trip");
    }

    @Test
    @DisplayName("Density and destroy update the body mass only when asked")
    void testUpdateBodyMass() {
        Shape shape = box();
        assertEquals(4.0f, body.mass(), delta, "2 by 2 box at density 1 expected mass 4");

        shape.density(2.0f);
        assertEquals(8.0f, body.mass(), delta, "density(float) expected to update the body mass");
        shape.density(3.0f, false);
        assertEquals(8.0f, body.mass(), delta, "density(float, false) expected to leave the body mass");
        body.updateMassFromShapes();
        assertEquals(12.0f, body.mass(), delta, "updateMassFromShapes expected to apply the new density");

        Shape second = box();
        float twoShapes = body.mass();
        second.destroy(false);
        assertEquals(twoShapes, body.mass(), delta, "destroy(false) expected to leave the body mass");
        body.updateMassFromShapes();
        assertEquals(12.0f, body.mass(), delta, "updateMassFromShapes expected to drop the destroyed shape");
    }

    @Test
    @DisplayName("Invalid creation gives an invalid shape and releases its user data slot")
    void testInvalidCreation() {
        Object data = new Object();
        ShapeDef def = new ShapeDef().userData(data);
        float tooShort = Box2D.LinearSlop * 0.5f;
        Shape capsule = body.createCapsuleShape(def, new Capsule(0.0f, 0.0f, 0.0f, tooShort, 0.5f));
        Shape segment = body.createSegmentShape(def, new Segment(0.0f, 0.0f, tooShort, 0.0f));
        Shape negativeDensity = body.createPolygonShape(new ShapeDef().userData(data).density(-1.0f), Polygon.box(1.0f, 1.0f));
        ShapeDef negativeFriction = new ShapeDef().userData(data);
        negativeFriction.material().friction(-1.0f);
        Shape badMaterial = body.createPolygonShape(negativeFriction, Polygon.box(1.0f, 1.0f));

        assertFalse(capsule.isValid(), "degenerate capsule expected invalid");
        assertFalse(segment.isValid(), "degenerate segment expected invalid");
        assertFalse(negativeDensity.isValid(), "negative density expected invalid");
        assertFalse(badMaterial.isValid(), "negative friction expected invalid");
        assertEquals(0, world.userDataTable().size(), "rejected shapes expected to release their slots");
        assertTrue(body.shapes().isEmpty(), "rejected shapes expected not attached");
    }

    @Test
    @DisplayName("Shape userData set, get, clear and release on shape and body destroy")
    void testUserData() {
        Object a = new Object();
        Object b = new Object();
        Shape shape = body.createPolygonShape(new ShapeDef().userData(a), Polygon.box(1.0f, 1.0f));
        assertSame(a, shape.userData(), "user data expected from the def");
        shape.userData(b);
        assertSame(b, shape.userData(), "user data expected to be replaced");
        assertEquals(1, world.userDataTable().size(), "replacing expected to release the old slot");
        shape.userData(null);
        assertNull(shape.userData(), "user data expected cleared");
        assertEquals(0, world.userDataTable().size(), "clearing expected to release the slot");

        shape.userData(a);
        shape.destroy();
        assertEquals(0, world.userDataTable().size(), "destroying the shape expected to release its slot");

        body.createPolygonShape(new ShapeDef().userData(a), Polygon.box(1.0f, 1.0f));
        body.createCircleShape(new ShapeDef().userData(b), new Circle(1.0f));
        body.destroy();
        assertEquals(0, world.userDataTable().size(), "destroying the body expected to release its shapes' slots");
    }

    @Test
    @DisplayName("Released shape slots are reused with a new generation")
    void testSlotReuse() {
        Shape first = body.createCircleShape(new ShapeDef().userData("first"), new Circle(1.0f));
        long staleHandle = UserDataTable.handleOf(org.box2d.internal.nBox2D.nShapeGetUserData(first.id()));
        first.destroy();
        Shape second = body.createCircleShape(new ShapeDef().userData("second"), new Circle(1.0f));
        assertEquals(1, world.userDataTable().slotCount(), "one slot expected to be reused");
        assertNull(world.userDataTable().resolve(staleHandle), "stale handle expected to resolve to null");
        assertEquals("second", second.userData(), "new shape expected its own user data");
    }

    @Test
    @DisplayName("Shape equality is by full id, Body.shapes() reflects creates and destroys")
    void testShapesAndEquality() {
        Shape a = box();
        Shape b = body.createCircleShape(new ShapeDef(), new Circle(1.0f));
        List<Shape> shapes = body.shapes();
        assertEquals(2, shapes.size(), "two shapes expected");
        assertTrue(shapes.contains(a) && shapes.contains(b), "snapshot expected to hold both shapes");
        Shape rebuilt = shapes.get(shapes.indexOf(a));
        assertEquals(a.hashCode(), rebuilt.hashCode(), "equal shapes expected the same hash code");
        assertNotEquals(a, b, "different shapes expected not equal");
        assertThrows(UnsupportedOperationException.class, () -> shapes.add(a), "snapshot expected unmodifiable");

        a.destroy();
        assertEquals(List.of(b), body.shapes(), "destroyed shape expected gone from the snapshot");
        assertEquals(2, shapes.size(), "an earlier snapshot expected unchanged");
    }

    @Test
    @DisplayName("Shape queries on a known box")
    void testQueries() {
        Body ground = world.createBody(new BodyDef());
        Shape shape = ground.createPolygonShape(new ShapeDef(), Polygon.box(1.0f, 1.0f));

        assertTrue(shape.testPoint(new Vec2(0.5f, 0.5f)), "point inside expected to overlap");
        assertFalse(shape.testPoint(new Vec2(2.0f, 2.0f)), "point outside expected not to overlap");

        CastOutput hit = shape.castRay(new Vec2(-5.0f, 0.0f), new Vec2(10.0f, 0.0f));
        assertTrue(hit.hit(), "ray through the box expected to hit");
        assertEquals(-1.0f, hit.point().x(), delta, "hit point expected on the left face");
        assertEquals(-1.0f, hit.normal().x(), delta, "hit normal expected to face left");
        assertEquals(0.4f, hit.fraction(), delta, "hit fraction expected 4/10");
        assertFalse(shape.castRay(new Vec2(-5.0f, 5.0f), new Vec2(10.0f, 0.0f)).hit(), "ray above the box expected to miss");

        // Box2D pads stored bounds by its speculative distance, 4 linear slops
        float margin = 4.0f * Box2D.LinearSlop + delta;
        AABB bounds = shape.bounds();
        assertEquals(-1.0f, bounds.lowerBound().x(), margin, "lower bound x expected about -1");
        assertEquals(-1.0f, bounds.lowerBound().y(), margin, "lower bound y expected about -1");
        assertEquals(1.0f, bounds.upperBound().x(), margin, "upper bound x expected about 1");
        assertEquals(1.0f, bounds.upperBound().y(), margin, "upper bound y expected about 1");
        assertTrue(bounds.lowerBound().x() <= -1.0f && bounds.upperBound().x() >= 1.0f, "bounds expected to contain the box");

        MassData massData = shape.computeMassData();
        assertEquals(4.0f, massData.mass(), delta, "2 by 2 box at density 1 expected mass 4");

        Vec2 closest = shape.closestPoint(new Vec2(3.0f, 0.0f));
        assertEquals(1.0f, closest.x(), delta, "closest point x expected on the right face");
        assertEquals(0.0f, closest.y(), delta, "closest point y expected 0");
    }

    @Test
    @DisplayName("Smoke: a dynamic body with a shape rests on a static ground shape")
    void testRestsOnGround() {
        Body ground = world.createBody(new BodyDef());
        ground.createPolygonShape(new ShapeDef(), Polygon.box(10.0f, 0.5f));
        Body crate = world.createBody(new BodyDef().type(BodyType.DynamicBody).position(0.0f, 3.0f));
        crate.createPolygonShape(new ShapeDef(), Polygon.box(0.5f, 0.5f));
        for (int i = 0; i < 120; i++) world.step(1.0f / 60.0f, 4);
        assertTrue(crate.position().y() > 0.5f, "crate expected not to fall through the ground");
        assertTrue(crate.position().y() < 3.0f, "crate expected to have fallen onto the ground");
    }
}
