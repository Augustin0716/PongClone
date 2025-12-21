package game.menu;

import game.Renderable;
import game.Updatable;
import game.keyHandling.GameActions;
import game.keyHandling.InputHandler;
import game.menu.menuComponent.MenuComponent;
import game.menu.menuComponent.SelectableMenuComponent;

import java.util.ArrayList;

/**
 * An abstract class used to display and manage a menu within a graphic interface.
 * It stores MenuComponents objects and can be able to listen to keyboard inputs and
 * send data from menu components to its MenuMaster. Even though the data, an Enum constant
 * is transferred through the menu from the menu components to the MenuMaster object, it doesn't
 * run any check whether the types are compatible, so it's recommended to check everything manually
 * when coding using these classes.
 */
public abstract class Menu implements Renderable, Updatable {

    protected final MenuMaster master;
    protected ArrayList<MenuComponent> menuComponents;
    protected LoopingList<SelectableMenuComponent> selectableMenuComponents;
    protected final int SELECTION_COOLDOWN_IN_TICKS = 10;
    protected int cooldown = 0;
    protected final InputHandler input;

    public Menu(MenuMaster<?> master, InputHandler<?> input) {
        this.master = master;
        this.input = input;
    }

    /**
     * This method should be used to initiate the construction of its component. The method is just here to clearly name
     * what's going on and encapsulate the behavior in every menu under a common name. Should be called in the
     * constructor of every menu.
     */
    public abstract void initComponents();

    /**
     * Method to test to see whether the menu should be able to change selection. This method is a way to temper the
     * speed of the changes, so a button can be held to defile without getting to the bottom of the list too quickly.
     * @return true if the menu can change selection, false otherwise
     */
    protected boolean checkCooldown() {
        if (cooldown > 0) {
            cooldown--;
            return false;
        } else return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void update() {
        if(checkCooldown()) {
            if (input.actionActivated(GameActions.MENU_MOVE_DOWN)) {
                selectableMenuComponents.current().toggleSelectionBehavior(false);
                selectableMenuComponents.next().toggleSelectionBehavior(true);
                this.cooldown = input.actionJustPressed(GameActions.MENU_MOVE_DOWN)?
                        SELECTION_COOLDOWN_IN_TICKS * 2:SELECTION_COOLDOWN_IN_TICKS;
            } else // if we go up, the go down part is ignored thanks to the 'else'
                if (input.actionActivated(GameActions.MENU_MOVE_UP)) {
                    selectableMenuComponents.current().toggleSelectionBehavior(false);
                    selectableMenuComponents.previous().toggleSelectionBehavior(true);
                    this.cooldown = input.actionJustPressed(GameActions.MENU_MOVE_UP)?
                            SELECTION_COOLDOWN_IN_TICKS * 2:SELECTION_COOLDOWN_IN_TICKS;
                }
            if (input.actionJustPressed(GameActions.SELECT)) {
                // just pressed here to avoid clicking through multiple menus
                master.menuActions(selectableMenuComponents.current().click());
                this.cooldown = SELECTION_COOLDOWN_IN_TICKS;
            }
        }
    }
}
