package game.menu.menuComponent;

import game.Renderable;
import game.menu.Menu;

public abstract class MenuComponent implements Renderable {
    protected int x,y, width, height;
    protected Menu<?> master;

    public MenuComponent(Menu<?> master) {
        this.master = master;
    }

    public MenuComponent(Menu<?> master, int x, int y) {
        this(master);
        this.x = x;
        this.y = y;
    }

    public void setPos(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Method to place a menu component from its center, more practical than {@link MenuComponent#setPos(int, int)}. The
     * menu component object must know its own width and height, or throw {@link UnsupportedOperationException}.
     * @param x the x coord of the center of the menu component
     * @param y the y coord of the center of the menu component
     * @throws UnsupportedOperationException if not implemented
     */
    public abstract void placeFromCenter(int x, int y);

    public int height() {
        return height;
    }

    public int width() {
        return width;
    }
}
