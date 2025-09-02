package game.menu;

import game.Ball;
import game.Game;
import game.keyHandling.GameActions;
import game.keyHandling.InputHandler;
import game.menu.menuComponent.Counter;
import game.menu.menuComponent.DigitFactory;
import game.menu.menuComponent.MenuComponent;

import java.awt.Graphics;

public class BackGroundMenu extends Menu {
    public BackGroundMenu(MenuMaster<?> master, InputHandler<GameActions> input) {
        super(master, input, 2);
        initComponents();
    }

    @Override
    public void render(Graphics g){
        for (MenuComponent mc : menuComponents) mc.render(g);
    }

    @Override
    public void update() {
        for (MenuComponent mc : menuComponents) {
            mc.update();
        }
    }

    @Override
    public void initComponents() {
        DigitFactory df = new DigitFactory(4);
        menuComponents[0] = new Counter(this, 0, df, 2);
        menuComponents[1] = new Counter(this, 0, df, 2);
        menuComponents[0].setPos(Ball.RADIUS * 5, Ball.RADIUS * 3);
        menuComponents[1].setPos(Game.WIDTH - Ball.RADIUS * 5 - 92, Ball.RADIUS * 3);

    }

    public void updateScore(int score, int side) {
        Counter c = ((Counter) menuComponents[(side == 1)? 0:1]);
        c.setValue(score);
    }

}
