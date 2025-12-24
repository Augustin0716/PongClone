package game;

import java.util.Arrays;
import java.util.Random;
import java.util.function.Consumer;

public class ComputerPlayer extends Racket {
    /**
     * This enum declares constants that are used to define how good the computer is at the game.
     */
    public enum Difficulty {THICKHEAD, OKAY, SMART, GOD}

    /**
     * The YFunction interface is a nested interface used to generate an object (stored in
     * {@link ComputerPlayer#YSupplier}) that holds one float value or an array of float values with an extra field that
     * take into account the number of time the method {@link YFunction#getY()} is called.
     * This interface has 2 factory methods, one for the {@code float} case and one for the {@code float[]} case,
     * {@link YFunction#constant} and {@link YFunction#sequence} respectively.
     * The objective of this interface is to avoid declaring fields with only a hypothetical use while conserving the
     * diversity of strategies and encapsulate the logic of the selection of the float value. The result is that the
     * caller of getY() can't make the difference between a constant instance and a sequence instance.
     * @see ComputerPlayer
     */
    private interface YFunction {
        /**
         * The key to uniformity of the YFunction and its contract. Will return one float that the instance holds,
         * depending on the instance.
         * @return a float, depending on the instance : either y for a constant instance or y[i] with i the number of
         * getY() calls divided by 10 for a sequence instance
         */
        float getY();

        /**
         * Generates a YFunction object that will return y. The goal is simply to hold the float and maintain
         * uniformity with the sequence instance.
         * @param y the float that will be stored upon closure and can be accessed via getY()
         * @return a YFunction which method getY() will return {@code y} as is
         */
        static YFunction constant(float y) {
            return () -> y;
        }

        /**
         * Generates a YFunction object that will return y[i] with i being the number of calls of this function divided
         * by 10 (and truncated). It takes care automatically of the index but it's not possible to change the index from
         * outside the instance. If the number of call exceeds y.length * 10, the float at the last index of y is
         * returned.
         * @param y the float sequence
         * @return a YFunction object which method getY() will return {@code y[i]} with i incrementing every 10 calls
         */
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
     * The function objects are here to tidy up codes. Upon construction, the ComputerPlayer chooses strategies
     * depending on its own difficulty setting and register them so they can be used during the match.
     */
    public Consumer<Ball> setTargetY;
    public Runnable computerMove;
    /**
     * Randomness is here to prevent bots from
     *  - being too predictable
     *  - always making the same moves
     *  - being unbeatable (for the GOD difficulty)
     * Only {@link Random#nextGaussian()} is used since its behavior was arbitrarily regarded as the most relevant
     */
    private final Random random = new Random();
    /**
     * This object is here to supply either always the same float or a float of index i from an array, with i increasing
     * with the number of call for the {@link YFunction#getY()} method.
     * This object changes everytime a calculation must be done since what it supplies is defined by closure.
     */
    private YFunction YSupplier;

    public ComputerPlayer(int side, Difficulty difficulty) {
        super(side);
        // sets up the strategies
        switch (difficulty) {
            case THICKHEAD -> {

                computerMove = this::goToTargetY;
                //TODO : delete it, useless

                setTargetY = (ball) -> {
                    Vector2D p = ball.position;
                    Vector2D s = ball.speed;
                    if (calculateHitTime(p.getX(), s.getX()) > 0) {
                    YSupplier = YFunction.sequence(calculateMovements(
                            p,
                            s,
                            0
                    ));
                    } else YSupplier = YFunction.constant(YSupplier.getY()); // just stay where it hit last
                };
            }
            case OKAY -> {

                computerMove = this::goToTargetY;

                setTargetY = (ball) -> {
                    Vector2D p = ball.position;
                    Vector2D s = ball.speed;
                    if (calculateHitTime(p.getX(), s.getX()) > 0)
                        YSupplier = YFunction.sequence(calculateMovements(p, s, 100));
                    else YSupplier = YFunction.constant(Game.HEIGHT / 2f - getOffset()); // goes back to center

                };
            }

            case SMART, GOD -> {

                computerMove = this::goToTargetY;

                setTargetY = (ball) -> {
                    YSupplier = YFunction.constant(
                            calculateBallPosition(
                                    ball.position.getY(),
                                    ball.speed.getY(),
                                    calculateHitTime(
                                            ball.position.getX(),
                                            ball.speed.getX()
                                    )
                            ) - getOffset()
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
        // the offset makes the computer hits the ball on average in the center with a bit of random variation
        float offset = getOffset();
        // time in ticks at which the ball reaches the goal
        int tf = calculateHitTime(position.getX(), speed.getX());
        if (tf < 0) {
            return new float[] {Game.HEIGHT / 2f - offset};
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
                positions[i] = calculateBallPosition(y0, vy, t) - offset;
            }
        }
        else Arrays.fill(positions, calculateBallPosition(y0, vy, tf) - offset);
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

    private float getOffset() {
        return (float) ((1 + random.nextGaussian() * 0.5) * HEIGHT / 2);
    }
}
