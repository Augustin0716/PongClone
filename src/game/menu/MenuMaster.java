package game.menu;

/**
 * An interface that is used to define a class as being able to handle a menu
 * (from the same package, not an awt Menu object). It holds an enum that is used
 * as a set of constants to perform actions.
 * @param <E> an Enum also hold by Menu Components object
 */
public interface MenuMaster<E extends Enum<E>> {
    /**
     * This method should be called by a menu whenever a menu Component is clicked
     * so this object can execute another method which corresponds to the button.
     * This method should be implemented in the form of a <code>switch</code> as following :
     * <pre>{@code
     * private void menuActions(ActionEnum action) {
     *     switch (actions) {
     *         case (ActionEnum.ACTION1) -> anObjectMethod1();
     *         case (ActionEnum.ACTION2) -> anObjectMethod2();
     *         case (ActionEnum.ACTION3) -> anObjectMethod3();
     *     }
     * }}</pre>
     * It should be called by the menu object whenever the user click and a selectable component
     * is selected (the menu should be able to know what component is selected) as such :
     * <pre>{@code
     * if (userClicked()) {
     *      master.menuActions(getSelectedMenuComponent().click())
     *      // SelectableMenuComponent.click() returns a subclass of the Enum
     * }
     * }</pre>
     * @param action an Enum that is a subclass of E as defined in MenuMaster
     */
    void menuActions(E action);

    /**
     * Used to open a menu. It gives a main way to open a menu for a MenuMaster Object.
     * It should be the assignation to a variable, such as <pre>{@code Menu menu = new Menu();}</pre>
     * (However this line doesn't work since <code>Menu</code> is abstract, it should be a subclass of Menu).
     * @see #closeMenu()
     */
    void openMenu();

    /**
     * Used to close a menu. It gives a main to close a menu. It should simply be a null
     * assignation to a variable, such as : <pre>{@code Menu menu = null;}</pre>
     */
    void closeMenu();
}