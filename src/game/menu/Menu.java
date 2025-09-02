package game.menu;

import game.Renderable;
import game.Updatable;
import game.keyHandling.InputHandler;
import game.menu.menuComponent.MenuComponent;
import game.menu.menuComponent.SelectableMenuComponent;

/**
 * An abstract class used to display and manage a menu within a graphic interface.
 * It stores MenuComponents objects and can be able to listen to keyboard inputs and
 * send data from menu components to its MenuMaster. Even though the data, an Enum constant
 * is transferred through the menu from the menu components to the MenuMaster object, it doesn't
 * run any check whether the types are compatible, so it's recommended to check everything manually
 * when coding using these classes.
 */
public abstract class Menu implements Renderable, Updatable {
    protected MenuMaster master;
    protected MenuComponent[] menuComponents;
    protected SelectableMenuComponent[] selectableMenuComponents;
    protected final int SELECTION_COOLDOWN_IN_TICKS = 100;
    protected int cooldown = 0;
    protected int currentSelection = 0;
    protected InputHandler input;

    public Menu(MenuMaster master, InputHandler<?> input, int numberOfComponent) {
        this.master = master;
        this.input = input;
        this.menuComponents = new MenuComponent[numberOfComponent];
    }
    public abstract void initComponents();
    protected boolean checkCooldown() {
        if (cooldown > 0) {
            cooldown--;
            return false;
        } else return true;
    }

    protected void setSelectableMenuComponents() {
        int[] list = new int[menuComponents.length];
        int j = 0;
        for(int i = 0; i < menuComponents.length; i++) {
            if (menuComponents[i] instanceof SelectableMenuComponent<?>) {
                list[j] = i;
                j++;
            }
        }
        for (int i = 0; i <= j; i++) {
            selectableMenuComponents[i] = (SelectableMenuComponent<?>) menuComponents[list[i]];
        }
    }
}
