package game;

/**
 * A functional interface for the method {@code public void update()}. It doesn't take any argument so the class must get
 * everything it needs to update itself, and doesn't return anything either. To use this method efficiently, it should
 * be called from high responsibility objects to lower responsibility objects, with the highest responsibility object
 * running in a Thread. For instance {@code MainObject implements Updatable} should call in its own <code>update()</code>
 * method the {@code InnerObject implements Updatable}'s {@code update()} method, and so on as such :
 * <pre>{@code
 * // MainObject implements Runnable
 * public void update() { //called from a main loop in a thread
 *     this.changeStuff(); // the mainObject updates something
 *     innerObject.update();
 *     // the place of the line above might influence the update of innerObject, therefore it's something to consider
 *     this.changeOtherStuff(); // the mainObject updates something else
 * }
 * // InnerObject
 * public void update() {
 *     this.moveStuff();
 *     // since it doesn't hold any object that must be updated, it just update itself
 * }}</pre>
 *
 */
@FunctionalInterface
public interface Updatable {
    void update();
}
