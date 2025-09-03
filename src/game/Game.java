package game;

import game.menu.Menu;
import game.menu.MenuMaster;
import game.menu.MainMenu;
import game.keyHandling.InputHandler;
import game.keyHandling.GameActions;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;



public class Game extends Canvas implements Runnable, Updatable, Renderable, MenuMaster<Game.MainMenuOptions> {
    public enum MainMenuOptions {
        NEW_GAME,
        BOT_VS_BOT,
        PLAYER_VS_BOT,
        PLAYER_VS_PLAYER,
        EXIT_GAME,
    } //TODO : extend the enum so it covers more cases

    private final String NAME = "Pong Java Edition";
    public static final int HEIGHT = 500;
    public static final int WIDTH = 800;
    private boolean running;
    private BufferStrategy bs;
    private Graphics g;
    private boolean player1isBot, player2isBot;
    private final float TICK_DELAY_NS = 1000000;
    private final float FRAME_DELAY_NS = (float) 1000000000 / 60;
    private final boolean CAP_REFRESH_RATE = false;
    private Menu menu;
    private MatchManager matchManager;
    private InputHandler<GameActions> input;

    public Game() {
        // For now this doesn't do anything, but we need it
        // Later, we're going to define it differently, so it can be more flexible
    }

    public void stop() {
        running = false;
    } //TODO : stopping the thread isn't sufficient, it should close the window.

    @Override
    public void menuActions(MainMenuOptions action) {

        switch (action) {
            case NEW_GAME -> {
                //the menu changes to allow a selection for the new game, not yet used
                //TODO : implements a new menu if this options is selected
            }
            case EXIT_GAME -> this.stop();
            case BOT_VS_BOT -> launchGame(0); //we initialize the game for 2 bots
            case PLAYER_VS_BOT -> launchGame(1); //we initialize the game for a player and a bot. The player is on the left
            case PLAYER_VS_PLAYER -> launchGame(2); //we initialize the game for 2 players
        } //TODO : the button in the main menu should open other menus

    }

    @Override
    public void openMenu() {
        menu = new MainMenu(this, input);
    }

    @Override
    public void closeMenu() {
        this.menu = null;
    }

    private void launchGame(int gameMode) {
        menu = null;
        matchManager.startGame(gameMode);
    }

    @Override
    public void update() {
        if (menu != null) menu.update();
        else matchManager.update();
    }

    @Override
    public void render(Graphics unused) {
        if (getBufferStrategy() == null) {
            createBufferStrategy(3);
            return;
        }
        bs = getBufferStrategy();
        g = bs.getDrawGraphics();


        g.setColor(Color.BLACK);
        g.fillRect(0,0, WIDTH, HEIGHT);

        if (menu != null) menu.render(this.g);
        else matchManager.render(this.g);

        g.dispose();
        bs.show();
    }


    public void init() {

        input = new InputHandler<GameActions>(this);

        matchManager = new MatchManager(this, input);

        openMenu();
    }

    @Override
    public void run() {
        float tickUpdateToMake;
        float frameUpdateToMake;
        long tickTimer = System.nanoTime();
        int ticks = 0;
        long frameTimer = System.nanoTime();
        int frames = 0;
        long debugTimer = System.currentTimeMillis();
        int loops = 0;

        init();
        do {
            try {
                Thread.sleep(1);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // tick loop, so we don't miss any
            tickUpdateToMake = (System.nanoTime() - tickTimer) / TICK_DELAY_NS;
            while (tickUpdateToMake > 1) {
                update();
                tickUpdateToMake--;
                ticks++;
            }
            tickTimer = System.nanoTime();

            // render loop, if we don't cap the refresh rate, it'll refresh as fast as possible
            frameUpdateToMake = (System.nanoTime() - frameTimer) / FRAME_DELAY_NS;
            if ((frameUpdateToMake > 1 || !CAP_REFRESH_RATE)) {
                render(g);
                frames++;
            }
            frameTimer = System.nanoTime();

            if (System.currentTimeMillis() - debugTimer > 1000) {
                debugTimer += 1000;
                System.out.println(ticks + " ticks and " + frames + " frames last second");
                System.out.println(loops + "game loop last second");
                ticks = frames = loops = 0;
            }
            loops++;
        } while (running);
    }

    public void start() {
        running = true;
        new Thread(this).start();
    }

    public static void main(String[] args) {
        Game game = new Game();
        game.setBackground(Color.BLACK);
        Dimension dim = new Dimension(game.WIDTH,game.HEIGHT);
        game.setPreferredSize(dim);
        game.setMinimumSize(dim);
        game.setMaximumSize(dim);

        JFrame window = new JFrame(game.NAME);
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setLayout(new BorderLayout());
        window.add(game, BorderLayout.CENTER);
        window.pack();
        window.setResizable(false);
        window.setVisible(true);
        game.setFocusable(true);
        game.requestFocus();

        game.start();
    }
}