package game;

import java.util.Arrays;
import java.util.Random;
import java.util.function.Consumer;

public class ComputerPlayer extends Racket {
    /**
     * This enum declares constants that are used to define how good the computer is at the game.
     */
    public enum Difficulty {THICKHEAD, OKAY, SMART, GOD}
    private interface YFunction {
        float getY();
        static YFunction constant(float y) {
            return () -> y;
        }
        static YFunction sequence(float[] y) {
            return new YFunction() {
                private int tick = 0;
                @Override
                public float getY() {
                    int i = Math.min(tick++ / 10, y.length - 1);
                    return y[i];
                }

            };
        }
    }
    /**
     * The functions are here to tidy up codes. Upon construction, the ComputerPlayer chooses strategies depending on its
     * own difficulty setting and register them so they can be used during the match.
     */
    public Consumer<Ball> setTargetY;
    public Runnable computerMove;
    /**
     * Only used for the lower difficulties, it allows the bot to have a notion of time
     */
    private int internalClock;
    private final Difficulty difficulty;
    private final Random random = new Random();
    private YFunction YSupplier;
    public ComputerPlayer(int side, Difficulty difficulty) {
        super(side);
        this.difficulty = difficulty;
        // sets up the strategies
        switch (difficulty) {
            case THICKHEAD, OKAY -> {

                computerMove = () -> {
                    goToTargetY();
                };

                int cap = (difficulty == Difficulty.THICKHEAD)? 0:100;
                setTargetY = (ball) -> {
                    YSupplier = YFunction.sequence(calculateMovements(
                            ball.position,
                            ball.speed,
                            cap
                    ));
                };
            }

            case SMART, GOD -> {

                computerMove = () -> {
                    goToTargetY();
                };

                setTargetY = (ball) -> {
                    YSupplier = YFunction.constant(
                            calculateBallPosition(
                                    ball.position.getY(),
                                    ball.speed.getY(),
                                    calculateHitTime(
                                            ball.position.getX(),
                                            ball.speed.getX()
                                    )
                            )
                    );
                };
            }
        }
    }

    public void goToTargetY() {
        if (y < YSupplier.getY()) y += SPEED;
        if (y > YSupplier.getY()) y -= SPEED;
    }


    private float[] calculateMovements(Vector2D position, Vector2D speed, int capCalculations) {
        float y0 = position.getY();
        float vy = speed.getY();
        // the offSet makes the computer hits the ball on average in the center with a bit of random variation
        float offSet = (float) ((1 + random.nextGaussian() * 0.5) * HEIGHT / 2);
        // time in ticks at which the ball reaches the goal
        int tf = calculateHitTime(position.getX(), speed.getX());
        if (tf < 0) {
            return new float[] {Game.HEIGHT / 2f - offSet};
            // quick fix for the negative array size issue, might be replaced in the future
        }
        // the length of the positions should be able to run 1/10 of the ticks between t = 0 and t = tf, and we add 1 to
        // ensure it's enough with the rounds up
        float[] positions = new float[tf / 10 + 1];
        /*
        this loop justifies the use of an array : the goal is to make the
        computer look more hesitant and human-like when it can't calculate
        far enough. It also gives it a chance without running yet another
        calculation. capCalculations is actually how far in the future
        the computer can foresee, in pixels.
         */
        if (tf > capCalculations) {
            float t;
            float vx = speed.getX();
            for(int i = 0; i < positions.length; i++) {
                t = Math.min(
                        capCalculations / vx + i * 10,
                        tf
                );
                positions[i] = calculateBallPosition(y0, vy, t) - offSet;
            }
        }
        else Arrays.fill(positions, calculateBallPosition(y0, vy, tf) - offSet);
        return positions;
    }

    /**
     * Calculates the position of the ball with the bounces at the time t.
     * Works with negative time, theoretically (although that might not be very useful).
     * @param y0 y position at t = 0
     * @param vy vertical speed of the ball
     * @param t time in tick, t = 0 being the current tick
     * @return y(t), with (exact) calculations of the bounces.
     */
    private float calculateBallPosition(float y0, float vy, float t) {
        // the distance of the height accessible to the center of the ball
        float h = Game.HEIGHT - 2 * Ball.RADIUS;
        // the period of the bounce (at the end of which the ball is at the same y with the same speed as
        // at the beginning)
        float p = 2 * h;
        float yDist = y0 + vy * t;
        float m = yDist % p;
        if (m < 0) m += p;
        float yInside = (m <= h) ? m : (p - m);
        // we add the radius of the ball because the calculations is off by the radius of the ball
        return yInside + Ball.RADIUS;
    }

    /**
     * Calculates at what time the ball will hit the goal of this (in ticks).
     * @param x0 the x coord of the ball, currently
     * @param vx the horizontal velocity of the ball
     * @return an int that represents the number of ticks before the ball hits the goal
     */
    private int calculateHitTime(float x0, float vx) {
        float t = (x - x0 + Ball.RADIUS * side) / vx;
        return (int) t;
    }

    //TODO : create a method for a defensive strategy in case the ball is headed to the other side
}
