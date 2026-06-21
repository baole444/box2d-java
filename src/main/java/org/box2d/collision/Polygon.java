package org.box2d.collision;

import org.box2d.NativeLoader;
import org.box2d.internal.b2Hull;
import org.box2d.internal.b2Polygon;
import org.box2d.internal.b2Vec2;
import org.box2d.internal.nBox2D;
import org.box2d.math.Rot;
import org.box2d.math.Transform;
import org.box2d.math.Vec2;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

/**
 * Solid convex polygon geometry with up to {@value #MaxVertices} vertices and an optional rounding radius.
 * <p>
 * The vertices, normals and centroid are derived by Box2D when the polygon is built, so they are exposed as read only.
 * To construct a polygon, use one of the provided static factory methods.
 * </p>
 * Wrapper for native {@link b2Polygon}.
 */
public final class Polygon {
    /**
     * Maximum number of vertices a polygon may have.
     */
    public static final int MaxVertices = 8;

    private final MemorySegment segment;

    static {
        if (!NativeLoader.isLoaded()) NativeLoader.load();
    }

    /**
     * Wrap an existing {@link b2Polygon} memory segment.
     * The segment is not owned by this {@link Polygon} instance.
     * @param segment the memory segment to wrap
     */
    public Polygon(MemorySegment segment) {
        this.segment = segment;
    }

    /**
     * Create a box polygon, centred at the origin and axis aligned.
     * @param halfWidth the box 1/2 width along the local x-axis
     * @param halfHeight the box 1/2 height along the local y-axis
     * @return a new box {@link Polygon}
     */
    public static Polygon box(float halfWidth, float halfHeight) {
        return new Polygon(nBox2D.nMakeBox(halfWidth, halfHeight));
    }

    /**
     * Create a square polygon, centred at the origin and axis aligned.
     * @param halfWidth the square 1/2 extent along both local axes
     * @return a new square {@link Polygon}
     */
    public static Polygon square(float halfWidth) {
        return new Polygon(nBox2D.nMakeSquare(halfWidth));
    }

    /**
     * Create a box polygon offset by the given centre and rotation.
     * @param halfWidth the box 1/2 width along the local x-axis
     * @param halfHeight the box 1/2 height along the local y-axis
     * @param center the local centre of the box
     * @param rotation the local rotation of the box
     * @return a new box {@link Polygon}
     */
    public static Polygon box(float halfWidth, float halfHeight, Vec2 center, Rot rotation) {
        return new Polygon(nBox2D.nMakeOffsetBox(halfWidth, halfHeight, center.segment(), rotation.segment()));
    }

    /**
     * Create a convex polygon from a set of points. The winding order does not matter,
     * and duplicate or interior points are discarded.
     * @param points the vertices to build the hull from, between 3 and {@value MaxVertices} points
     * @return a new {@link Polygon}
     * @throws IllegalArgumentException if the point count is out of range or the points are malformed
     */
    public static Polygon fromVertices(Vec2... points) {
        int count = points.length;
        if (count < 3 || count > MaxVertices) throw  new IllegalArgumentException(String.format("Polygon requires 3 to %d point, got %d", MaxVertices, count));
        MemorySegment hull;
        try (Arena tmpArena = Arena.ofConfined()) {
            MemorySegment pts = b2Vec2.allocateArray(count, tmpArena);
            long stride = b2Vec2.layout().byteSize();
            for (int i = 0; i < count; i++) MemorySegment.copy(points[i].segment(), 0L, b2Vec2.asSlice(pts, i), 0L, stride);
            hull = nBox2D.nComputeHull(pts, count);
        }
        if (b2Hull.count(hull) == 0) throw new IllegalArgumentException("Polygon points did not form a convex hull");
        return new Polygon(nBox2D.nMakePolygon(hull, 0.0f));
    }

    /**
     * Create a new {@link Polygon} with its values initialized to this polygon's values.
     * @return a new {@link Polygon}
     */
    public Polygon copy() {
        MemorySegment destination = Arena.ofAuto().allocate(b2Polygon.layout());
        MemorySegment.copy(segment, 0L, destination, 0L, b2Polygon.layout().byteSize());
        return new Polygon(destination);
    }

    /**
     * Get the number of the vertices of this polygon.
     * @return the vertex count
     */
    public int count() {
        return b2Polygon.count(segment);
    }

    /**
     * Get the rounding radius of this polygon.
     * @return the radius value, in Radian
     */
    public float radius() {
        return b2Polygon.radius(segment);
    }

    /**
     * Get a copy of the vertex at the given index.
     * @param index the vertex index, in {@code [0, count()]}
     * @return a new {@link Vec2} with the vertex value
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public Vec2 vertex(int index) {
        if (index < 0 || index >= count()) throw new IndexOutOfBoundsException("Vertex index out of range: " + index);
        return new Vec2(b2Polygon.vertices(segment, index)).copy();
    }

    /**
     * Get a copy of this polygon's centroid.
     * @return a new {@link Vec2} with the centroid value
     */
    public Vec2 centroid() {
        return new Vec2(b2Polygon.centroid(segment)).copy();
    }

    /**
     * Check if the given point is inside this polygon's local frame or not.
     * @param point the point to test, in local coordinates
     * @return true if the point is inside this polygon
     */
    public boolean pointInPolygon(Vec2 point) {
        return nBox2D.nPointInPolygon(point.segment(), segment);
    }

    /**
     * Compute the mass properties of this polygon for the given density.
     * @param density the area density in kg/m^2
     * @return a new {@link MassData} for this polygon
     */
    public MassData computeMass(float density) {
        return new MassData(nBox2D.nComputePolygonMass(segment, density));
    }

    /**
     * Compute the bounding box of this polygon when placed by the given transform.
     * @param transform the transform to place this polygon with
     * @return a new {@link AABB} containing this polygon
     */
    public AABB computeAABB(Transform transform) {
        return new AABB(nBox2D.nComputePolygonAABB(segment, transform.segment()));
    }

    /**
     * Get the memory segment of this {@link Polygon}.
     * @return the underlying memory segment
     */
    public MemorySegment segment() {
        return segment;
    }

    @Override
    public String toString() {
        return String.format("Polygon[count=%d, radius=%.3f, centroid=(%.3f, %.3f)]", count(), radius(), centroid().x(), centroid().y());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Polygon polygon)) return false;
        int count = count();
        if (count != polygon.count() || Float.compare(radius(), polygon.radius()) != 0) return false;
        for (int i = 0; i < count; i++) {
            if (!vertex(i).equals(polygon.vertex(i))) return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int count = count();
        int result = prime * count + Float.floatToIntBits(radius());
        for (int i = 0; i < count; i++) result = prime * result + vertex(i).hashCode();
        return result;
    }
}
