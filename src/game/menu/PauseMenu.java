package game.menu;

import game.Game;
import game.keyHandling.InputHandler;
import game.keyHandling.GameActions;
import game.menu.menuComponent.MenuComponent;
import game.menu.menuComponent.Label;
import game.menu.menuComponent.Button;
import game.MatchManager.PauseMenuOptions;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class PauseMenu<E extends Enum<E>> extends Menu {

    public PauseMenu(MenuMaster<E> master, InputHandler<GameActions> input) {
        super(master, input);
        initComponents();
    }

    @Override
    public void initComponents() {
        Font font = new Font("Arial", Font.BOLD, 24);
        selectableMenuComponents = new LoopingList<>(
                new Button<>(this, "Resume", font, PauseMenuOptions.RESUME),
                new Button<>(this, "Main Menu", font, PauseMenuOptions.MAIN_MENU)
        );
        menuComponents = new ArrayList<>(Arrays.asList(
                new Label(this, "GAME PAUSED"),
                new Label(this, "Press Del, F or 7 on the num pad to continue")
        ));
        menuComponents.addAll(selectableMenuComponents);

        int sigmaShift = 0;
        for(MenuComponent mc : menuComponents) {
            mc.placeFromCenter(Game.WIDTH / 2, Game.HEIGHT / 2 - 100 + sigmaShift);
            sigmaShift += mc.height() + 10;
        }
    }

    @Override
    public void render(Graphics g) {
        for (MenuComponent mc : menuComponents) {
            mc.render(g);
        }
    }
}
