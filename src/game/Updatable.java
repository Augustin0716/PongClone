package game;

/**
 * A functionnal interface for the method <code>void update()</code>. It doesn't take any argument so the class must get everything it needs to update itself, and doesn't return anything either.
 * To use this method efficiently, it should be called from high responsibility objects to lower responsibility objects, with the highest responsibility object run in a Thread.
 * For instance <code>MainObject implements Updatable</code> should call in its own <code>update()</code> method the <code>InnerObject implements Updatable</code>'s <code>update()</code> method, and so on.
 *
 */
@FunctionalInterface
public interface Updatable {
    void update();
}
