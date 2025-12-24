package game.menu;

import game.Game;
import game.keyHandling.GameActions;
import game.keyHandling.InputHandler;
import game.MatchManager.PauseMenuOptions;
import game.menu.menuComponent.Button;
import game.menu.menuComponent.Label;
import game.menu.menuComponent.MenuComponent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class WonMenu extends Menu<PauseMenuOptions> {

    private final int winningSide;

    public WonMenu(MenuMaster<PauseMenuOptions> master, InputHandler<GameActions> input, int winningSide) {
        super(master, input);
        initComponents();
        this.winningSide = winningSide;
    }

    @Override
    public void initComponents() {
        Font font = new Font("Arial", Font.BOLD, 24);
        selectableMenuComponents = new LoopingList<>(
                new Button<>(this, "Play again", font, PauseMenuOptions.NEW),
                new Button<>(this, "Main Menu", font, PauseMenuOptions.MAIN_MENU)
        );
        menuComponents = new ArrayList<>(Arrays.asList(
                new Label(this, "GAME OVER"),
                new Label(this, ((winningSide == 1)? "Right":"Left")+ " player wins !")
        ));
        menuComponents.addAll(selectableMenuComponents);

        int offSet = 0;
        for(MenuComponent mc : menuComponents) {
            mc.placeFromCenter(Game.WIDTH / 2, Game.HEIGHT / 2 - 100 + offSet);
            offSet += mc.height() + 10;
        }
    }

}
