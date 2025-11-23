package game.test;

import java.awt.Polygon;
import java.awt.geom.Arc2D;
import java.awt.geom.Path2D;
import java.lang.Math;
import java.awt.geom.Area;

/**
 * A polygon subclass made to generate rounded rectangle shapes. Since this is a complicated shape to make, this class
 * allows for easy creation of such an object and display using Graphics.drawPolygon(Polygon p) or Graphics2D.fill(Shape s)
 */
public class RoundedRectangle {

    /**
     * Main static method to generate a rounded rectangle. Is overloaded (so you don't have to specify a quality)
     * @param x the x position of the top-left corner of the rectangle that exactly contains the shape.
     * @param y the y position of the top-left corner of the rectangle that exactly contains the shape.
     * @param width how wide the shape is on the x-axis
     * @param length how wide the shape is on the y-axis
     * @param roundedProportion how rounded the corner is : 0 makes the shape a pointy rectangle and 1 makes the closest
     *                          corners linked by a semicircle (like a sausage)
     * @param quality how many vertices the corners will have to make a rounded shape, the higher, the better visually
     *                (but worse for your CPU !). A generally good value is 10 (mathematically calculated).
     * @return a Polygon object that is a rounded rectangle with the specified params
     *
     */
    public static Polygon getRoundedRectAsPolygon(int x, int y, int width, int length, float roundedProportion, int quality) {
        // Secures the parameters
        if (roundedProportion < 0 || roundedProportion > 1) {
            throw new IllegalArgumentException("`roundedProportion` can take only values between 0 and 1.");
        }
        if (quality < 0) throw new IllegalArgumentException("`quality` must be superior or equal to 0");

        int nPoints = 8 + quality * 4;
        int[] xPoints = new int[nPoints];
        int[] yPoints = new int[nPoints];
        int nbPtPerAngle = quality + 2;
        float radius = roundedProportion * Math.min(length, width) / 2;
        double HALF_PI = Math.PI / 2;
        // The y coordinates of the focal points
        float Y_N = y + radius;
        float Y_S = y + length - radius;
        // The x coordinates of the focal points
        float X_W = x + radius;
        float X_E = x + width - radius;

        for (int i = 0; i < nbPtPerAngle; i++) { // one loop to create them all
            /*
            For optimization purposes, it has been chosen to generate 4 points for each loop for each corner, since the
            angle increment (the double `angle`) is always the same. The points are generated from the highest point on
            the north-east of the shape and then counter clock-wise.
             */
            double angle = HALF_PI * i / (nbPtPerAngle - 1);

            xPoints[0 * nbPtPerAngle + i] = (int) (X_W + radius * Math.cos(angle + 1 * HALF_PI)); // NW quadrant
            yPoints[0 * nbPtPerAngle + i] = (int) (Y_N - radius * Math.sin(angle + 1 * HALF_PI));

            xPoints[1 * nbPtPerAngle + i] = (int) (X_W + radius * Math.cos(angle + 2 * HALF_PI)); // SW quadrant
            yPoints[1 * nbPtPerAngle + i] = (int) (Y_S - radius * Math.sin(angle + 2 * HALF_PI));

            xPoints[2 * nbPtPerAngle + i] = (int) (X_E + radius * Math.cos(angle + 3 * HALF_PI)); // SE quadrant
            yPoints[2 * nbPtPerAngle + i] = (int) (Y_S - radius * Math.sin(angle + 3 * HALF_PI));

            xPoints[3 * nbPtPerAngle + i] = (int) (X_E + radius * Math.cos(angle + 4 * HALF_PI)); // NE quadrant
            yPoints[3 * nbPtPerAngle + i] = (int) (Y_N - radius * Math.sin(angle + 4 * HALF_PI));
        }

        return new Polygon(xPoints, yPoints, nPoints);
    }

    /**
     * A method to generate rounded rectangles without having to specify a quality. This one overloads
     * getRoundedRectAsPolygon(x, y, width, length, roundedProportion, quality) and will choose the quality so the
     * number of vertices is balanced between aesthetic and resource consumption.
     *
     * @param x the position of the top side of the shape
     * @param y the position of the left side of the shape
     * @param width how wide the shape is on the x-axis
     * @param length how wide the shape is on the y-axis
     * @param roundedProportion how pointy or rounded the shape is. Should be a float between 0 and 1 (0 = pointy)
     * @return a Polygon object that is a rounded rectangle with specified values and a computer-chosen quality.
     * @see Polygon
     */
    public static Polygon getRoundedRectAsPolygon(int x, int y, int width, int length, float roundedProportion) {
        final int MIN_THRESHOLD = 2;
        final int S_LIMIT = 10;
        final int M_LIMIT = 80;
        int quality;
        float radius = roundedProportion / 2 * Math.min(width, length);
        if (radius <= S_LIMIT) {
            quality = Math.max((int) radius, MIN_THRESHOLD);
        } else {
            if (radius <= M_LIMIT) {
                quality = 10;
            } else {
                quality = Math.max(10, (int) (Math.PI / (4 * Math.acos(1 - 0.5 * Math.pow(radius,0.5)/ radius))));
            }
        }
        return getRoundedRectAsPolygon(x, y, width, length, roundedProportion, quality);
    }

    /**
     *
     * @param x the
     * @param y
     * @param width how wide the shape is on the x-axis
     * @param length how wide the shape is on the y-axis
     * @return a Polygon object that is a rounded rectangle with the specified values and a rounded proportion of 0.5
     */
    public static Polygon getRoundedRectAsPolygon(int x, int y, int width, int length) {
        return getRoundedRectAsPolygon(x, y, width, length, 0.5f);
    }

    public static Area getRoundedRectAsArea(float x, float y, float width, float height, float roundedProportion) {
        Path2D path = new Path2D.Float();

        final float r = roundedProportion * Math.min(width, height);
        float south = y + height;
        float east = x + width;

        path.moveTo(x + r, y);
        path.append(new Arc2D.Float(x, y, r, r, 90, 90, Arc2D.OPEN), true); //NW corner
        path.append(new Arc2D.Float(x, south - r, r, r, 180, 90, Arc2D.OPEN), true); //SW corner;
        path.append(new Arc2D.Float(east - r, south - r, r, r, 270, 90, Arc2D.OPEN), true); //SE corner
        path.append(new Arc2D.Float(east - r, y, r, r, 0, 90, Arc2D.OPEN), true); // NE corner
        path.closePath();

        return new Area(path);
    }

    public static Area getHollowRoundedRect(float x, float y, float width, float height, float roundedProportion, int thickness) {
        Area outer = getRoundedRectAsArea(x, y, width, height, roundedProportion);
        Area inner = getRoundedRectAsArea(x + thickness, y + thickness, width - 2 * thickness, height - 2 * thickness, roundedProportion);
        outer.subtract(inner);
        return outer;
    }

    public static Area getHollowRoundedRect(float x, float y, float width, float height, float roundedProportion, float thickProportion) {
        int thickness = (int) (Math.min(width, height) * thickProportion);
        return getHollowRoundedRect(x, y, width, height, roundedProportion, thickness);
    }
}
