package game.menu.menuComponent;

import game.menu.Menu;

/**
 * Level of abstraction made for marking a menu component as not a selectable one.
 * Inherits the abstract class MenuComponent (as in the same package, not the awt
 * MenuComponent) and is also an abstract class.
 */
public abstract class StaticMenuComponent extends MenuComponent {
    public StaticMenuComponent(Menu master) {super(master);}
}
