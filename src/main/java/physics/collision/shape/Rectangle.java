package physics.collision.shape;

import org.joml.Vector2f;
import physics.collision.CollisionUtil;
import util.Pair;

/**
 * <h1>Azurite</h1>
 * <p>
 * The GJKSM shape implementation of a rectangle.
 *
 * @author Juyas
 * @version 19.06.2021
 * @since 19.06.2021
 */
public class Rectangle extends Shape {

    private final Vector2f[] relatives;
    private Vector2f[] absolutes;

    private final Vector2f relativeCentroid;
    private Vector2f absoluteCentroid;

    private final Circle boundingSphere;

    public Rectangle(Vector2f a, Vector2f b, Vector2f c, Vector2f d) {
        this.relatives = CollisionUtil.convexHull(new Vector2f[]{new Vector2f(a), new Vector2f(b), new Vector2f(c), new Vector2f(d)});
        this.absolutes = new Vector2f[4];
        this.relativeCentroid = CollisionUtil.polygonCentroid(this.relatives);
        this.boundingSphere = new Circle(relativeCentroid, CollisionUtil.boundingSphere(relativeCentroid, relatives));
    }

    @Override
    public void adjust() {
        for (int i = 0; i < 4; i++) {
            absolutes[i] = position().add(relatives[i], new Vector2f());
        }
        absoluteCentroid = position().add(relativeCentroid, new Vector2f());
        this.boundingSphere.setPosition(position());
    }

    public Vector2f[] getAbsolutePoints() {
        return absolutes;
    }

    @Override
    public Vector2f centroid() {
        return absoluteCentroid;
    }

    @Override
    public Vector2f reflect(Vector2f centroid, Vector2f collisionRay) {
        Pair<Vector2f, Vector2f> normals = CollisionUtil.collisionEdgeNormals(this.absolutes, this.absoluteCentroid, centroid);
        if (normals.getLeft().dot(collisionRay) >= 0) {
            //System.out.println("chose: " + normals.getLeft() + " for " + collisionRay);
            return CollisionUtil.planeReflection(normals.getLeft(), collisionRay);
        } else {
            //System.out.println("chose: " + normals.getRight() + " for " + collisionRay);
            return CollisionUtil.planeReflection(normals.getRight(), collisionRay);
        }
    }

    @Override
    public Circle boundingSphere() {
        return boundingSphere;
    }

    @Override
    public Vector2f supportPoint(Vector2f v) {
        return CollisionUtil.maxDotPoint(absolutes, v);
    }
}