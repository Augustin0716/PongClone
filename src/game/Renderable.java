package game;

import java.awt.Graphics;

/**
 * This functional interface should be used on objects that should be rendered on an AWT component. It takes as
 * argument the Graphics object from said AWT component. The way it should be implemented is : if a class is an AWT
 * component subclass (say a Canvas subclass) implementing Renderable, <code>render(Graphics g)</code> should generate
 * g (while ignoring the parameter Graphics) and pass it down the objects it holds. Let's take for instance a Game
 * object that holds a MatchManager object that itself holds a Ball object and 2 Racket objects, the way
 * <code>render(Graphics g)</code> should be used is :
 * <pre>{@code
 * // Game extends Canvas
 * public void render(Graphics unused) { // the parameter is not used
 *     bs = getBufferStrategy();
 *     Graphics g = bs.getDrawGraphics();
 *     // the Graphics object is generated
 *     matchManager.render(g);
 *     g.dispose();
 *     bs.show();
 * }
 * // MatchManager
 * public void render(Graphics g) {
 *     g.drawImage(backGround, 0, 0, xMax, yMax);
 *     // the earlier the draw, the further back it is, so we start with the background
 *     ball.render(g);
 *     racket1.render(g);
 *     racket2.render(g);
 *     // then the objects are drawn, on the foreground
 * }
 * // Ball
 * public void render(Graphics g) {
 *     g.setColor(Color.WHITE); // don't forget about colors
 *     g.fillOval(x - RADIUS, y - RADIUS, x + RADIUS, y + RADIUS);
 *     // since it doesn't hold any object that should be rendered, Ball just render itself (a circle)
 * }
 * // Racket
 * public void render(Graphics g) {
 *     g.setColor(Color.WHITE);
 *     g.fillRect(x, y, x + WIDTH, y + HEIGHT);
 * }}</pre>
 */
@FunctionalInterface
public interface Renderable {
    void render(Graphics g);
}
