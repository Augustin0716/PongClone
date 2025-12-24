package game.menu;

import game.Ball;
import game.Game;
import game.MatchManager.PauseMenuOptions;
import game.keyHandling.GameActions;
import game.keyHandling.InputHandler;
import game.menu.menuComponent.Counter;
import game.menu.menuComponent.DigitFactory;
import game.menu.menuComponent.MenuComponent;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

public class BackGroundMenu extends Menu<PauseMenuOptions> {
    private final BufferedImage backGround;

    @SuppressWarnings("unchecked")
    public BackGroundMenu(MenuMaster<PauseMenuOptions> master, InputHandler<GameActions> input) {
        super(master, input);
        initComponents();
        backGround = new BufferedImage(Game.WIDTH, Game.HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = backGround.createGraphics();

        final int r = Ball.RADIUS;
        final int r2 = r * 2;
        final int r3 = r * 3;
        g2d.setColor(new Color(55,55,55));

        // lines surrounding the field
        g2d.fillRect(r2, r2, Game.WIDTH - r2 * 2, Game.HEIGHT - r2 * 2);
        g2d.clearRect(r3, r3, Game.WIDTH - r3 * 2, Game.HEIGHT - r3 * 2);

        // center lines
        g2d.fillRect((Game.WIDTH - r) / 2, r3, r, Game.HEIGHT - r3 * 2);
        g2d.fillRect(Game.WIDTH / 2 - 25, Game.HEIGHT / 2 - 25, 50, 50);
        g2d.clearRect(Game.WIDTH / 2 - 15, Game.HEIGHT / 2 - 15, 30, 30);
        g2d.fillRect((Game.WIDTH - r) / 2, (Game.HEIGHT - r) / 2, r, r);

        g2d.dispose();
    }

    @Override
    public void render(Graphics g) {
        g.drawImage(backGround, 0, 0, null);
        super.render(g);
    }

    @Override
    public void update() {
        throw new UnsupportedOperationException("this menu should not update");
        // unused
    }

    @Override
    public void initComponents() {
        DigitFactory df = new DigitFactory(4);
        menuComponents = new ArrayList<>(Arrays.asList(
                new Counter(this, 0, df, 2), // left
                new Counter(this, 0, df, 2) //right
        ));
        //TODO : find the cause of this bug, somehow the type isn't saved properly
        menuComponents.get(0).setPos(Ball.RADIUS * 5, Ball.RADIUS * 5);
        menuComponents.get(1).setPos(Game.WIDTH - 92 - Ball.RADIUS * 5, Ball.RADIUS * 5);
    }

    public void updateScore(int score, int side) {
        Counter c = ((Counter) menuComponents.get((side == 1)? 0:1));
        c.setValue(score);
    }

}
