package game.menu;

import game.keyHandling.InputHandler;
import game.keyHandling.GameActions;
import game.menu.menuComponent.MenuComponent;
import game.menu.menuComponent.Label;

import java.awt.Graphics;

public class PauseMenu<E extends Enum<E>> extends Menu {


    public PauseMenu(MenuMaster<E> master, InputHandler<GameActions> input) {
        super(master, input,2);
        initComponents();
    }

    @Override
    public void initComponents() {
        menuComponents[0] = new Label(this, "GAME PAUSED");
        menuComponents[1] = new Label(this, "Press Del, F or 7 on the num pad to continue");
        menuComponents[0].setPos(350, 200);
        menuComponents[1].setPos(250, 235);
    }

    @Override
    public void update() {
        if(input.actionActivated(GameActions.PAUSE)) {
            master.closeMenu();
        } //TODO : secure the menu so it doesn't close immediately
    }

    @Override
    public void render(Graphics g) {
        for (MenuComponent mc : menuComponents) {
            mc.render(g);
        }
    }
}
