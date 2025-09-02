package game.keyHandling;

import java.awt.event.KeyEvent;

/**
 * An Enum class implementing InputActions used as an action dictionary for the game Pong.
 * It stocks everything needed to handle commands in this game.
 *
 */
public enum GameActions implements InputActions {

    PLAYER1_MOVE_UP(new int[] {KeyEvent.VK_Z, KeyEvent.VK_W}),
    PLAYER1_MOVE_DOWN(new int[] {KeyEvent.VK_S, KeyEvent.VK_A}),
    PLAYER2_MOVE_UP(new int[] {KeyEvent.VK_UP, KeyEvent.VK_NUMPAD8}),
    PLAYER2_MOVE_DOWN(new int[] {KeyEvent.VK_DOWN, KeyEvent.VK_NUMPAD2}),
    MENU_MOVE_UP(new int[] {KeyEvent.VK_Z, KeyEvent.VK_W, KeyEvent.VK_UP, KeyEvent.VK_NUMPAD8}),
    MENU_MOVE_DOWN(new int[] {KeyEvent.VK_S, KeyEvent.VK_A, KeyEvent.VK_DOWN, KeyEvent.VK_NUMPAD2}),
    PAUSE(new int[] {KeyEvent.VK_F, KeyEvent.VK_DELETE, KeyEvent.VK_NUMPAD7}),
    SELECT(new int[] {KeyEvent.VK_E, KeyEvent.VK_ENTER, KeyEvent.VK_NUMPAD9});
    private int[] keyCodes;
    GameActions(int[] keyCodes) {
        this.keyCodes = keyCodes;
    }

    @Override
    public int[] getKeyCodes() {
        return this.keyCodes;
    }

    public void setKeyCodes(int[] keyCodes) {
        this.keyCodes = keyCodes;
    } // maybe useful somewhere else, move it up to InputActions and make it Override ?
}
