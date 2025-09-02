package game.menu.menuComponent;

import game.Renderable;
import game.Updatable;
import game.menu.Menu;

public abstract class MenuComponent implements Renderable, Updatable {
    int x,y;
    Menu master;
    public MenuComponent(Menu master) {
        this.master = master;
    }
    public MenuComponent(Menu master, int x, int y) {
        this(master);
        this.setPos(x, y);
    }
    public void setPos(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
