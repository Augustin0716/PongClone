package game.menu;

import game.keyHandling.GameActions;
import game.keyHandling.InputHandler;
import game.menu.menuComponent.MenuComponent;
import game.menu.menuComponent.SelectableMenuComponent;
import game.Updatable;
import game.Renderable;

import java.awt.Graphics;
import java.util.ArrayList;

/**
 * An abstract class used to display and manage a menu within a graphic interface.
 * It stores MenuComponents objects and is able to listen to keyboard inputs and
 * send data from menu components to its MenuMaster. The parameter is the enum sent as data to the MenuMaster. Even
 * though the Menu class and subclasses don't use it directly, it's a parameter for Menu so everything is type-safe.
 * @param <E>
 */
public abstract class Menu<E extends Enum<E>> implements Renderable, Updatable {

    protected final MenuMaster<E> master;
    /**
     * A {@link LoopingList} that contains every {@link SelectableMenuComponent} for easy handling of the change and the
     * capacity to pass from the last element of the menu to the first without any test.
     */
    protected LoopingList<SelectableMenuComponent<E>> selectableMenuComponents;
    /**
     * An {@link ArrayList} that contains every {@link MenuComponent}, including the ones from
     * {@link Menu#selectableMenuComponents}. It's the list of every single {@code MenuComponent} object in the menu.
     */
    protected ArrayList<MenuComponent> menuComponents;
    protected final int SELECTION_COOLDOWN_IN_TICKS = 10;
    protected int cooldown = 0;
    protected final InputHandler<GameActions> input;

    /**
     * Sole constructor of the class. This allows methods like the overridden {@link Menu#update()} method can
     * work properly.
     * @param master the master of the menu, which is parameterized with E like the menu and the
     *              {@link SelectableMenuComponent} objects it holds
     * @param input the input, whether they're used or not. Can be null if {@code update()} is overridden not to handle
     *              it.
     */
    public Menu(MenuMaster<E> master, InputHandler<GameActions> input) {
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

    /**
     * Default method to update. Uses the inputHandler to listen to the inputs and move in consequence. In this form, being
     * updated is like having focus.
     */
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

    /**
     * Default method for rendering a menu. This method asserts that every {@code MenuComponent} object that must be
     * rendered is present in {@link Menu#menuComponents}.
     * @param g the Graphics object used to render
     */
    @Override
    public void render(Graphics g) {
        for (MenuComponent mc : menuComponents) mc.render(g);
    }
}
