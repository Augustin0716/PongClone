package game.keyHandling;

/**
 *
 * This functional interface allows an enum class to be used as an action mapping
 * by the InputHandler class. An action mapping is an ensemble of constants (the enum instances) associated
 * with an integer array that represents the codes of the key that triggers said action.
 * Multiple keys can trigger an action, and multiple actions can be triggered by the same
 * key, which allows flexibility.<br>
 * Here is an example of an action dictionary :
 * <pre>{@code
 * GameActions extends Enum implements InputActions {
 *     GO_LEFT(new int[] {KeyEvent.VK_LEFT}),
 *     GO_RIGHT(new int[] {KeyEvent.VK_RIGHT}),
 *     JUMP(new int[] {KeyEvent.VK_UP}),
 *     CROUCH(new int[] {KeyEvent.VK_DOWN});
 *
 *     private int[] keyCodes;
 *
 *     GameActions(int[] keyCodes) {
 *         this.keyCodes = keyCodes;
 *     }
 *
 *     @Override
 *     public int[] getKeyCodes() {
 *         return keyCodes;
 *     }
 * }}</pre>
 * It is advised but not mandatory to also declare a method <pre>{@code
 * public void setKeyCodes(int[] keyCodes) {
 *     this.keyCodes = keyCodes;
 * }}</pre> to be able to modify the key codes of any given action.
 */
@FunctionalInterface
public interface InputActions {
    int[] getKeyCodes();
}
