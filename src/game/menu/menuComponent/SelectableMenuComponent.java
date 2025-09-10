package game.menu.menuComponent;

import game.menu.Menu;

/**
 * A menu component that can handle being clicked and showing it's selected.
 * It's an abstract subclass that carry an Enum that should be used to transmit
 * a message
 * @param <E> an action mapping that the MenuMaster (an interface that allows an object to use a menu) holds and can handle
 */
public abstract class SelectableMenuComponent<E extends Enum<E>> extends MenuComponent {
    public SelectableMenuComponent(Menu master) {super(master);}
    public abstract E click();
    public abstract void toggleSelectionBehavior(boolean isSelected);
}
