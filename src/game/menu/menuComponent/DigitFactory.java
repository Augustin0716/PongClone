package game.menu.menuComponent;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * A class that generates and hold BufferedImage objects representing 7-segments digits. It generates it upon
 * initializing and {@link #getDigit(int)} is the method used to get them. Once created, the images cannot be changed
 * directly from the object, it's best to create yet another one for another "set" of 7-segments digits with another
 * style. The Counter class rely on this class to get its numbers.
 * @see #DigitFactory(int, Color, Color)  DigitFactory
 */
public class DigitFactory {
    private final BufferedImage[] digits = new BufferedImage[10];
    public final int size;

    /**
     * Main constructor of the class. It generates the digits, so they're directly ready to go and
     * accessible via {@link #getDigit(int)}.
     * @param size int for scaling the images, which dimensions will be (10*size)x(17*size) measured in pixels
     * @param fontColor the color of the digits, as AWT Color objects
     * @param backGroundColor the color of the background, as AWT Color objects
     */
    public DigitFactory(int size, Color fontColor, Color backGroundColor) {
        this.size = size;
        int L = 10 * size;
        int l = 3 * size;
        int u = L - l;
        /*
        L for longueur in French, (meaning height, but it means I have to put height in width)
        which is the longer side of the rectangle
        l for largeur in French, (meaning width, but it means I have to put width in height)
        which is the shorter side of the rectangle
        u for units. will be used to signal we're moving one segment to the right (if in x)
        or one segment down (if in y) or 2 (if 2*u in y, for the bottom segment). The segments
        will intersect with one another in certain cases but this is an arbitrary choice.
         */
        boolean[][] segments = {
                // in order : top, upper-left, upper-right, middle, lower-left, lower-right, bottom
                {true, true, true, false, true, true, true}, //0 every one except middle
                {false, false, true, false, false, true, false}, // 1 UR + LR
                {true, false, true, true, true, false, true}, // 2 T + UR + M + LL + B
                {true, false, true, true, false, true, true}, // 3 T + UR + M + LR + B
                {false, true, true, true, false, true, false}, // 4 UL + UR + M + LR
                {true, true, false, true, false, true, true}, // 5 T + UL + M + LR + B
                {true, true, false, true, true, true, true}, // 6 T + UL + M + LL + LR + B
                {true, false, true, false, false, true, false}, // 7 T + UR + LR
                {true, true, true, true, true, true, true,}, // 8 every one
                {true, true, true, true, false, true, false} //9 T + UL + UR + M + LR
        };

        for(int i = 0; i < 10; i++) {
            // The size is exactly the space needed for all the segments to fit in the image
            digits[i] = new BufferedImage(L, 2 * u + l, BufferedImage.TYPE_INT_ARGB);
            Graphics g = digits[i].createGraphics();

            if (backGroundColor != null) {
                g.setColor(backGroundColor);
                g.fillRect(0, 0, L, 2 * u + l);
            }

            g.setColor(fontColor);

            if (segments[i][0]) g.fillRect(0,0, L, l);
            if (segments[i][1]) g.fillRect(0,0, l, L);
            if (segments[i][2]) g.fillRect(u, 0, l, L);
            if (segments[i][3]) g.fillRect(0, u, L, l);
            if (segments[i][4]) g.fillRect(0, u, l, L);
            if (segments[i][5]) g.fillRect(u, u, l, L);
            if (segments[i][6]) g.fillRect(0, 2*u, L, l);
        }
    }

    /**
     * Constructor that takes colors as Strings. They should be in the hex color format : "#XXXXXX"
     * to work properly. For no background (transparent), backGroundColor can be <code>null</code>.
     * @param size int for scaling the images, which dimensions will be (10*size)x(17*size) measured in pixels
     * @param fontColor the color of the digits, as a String in hex color format
     * @param backGroundColor the color of the background, as a String in hex color format
     * @see #DigitFactory(int, Color, Color)  DigitFactory
     */
    public DigitFactory(int size, String fontColor, String backGroundColor) {
        this(size, Color.decode(fontColor), (backGroundColor != null)? Color.decode(backGroundColor):null);
    }

    /**
     * Minimalist constructor that only takes the size as an argument. The digits will be white and there will be no
     * background.
     * @param size int for scaling the images, which dimensions will be (10*size)x(17*size) measured in pixels
     */
    public DigitFactory(int size) {
        this(size, Color.WHITE, null);
    }

    public BufferedImage getDigit(int n) {
        if (n < 0 || n > 9) throw new IllegalArgumentException("getDigit(int n) only accepts 0 <= n <= 9");
        else return digits[n];
    }

}
