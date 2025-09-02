package game;

import java.util.Vector;

import static java.lang.Math.sqrt;

/**
 * Vector2D objects are objects which contain an x and y coordinates, as float.
 * This class has some useful methods to modify dynamically coordinates, either independently or together.
 * Some methods are also static methods used to create new Vector2D objects, either from already existing
 * Vector2D objects or from numbers (or even from nothing). It's made to allow and support a large spectrum of mathematical operations.
 */
public class Vector2D implements Cloneable {
    private float x;
    private float y;

    /**
     * The main constructor for Vector2D objects.
     * @param x x coordinates
     * @param y y coordinates
     */
    public Vector2D(float x,float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * The default constructor for Vector2D objects. Overloads <code>Vector2D(float x, float y)</code>
     * with default values x = 0 and y = 0.
     * @see #Vector2D(float, float)  Vector2D
     */
    public Vector2D() {
        this(0,0);
    }

    /**
     * Pass the coordinates of the vector v to <code>set(float x, float y)</code>.
     * @param v a non-null vector2D which coordinates should be copied
     * @see #set(float, float) 
     */
    public void set(Vector2D v){
        this.set(v.getX(), v.getY());
    }

    /**
     * Set both x and y coordinates.
     * @param x new x coordinate
     * @param y new y coordinate
     * @see #setX(float x)
     * @see #setY(float y)
     * @see #set(Vector2D)
     */
    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }
    public void setX(float x) {
        this.x = x;
    }
    public void setY(float y) {
        this.y = y;
    }
    public float getX() {
        return this.x;
    }
    public float getY() {
        return this.y;
    }
    public void addToX(float howMuchToAdd) {
        this.x += howMuchToAdd;
    }
    public void addToY(float howMuchToAdd) {
        this.y += howMuchToAdd;
    }
    public void multiplyXBy(float factor) {
        this.x *= factor;
    }
    public void multiplyYBy(float factor) {
        this.y *= factor;
    }
    public void add(Vector2D howMuchToAdd) {
        this.x += howMuchToAdd.getX();
        this.y += howMuchToAdd.getY();
    }
    public void add(float x, float y) {
        this.x += x;
        this.y += y;
    }

    public double norm() {
        return sqrt(y * y + x * x);
    }
    public boolean within(Vector2D a, Vector2D b) {
        return (a.getX() <= this.getX() && this.getX() <= b.getX() && a.getY() <= this.getY() && this.getY() <= b.getY());
    }
    public boolean within(float x1, float y1, float x2, float y2) {
        return within(new Vector2D(x1, y1), new Vector2D(x2, y2));
    }

    /**
     * Create a new Vector2D object with the same coordinates (a shallow clone).
     * Overrides <code>Object.clone()</code>, so subclasses can get it too.
     * Simpler than constructing another Vector2D object.
     * @return a shallow clone of the vector object
     */

    @Override
    public Vector2D clone() {
        Vector2D o = null;
        try {
            o = (Vector2D) super.clone();
        } catch (CloneNotSupportedException e) {
            System.out.println(e);
            e.printStackTrace();
        }
        return o;
    }

    /**
     * For displaying purposes only, use {@link #getX()} and {@link #getY()} for the coordinates, as float
     * @return the coordinates of the Vector2D object in the format (x, y)
     * @see #getX()
     * @see #getY()
     */
    public String str() {
        return '(' + x + ", " + y + ')';
    }

    /**
     * Abstract method to get a new zero Vector2D object.
     * @return a new Vector2D object with coordinates (0, 0)
     */
    public static Vector2D zero() {
        return new Vector2D(0,0);
    }
    /**
     * Abstract method for adding operation on vector2D objects. Doesn't modify v1 nor v2.
     * @param v1 a non-null vector2D object
     * @param v2 another non-null vector2D object (v1 and v2 can be the same object)
     * @return a new Vector2D object with coordinates <code>(v1(x) + v2(x), v1(y) + v2(y))</code>.
     */
    public static Vector2D addOperation(Vector2D v1, Vector2D v2) {
        return new Vector2D(v1.getX() + v2.getX(), v1.getY() + v2.getY());
    }

    public static Vector2D addOperation(Vector2D v, float x, float y) {
        return new Vector2D(v.getX() + x, v.getY() + y);
    }

    public static Vector2D multiplyOperation(Vector2D v, float factor) {
        return new Vector2D(v.getX() * factor, v.getY() * factor);
    }

}