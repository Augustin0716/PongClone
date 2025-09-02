package game.menu;

import game.keyHandling.InputHandler;
import game.keyHandling.GameActions;
import game.menu.menuComponent.*;
import game.Game.MainMenuOptions;
import java.awt.Graphics;
public class MainMenu extends Menu {
    public MainMenu(MenuMaster master, InputHandler<GameActions> input) {
        super(master, input, 4);
        this.initComponents();
    }

    @Override
    public void render(Graphics g) {
        for (MenuComponent mc : menuComponents) mc.render(g);
    }

    @Override
    public void update() {
        if(checkCooldown()) {
            if (input.actionActivated(GameActions.MENU_MOVE_DOWN)) {
                ((SelectableMenuComponent) menuComponents[currentSelection]).toggleSelectionBehavior(false);
                if (currentSelection < menuComponents.length - 1) currentSelection++;
                else currentSelection = 0;
                ((SelectableMenuComponent) menuComponents[currentSelection]).toggleSelectionBehavior(true);
                this.cooldown = SELECTION_COOLDOWN_IN_TICKS;
            } else
            if (input.actionActivated(GameActions.MENU_MOVE_UP)) {
                ((SelectableMenuComponent) menuComponents[currentSelection]).toggleSelectionBehavior(false);
                if (currentSelection == 0) currentSelection = menuComponents.length - 1;
                else currentSelection--;
                ((SelectableMenuComponent) menuComponents[currentSelection]).toggleSelectionBehavior(true);
                this.cooldown = SELECTION_COOLDOWN_IN_TICKS;
            }
            if (input.actionActivated(GameActions.SELECT)) {
                master.menuActions(((SelectableMenuComponent) menuComponents[currentSelection]).click());
                this.cooldown = SELECTION_COOLDOWN_IN_TICKS;
            }
        }
    }

    @Override
    public void initComponents() {
        menuComponents[0] = new Button(this,"2 BOTS", MainMenuOptions.BOT_VS_BOT);
        menuComponents[1] = new Button(this, "PLAYER VS BOT", MainMenuOptions.PLAYER_VS_BOT);
        menuComponents[2] = new Button(this, "2 PLAYERS", MainMenuOptions.PLAYER_VS_PLAYER);
        menuComponents[3] = new Button(this, "QUIT", MainMenuOptions.EXIT_GAME);
        for (int i = 0; i < menuComponents.length; i++) {
            menuComponents[i].setPos(450, 250 + 30 * i);
        }
        ((SelectableMenuComponent) menuComponents[currentSelection]).toggleSelectionBehavior(true);
    }
}
