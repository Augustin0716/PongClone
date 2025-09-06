package game.menu;

import game.keyHandling.GameActions;
import game.keyHandling.InputHandler;
import game.menu.menuComponent.Label;
import game.menu.menuComponent.MenuComponent;

import java.awt.*;

public class WonMenu extends Menu {

    private final int winningSide;

    public WonMenu(MenuMaster master, InputHandler<GameActions> input, int winningSide) {
        super(master, input, 2);
        initComponents();
        this.winningSide = winningSide;
    }

    @Override
    public void initComponents() {
        menuComponents[0] = new Label(this, "GAME OVER");
        menuComponents[1] = new Label(this, (winningSide == 1)? "Left player wins !":"Right player wins !");

        menuComponents[0].setPos(350, 220);
        menuComponents[1].setPos(320, 250);
    }

    @Override
    public void render(Graphics g) {
        for (MenuComponent mc : menuComponents) {
            mc.render(g);
        }
    }

    @Override
    public void update() {
        if (input.actionActivated(GameActions.SELECT)) {
            master.closeMenu();
        }
    }
}
