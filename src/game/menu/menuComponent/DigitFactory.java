package game.menu.menuComponent;

import java.awt.*;
import java.awt.image.BufferedImage;

public class DigitFactory {
    private static BufferedImage[] digits = new BufferedImage[10];
    public final int size;
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
            digits[i] = new BufferedImage(L, 2 * u + l, BufferedImage.TYPE_INT_ARGB);
            Graphics g = digits[i].createGraphics();


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

    public DigitFactory(int size, String fontColor, String backGroundColor) {
        this(size, Color.decode(fontColor), Color.decode(backGroundColor));
    }

    public DigitFactory(int size) {
        this(size, Color.WHITE, Color.BLACK);
    }

    public BufferedImage getDigit(int n) {
        if (n < 0 || n > 9) throw new IllegalArgumentException("getDigit(int n) only accepts 0 <= n <= 9");
        else return digits[n];
    }

}
