package game;

import java.awt.*;
import static java.lang.Math.min;
import static java.lang.Math.max;

public class Racket implements Renderable, Updatable {
    public float x,y; // left up corners coordinates, not a game.Vector2D object because only y will change
    public final int side;
    public static final int HEIGHT = 100;
    public static final int WIDTH = 10;
    public static final float SPEED = 1f;
    private final int UP_SCROLL_LIMIT, DOWN_SCROLL_LIMIT;

    /**
     * The sole constructor for the Racket object
     * @param side either 1 for left or -1 for right
     */
    public Racket(int side) {
        UP_SCROLL_LIMIT = Ball.RADIUS * 3; // 1.5 * the diameter of the ball
        DOWN_SCROLL_LIMIT = Game.HEIGHT - Ball.RADIUS * 3 - HEIGHT;
        if (side == 1 || side == -1) this.side = side;
        else throw new IllegalArgumentException("the value of the side should be either 1 for the left side or -1 for the right side.");

    }

    @Override
    public void render(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect((int) x, (int) y, WIDTH, HEIGHT);
    }

    @Override
    public void update() {
        // it will just limit the scrolling ability, the scroll itself will be handled by the matchManager
        y = min(DOWN_SCROLL_LIMIT, max(UP_SCROLL_LIMIT, y));
    }

    public Vector2D getCenter() {
        return new Vector2D(x + (float) WIDTH / 2, y + (float) HEIGHT / 2);
    }

}
