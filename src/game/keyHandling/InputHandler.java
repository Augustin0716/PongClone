package game.keyHandling;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.util.Arrays;

import java.awt.Component;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import game.Updatable;

/**
 * Returns for each action whether the keys bound to this action are pressed.
 * It extends KeyListener however {@code public void keyTyped} is not regarded.
 * {@code public boolean actionActivated(action)} is the go-to method of this class, but for actions that require to be
 * triggered for a single tick, {@code public boolean actionJustPressed(action)} can be used, and {@code public boolean
 * actionJustReleased} is used to detect the first tick the action has been released.
 * When constructed, it adds itself to the java awt component chosen as master.
 * It works by using an Enum class that implements InputActions, referred to as an "action mapping".
 * See InputActions for more details about this kind of Enum.
 * @param <E> an Enum class that implements InputAction used as an action mapping
 */
public class InputHandler<E extends Enum<E> & InputActions> implements KeyListener, Updatable {

    private final Queue<KeyAction> eventQueue = new ConcurrentLinkedQueue<>();
    private final int INPUT_LENGTH = KeyEvent.KEY_LAST + 1;
    private final boolean[] pressedKeysArray = new boolean[INPUT_LENGTH];
    private final boolean[] previous = new boolean[INPUT_LENGTH];

    private record KeyAction(int keyCode, boolean pressed) {
    }

    /**
     * The sole constructor of the class. It adds itself to the Component directly, so it's ready right away.
     * @param master an awt Component subclass (that can call {@code addKeyListener(KeyListener I)})
     */
    public InputHandler(Component master) {
        master.addKeyListener(this);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        eventQueue.add(new KeyAction(e.getKeyCode(), false));
    }

    @Override
    public void keyPressed(KeyEvent e) {
        eventQueue.add(new KeyAction(e.getKeyCode(), true));
    }

    @Override
    public void keyTyped(KeyEvent e) {

    } // Not used


    /**
     * Copies the array {@code pressedKeysArray}, which contains only the keys pressed this tick into
     * {@code previous}, which contains the keys pressed last tick and then process every key event received this tick.
     * In other words, we keep in memory the keys pressed within the tick that just ended then keep in record the keys
     * pressed within this tick, which allows the methods {@code actionJustPressed(Enum)} and
     * {@code actionJustReleased(Enum)} to work properly. This method is like the timekeeper of this class,
     * allowing it to make the difference between "now" and "before".
     * @see #actionJustPressed(Enum)
     * @see #actionJustReleased(Enum)
     */
    @Override
    public void update() {
        System.arraycopy(pressedKeysArray, 0, previous, 0, INPUT_LENGTH);
        //previous = pressedKeysArray.clone();
        //TODO : choose the better option

        KeyAction action;
        while ((action = eventQueue.poll()) != null) {
            if (action.keyCode >= 0 && action.keyCode < INPUT_LENGTH) {
                pressedKeysArray[action.keyCode] = action.pressed;
            }
        }
    }

    /**
     * The main gateway between the inputs and the logic.<p> The goal of this method is <b>not</b> to check whether a
     * specific key is pressed (although possible), but rather to see whether an action performed by a keyboard input
     * should be executed. For instance, if {@code RUNNING} is performed by pressing the {@code →} key or the
     * {@code D} key, {@code actionActivated(RUNNING)} will be true if :
     * <ul>
     * <li>D is pressed</li>
     * <li>→ is pressed</li>
     * <li> both D and → are pressed.</li>
     * </ul>
     * You can test whether an action is not activated by just using the logical not as such :<br>
     * {@code if(!actionActivated(RUNNING)) {...} // returns true if D and → are not pressed}.
     * @param action an Enum instance of E as defined in {@link #InputHandler}
     * @return true if at least one of the keys performing the action is pressed, else false
     * @see #actionJustPressed(Enum)
     * @see #InputHandler
     */
    public boolean actionActivated(E action) {
        return Arrays.stream(action.getKeyCodes()).anyMatch(key -> pressedKeysArray[key]);
    }

    /**
     * While {@link #actionActivated(Enum)} checks if one of the action's keys is pressed, this method checks if it
     * wasn't the case last tick. For instance, if 'SPACE' is triggered by the space bar, <code>actionJustPressed</code>
     * will return true for SPACE only the first tick space is pressed, then false until space is released for at least
     * one tick and pressed again.
     * To make it short : <i>just pressed</i> = pressed, but only the first tick of the press.
     * @param action an action that is a subclass of E as defined in {@link #InputHandler}
     * @return true if one of the keys performing the action is just pressed, else false
     * @see #actionActivated(Enum)
     * @see #actionJustReleased(Enum)
     * @see #InputHandler
     */
    public boolean actionJustPressed(E action) {
        boolean pressedLastTick = Arrays.stream(action.getKeyCodes()).anyMatch(key -> previous[key]);
        boolean pressedThisTick = Arrays.stream(action.getKeyCodes()).anyMatch(key -> pressedKeysArray[key]);
        return !pressedLastTick && pressedThisTick;
    }

    /**
     * Works just as {@link #actionJustPressed(Enum)} but for this method, it returns true for this action if the keys
     * for said actions are <i>just released</i>. For instance, if <code>SPACE</code> is triggered by the space bar,
     * <code>actionJustReleased(SPACE)</code> will return true for the first tick of the action not being activated.
     * To make it short : <i>just released</i> = released, but only for the first tick.
     * @param action an enum subclass of E as defined in {@link #InputHandler}
     * @return true if all keys for action are just released, else false
     * @see #actionJustPressed(Enum)
     * @see #InputHandler
     */
    public boolean actionJustReleased(E action) {
        boolean pressedLastTick = Arrays.stream(action.getKeyCodes()).anyMatch(key -> previous[key]);
        boolean pressedThisTick = Arrays.stream(action.getKeyCodes()).anyMatch(key -> pressedKeysArray[key]);
        return pressedLastTick && !pressedThisTick;
    }

}
