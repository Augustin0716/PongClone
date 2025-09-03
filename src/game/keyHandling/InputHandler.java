package game.keyHandling;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.util.Arrays;

import java.awt.Component;
import game.Updatable;

/**
 * Returns for each action whether the keys bound to this action are pressed.
 * It extends KeyListener however <code>public void keyTyped</code> doesn't do anything.
 * <code>public boolean actionActivated(action)</code> should be used for the class to work properly.
 * When constructed, it adds itself to the java awt component chosen as master.
 * It works by using an Enum class that implements InputActions, referred to as an "action mapping".
 * See InputActions for more details about this kind of Enum.
 * @param <E> an Enum class that implements InputAction used as an action mapping
 */
public class InputHandler<E extends Enum<E> & InputActions> implements KeyListener, Updatable {

    private final int INPUT_LENGTH = KeyEvent.KEY_LAST + 1;
    private final boolean[] pressedKeysArray = new boolean[INPUT_LENGTH];
    private final boolean[] previous = new boolean[INPUT_LENGTH];
    private final boolean[] current = new boolean[INPUT_LENGTH];

    /**
     * The sole constructor of the class. It adds itself to the Component directly, so it's ready right away.
     * @param game an awt Component subclass (that can call <code>addKeyListener(KeyListener I)</code>)
     */
    public InputHandler(Component game) {
        game.addKeyListener(this);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        toggleKey(e, false);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        toggleKey(e, true);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    } // Not used

    /**
     * Tests whether the code is acceptable (actually if it just fits in the arrays) and modify the state of the key in
     * pressedKeysArray and current (the array responsible for {@link #actionJustPressed(Enum)} and
     * {@link #actionJustReleased(Enum)} working properly). This method is here as a security measure and a way not to
     * repeat code, even on 2 lines.
     * @param e the KeyEvent transmitted from the methods keyPressed and keyReleased
     * @param pressed a boolean telling the state of the key : true -> the key is pressed, false -> the key is released
     * @see #actionActivated(Enum)
     * @see #actionJustPressed(Enum)
     * @see #actionJustReleased(Enum)
     */
    public void toggleKey(KeyEvent e, boolean pressed) {
        int code = e.getKeyCode();
        if (code >= 0 && code < pressedKeysArray.length) {
            pressedKeysArray[code] = pressed;
            current[code] = pressed;
        }
    }

    /**
     * Copies the array <code>current</code>, which contains only the keys pressed this tick into <code>previous</code>,
     * which contains the keys pressed last tick.
     * In other words, we keep in memory the keys pressed within the tick that just ended, which allows the methods
     * <code>actionJustPressed(Enum)</code> and <code>actionJustReleased(Enum)</code> to work properly.
     * This method is like the timekeeper of this class, allowing it to make the difference between "now" and "before".
     * @see #actionJustPressed(Enum)
     * @see #actionJustReleased(Enum)
     */
    @Override
    public void update() {
        System.arraycopy(current, 0, previous, 0, KeyEvent.KEY_LAST);
        Arrays.fill(current, false); // should I do this though ? It seems like it can mess things up
    }

    /**
     * The main gateway between the inputs and the logic.<p> The goal of this method is <b>not</b> to
     * check whether a specific key is pressed (although possible), but rather to see whether an
     * action performed by a keyboard input should be executed. For instance, if <code>RUNNING</code>
     * is performed by pressing the <i>→</i> key or the <i>D</i> key, <code>actionActivated(RUNNING)</code>
     * will be true if :
     * <ul>
     * <li>D is pressed</li>
     * <li>→ is pressed</li>
     * <li> both D and → are pressed.</li>
     * </ul>
     * You can test whether an action is not activated by just using the logical not as such :
     * <code>!actionActivated(RUNNING) // returns true if JUMP is not triggered</code>.
     * @param action an Enum subclass of E as defined in {@link #InputHandler}
     * @return true if at least one of the keys performing the action is pressed, else false
     * @see #actionJustPressed(Enum)
     * @see #InputHandler
     */
    public boolean actionActivated(E action) {
        return Arrays.stream(action.getKeyCodes()).anyMatch(key -> pressedKeysArray[key]);
    }

    /**
     * While {@link #actionActivated(Enum)} checks if one of the action's keys is pressed,
     * this method checks if it wasn't the case last tick. For instance, if 'JUMP' is triggered
     * by the space bar, actionJustPressed will return true for JUMP only the first tick space
     * is pressed, then false until space is released for at least one tick and pressed again.
     * To make it short : <i>just pressed</i> = pressed, but only the first tick of the press.
     * @param action an action that is a subclass of E as defined in {@link #InputHandler}
     * @return true if one of the keys performing the action is just pressed, else false
     * @see #actionActivated(Enum)
     * @see #actionJustReleased(Enum)
     * @see #InputHandler
     */
    public boolean actionJustPressed(E action) {
        boolean pressedLastTick = Arrays.stream(action.getKeyCodes()).anyMatch(key -> previous[key]);
        boolean pressedThisTick = Arrays.stream(action.getKeyCodes()).anyMatch(key -> previous[key]);
        return !pressedLastTick && pressedThisTick;
    }

    /**
     * Works just as {@link #actionJustPressed(Enum)} but for this method, it returns true for this action
     * if the keys for said actions are <i>just released</i>. For instance, if <code>JUMP</code> is
     * triggered by the space bar, <code>actionJustReleased(JUMP)</code> will return true for the first
     * tick of the action not being activated. To make it short : <i>just released</i> = released, but
     * only for the first tick.
     * @param action an enum subclass of E as defined in {@link #InputHandler}
     * @return true if all keys for action are just released, else false
     * @see #actionJustPressed(Enum)
     * @see #InputHandler
     */
    public boolean actionJustReleased(E action) {
        boolean pressedLastTick = Arrays.stream(action.getKeyCodes()).anyMatch(key -> previous[key]);
        boolean pressedThisTick = Arrays.stream(action.getKeyCodes()).anyMatch(key -> previous[key]);
        return pressedLastTick && !pressedThisTick;
    }

}
