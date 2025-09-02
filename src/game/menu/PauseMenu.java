package game.menu;

import game.keyHandling.InputHandler;
import game.keyHandling.GameActions;
import game.menu.menuComponent.MenuComponent;

import java.awt.*;

public class PauseMenu<E extends Enum<E>> extends Menu {

    public PauseMenu(MenuMaster<E> master, InputHandler input) {
        super(master, input,0);
        initComponents();
    }

    @Override
    public void initComponents() {

    }

    @Override
    public void update() {
        if (checkCooldown()) {
            if (input.actionActivated(GameActions.PAUSE)) {
                master.closeMenu();
                return;
            }
            if (input.actionActivated(GameActions.MENU_MOVE_DOWN)) {

            }
        }
    }

    @Override
    public void render(Graphics g) {
        for (MenuComponent mc : menuComponents) {
            mc.render(g);
        }
    }
}
