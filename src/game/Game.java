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
    private final float TICK_DELAY_NS =  1E7f; // = 1,000,000,000 / 100 which makes it 100 Hz for test
    // TODO : currently 1000 Hz, test for 60, 100 or 120 Hz to save CPU
    private final float FRAME_DELAY_NS = 1.6666667E7f; // = 1,000,000,000 / 60 which makes it 60 Hz
    private final boolean CAP_REFRESH_RATE = true; //doesn't work for some reasons
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
        input.update();
        if (menu != null) menu.update();
        else matchManager.update();
    }

    @Override
    public void render(Graphics unused) {
        if (getBufferStrategy() == null) {
            createBufferStrategy(3);
            return;
        }
        BufferStrategy bs = getBufferStrategy();
        Graphics g = bs.getDrawGraphics();

        g.setColor(Color.BLACK);
        g.clearRect(0,0, WIDTH, HEIGHT);

        if (menu != null) menu.render(g);
        else matchManager.render(g);

        g.dispose();
        bs.show();
    }


    public void init() {

        input = new InputHandler<>(this);

        matchManager = new MatchManager(this, input);

        openMenu();
    }

    @Override
    public void run() {
        long now;
        long tickTimer = System.nanoTime();
        float unprocessedTicks = 0;
        int ticks = 0;
        long frameTimer = System.nanoTime();
        int frames = 0;
        float unprocessedFrames = 0;
        long debugTimer = System.currentTimeMillis();
        int loops = 0;
        boolean shouldRender = false;

        init();

        do {
            now = System.nanoTime();
            unprocessedTicks += (now - tickTimer) / TICK_DELAY_NS;
            tickTimer = now;

            // tick loop, so we don't miss any
            while (unprocessedTicks >= 1) {
                update();
                ticks++;
                unprocessedTicks--;
                shouldRender = true;
            }

            // render loop, if we don't cap the refresh rate, it'll refresh as fast as possible
            unprocessedFrames += (now - frameTimer) / FRAME_DELAY_NS;
            frameTimer = now;
            if ((unprocessedFrames >= 1 || !CAP_REFRESH_RATE) && shouldRender) {
                render(null);
                frames++;
                shouldRender = false;
                unprocessedFrames--;
            }

            if (System.currentTimeMillis() - debugTimer >= 1000) {
                debugTimer += 1000;
                System.out.println(ticks + " ticks and " + frames + " frames last second");
                System.out.println(loops + "game loop last second");
                ticks = frames = loops = 0;
            }

            try {
                Thread.sleep(5);
            } catch (Exception e) {
                e.printStackTrace();
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
        Dimension dim = new Dimension(Game.WIDTH, Game.HEIGHT);
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