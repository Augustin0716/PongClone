package game;

import java.awt.*;
import java.awt.image.BufferedImage;

import static java.lang.Math.*;

public class Ball implements Renderable, Updatable {
    //TODO : might be good to set the Vector2D private and use getters
    public final Vector2D position = new Vector2D(0,0);
    public final Vector2D speed = new Vector2D(0,0);
    public static final int RADIUS = 10;
    public final MatchManager master;
    public final Racket player1, player2;
    private final BufferedImage sprite;

    public Ball(MatchManager master, Racket player1, Racket player2) {
        this.master = master;
        this.player1 = player1;
        this.player2 = player2;
        sprite = new BufferedImage(RADIUS * 2, RADIUS * 2, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) sprite.getGraphics();
        g.setPaint(new RadialGradientPaint(
                RADIUS / 2f,
                RADIUS / 2f,
                RADIUS,
                new float[]{0f, 0.4f, 1f},
                new Color[]{Color.WHITE, new Color(205,205,205), new Color(155, 155, 155)},
                MultipleGradientPaint.CycleMethod.REFLECT
        ));
        g.fillOval(0,0, RADIUS * 2, RADIUS * 2);
        g.dispose();
    }

    private void testCollisions() {
        // upper wall
        if (this.position.getY() < RADIUS) {
            speed.setY(this.speed.getY() * -1);
            position.setY(RADIUS);
            return;
        }
        // inferior wall
        if (this.position.getY() > Game.HEIGHT - RADIUS) {
            speed.setY(speed.getY() * -1);
            position.setY(Game.HEIGHT - RADIUS);
        }

        float halfHeight = (float) Racket.HEIGHT / 2;
        float halfWidth = (float) Racket.WIDTH / 2;
        // rackets
        for (Racket player : new Racket[] {player1, player2}) {
            Vector2D c = player.getCenter();

            // gets the distances and if it's too far for a collision to happen, we skip
            float dx = abs(c.getX() - position.getX());
            if (dx >= halfWidth + RADIUS) continue;
            float dy = abs(c.getY() - position.getY());
            if (dy >= halfHeight + RADIUS) continue;

            // easy cases where the collisions are obvious geometrically
            if (dx < halfWidth || dy < halfHeight) {
                handleRacketCollision(c, player.side);
                return;
            }
            // trickier case when the ball is in a "collision corner"
            float cornerDistanceSq = (dx - halfWidth)*(dx - halfWidth) + (dy - halfHeight)*(dy - halfHeight);
            if (cornerDistanceSq < RADIUS * RADIUS) {
                handleRacketCollision(c, player.side);
                return;
            }
        }
    }

    private void handleRacketCollision(Vector2D racketCenter, int side) {
        // the ball takes an angle depending on where it hits : 0 at the center, 60° at the edge
        double angle = toRadians(60 * 2 * (position.getY() - racketCenter.getY()) / Racket.HEIGHT);
        // the norm is used so the ball's speed feels the same as before
        double norm = speed.norm();

        // some speed is added for each collision, so the game gets more difficult
        norm += min(norm * 0.05, 1);
        // the norm is "redistributed" to the coordinates depending on the angle
        speed.set((float) (norm * cos(angle)), (float) (norm * sin(angle)));
        // we get the ball back outside the racket to avoid a collision mayhem
        float cx = racketCenter.getX();
        position.setX(cx + ((float) Racket.WIDTH / 2 + RADIUS) * side);
        /*
        the speed is always positive at the end of the calculations, so we multiply X by 1
        or -1 to make it go toward the right side (which can be the left side !)
        */
        speed.multiplyXBy(side);

        // we update the computer once, for performance
        for (Racket player : new Racket[] {player1, player2}) {
            if (player instanceof ComputerPlayer) ((ComputerPlayer) player).setTargetY.accept(this);
        }
    }

    @Override
    public void render(Graphics g) {
        //g.setColor(Color.WHITE);
        //g.fillOval((int) this.position.getX() - RADIUS, (int) this.position.getY() - RADIUS, RADIUS * 2, RADIUS * 2);
        g.drawImage(sprite, (int) position.getX() - RADIUS, (int) position.getY() - RADIUS, null);
    }

    @Override
    public void update() {
            this.position.add(speed);
            testCollisions();
    }

    public int touchDown() {
        if (this.position.getX() <= RADIUS) return -1;
        if (this.position.getX() >= Game.WIDTH - RADIUS) return 1;
        else return 0;
    }

}
