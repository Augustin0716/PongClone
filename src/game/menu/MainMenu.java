package game.menu;

import game.Game;
import game.keyHandling.InputHandler;
import game.keyHandling.GameActions;
import game.Game.MainMenuOptions;
import game.menu.menuComponent.Button;
import game.menu.menuComponent.MenuComponent;

import java.awt.*;
import java.util.ArrayList;

public class MainMenu extends Menu<MainMenuOptions> {
    public MainMenu(MenuMaster<MainMenuOptions> master, InputHandler<GameActions> input) {
        super(master, input);
        this.initComponents();
    }

    @Override
    public void initComponents() {
        Font font = new Font("Arial", Font.BOLD, 24);

        selectableMenuComponents = new LoopingList<>(
                new Button<>(this,"2 BOTS", font, MainMenuOptions.BOT_VS_BOT),
                new Button<>(this, "PLAYER VS BOT", font, MainMenuOptions.PLAYER_VS_BOT),
                new Button<>(this, "2 PLAYERS", font,MainMenuOptions.PLAYER_VS_PLAYER),
                new Button<>(this, "QUIT", font, MainMenuOptions.EXIT_GAME)
                );
        menuComponents = new ArrayList<>(selectableMenuComponents);
        int i = 0;
        for (MenuComponent mc : menuComponents) {
            mc.placeFromCenter(Game.WIDTH / 2, Game.HEIGHT / 2 - 90 + 60 * i++);
        }
        selectableMenuComponents.current().toggleSelectionBehavior(true);
    }
}
