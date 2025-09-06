package game;

import java.awt.Graphics;

import game.keyHandling.GameActions;
import game.keyHandling.InputHandler;
import game.menu.*;

public class MatchManager implements Renderable, Updatable, MenuMaster<MatchManager.PauseMenuOptions> {

    public enum PauseMenuOptions {
        RESUME,
        MAIN_MENU,
        SOUND_ON, SOUND_OFF

    }
    private final Game master;
    private Menu menu;
    private final int middleX;
    private final int middleY;
    private int gameMode;
    private int scoreSide = 1;
    public Racket player1 = new Racket(1); //TODO : players should be left or right rather than 1 and 2
    public Racket player2 = new Racket(-1);
    public Ball ball = new Ball(this, player1, player2);
    private int countdown = 0;
    public int gameState = -1;
    private int scorePlayer1;
    private int scorePlayer2;
    private final InputHandler<GameActions> input;
    private final BackGroundMenu backGround = new BackGroundMenu(this, null);

    public MatchManager(Game master, InputHandler<GameActions> input) {
        this.master = master;
        this.input = input;
        this.middleX = Game.WIDTH / 2;
        this.middleY = Game.HEIGHT / 2;
        this.player1.x = Ball.RADIUS * 3;
        this.player2.x = Game.WIDTH - Ball.RADIUS * 3;
        resetPos();
    }

    @Override
    public void update() {
        switch (gameState) {
            case -1 -> {
                // in this case, the game hasn't been initialized, so there is nothing to update
            }

            case 0 -> { // before the serve
                if (countdown > 0) {
                    countdown--;
                } else {
                    ball.speed.set(.2f * scoreSide, 0); // the ball is headed to the player that lost last point
                    gameState++;
                    for (Racket player : new Racket[] {player1, player2}) {
                        if (player instanceof ComputerPlayer) ((ComputerPlayer) player).setTargetY(ball);
                    }
                }
            }

            case 1 -> { // the part where the game is actually played
                gameLogic();
                scoreSide = ball.touchDown();
                if (scoreSide == 0) return;
                int score = (scoreSide == 1)? ++scorePlayer1:++scorePlayer2;
                backGround.updateScore(score, scoreSide);
                countdown = 2000;
                gameState++;
            }

            case 2 -> { // countdown before resetting the positions, so the players acknowledge the score
                if (countdown > 0) countdown--;
                else {
                    resetPos();
                    if (winTest()) {
                        gameState++;
                        menu = new WonMenu(this, input, scoreSide);
                        countdown = 1500;
                    } else {
                        gameState = 0;
                        countdown = 3000;
                    }
                }
            }

            case 3 -> {
                if (menu == null) {
                    if (countdown > 0) countdown--;
                    else {
                        gameState = -1;
                        master.openMenu();
                    }
                } else menu.update(); //else the menu will not update
            }
        }
    }

    public void updateEntities() {
        player1.update();
        player2.update();
        ball.update();
    }

    @Override
    public void render(Graphics g){
        backGround.render(g);
        player1.render(g);
        player2.render(g);
        ball.render(g);
        if (menu != null) menu.render(g);
    }

    @Override
    public void menuActions(PauseMenuOptions action) {
        /*
        switch (action) {
            case RESUME -> menu = null;
            case MAIN_MENU -> we call the game, so it opens the main menu and kill the current game
            case SOUND_OFF -> we mute the game when the sound will exist
            case SOUND_ON -> we play sounds again when they exist
         */
    }

    @Override
    public void openMenu() {
        menu = new PauseMenu<>(this, input);
    }

    @Override
    public void closeMenu() {
        this.menu = null;
    }


    public void resetPos() {
        player1.y = player2.y = middleY - (float) Racket.HEIGHT / 2;
        ball.position.set(middleX, middleY);
        ball.speed.set(0,0);
    }

    public void startGame(int gameMode) {
        backGround.updateScore(0,1);
        backGround.updateScore(0,-1);
        this.gameMode = gameMode;
        scorePlayer1 = scorePlayer2 = 0;
        switch (gameMode) {
            case 0 -> {
                player1 = new ComputerPlayer(1, ComputerPlayer.Difficulty.SMART);
                player2 = new ComputerPlayer(-1, ComputerPlayer.Difficulty.SMART);
            }
            case 1 -> {
                player1 = new Racket(1);
                player2 = new ComputerPlayer(-1, ComputerPlayer.Difficulty.OKAY);
            }
            case 2 -> {
                player1 = new Racket(1);
                player2 = new Racket(-1);
            }
        }
        ball = new Ball(this, player1, player2);
        countdown = 1000;

        player1.x = Ball.RADIUS * 3;
        player2.x = Game.WIDTH - Ball.RADIUS * 3;
        resetPos();
        gameState++;
    }

    private void gameLogic() {
        if (menu != null) {
            menu.update();
            // causes a return because we shouldn't update anything else if there is a PauseMenu.
            return;
        }
        if (input.actionActivated(GameActions.PAUSE)) {
            //should have been actionJustPressed, but it doesn't work because of tick doubling
            openMenu();
            // same as above, it's safer to stop right away
            return;
        }
        switch (gameMode) {
            case 0 -> {
                // bots play their moves
                // typecasts are made so the IDE doesn't scream for help and are safe anyway thanks to the logic
                ((ComputerPlayer) player1).computerMove();
                ((ComputerPlayer) player2).computerMove();
            }

            case 1 -> { // if only one player plays, every input for the players trigger p1
                if (input.actionActivated(GameActions.PLAYER1_MOVE_DOWN) || input.actionActivated(GameActions.PLAYER2_MOVE_DOWN)) {
                    player1.y += Racket.SPEED;
                }
                if (input.actionActivated(GameActions.PLAYER1_MOVE_UP) || input.actionActivated(GameActions.PLAYER2_MOVE_UP)) {
                    player1.y -= Racket.SPEED;
                }
                // the bot plays too
                ((ComputerPlayer) player2).computerMove();
            }

            case 2 -> {
                if (input.actionActivated(GameActions.PLAYER1_MOVE_DOWN)) player1.y += Racket.SPEED;
                if (input.actionActivated(GameActions.PLAYER1_MOVE_UP)) player1.y -= Racket.SPEED;
                if (input.actionActivated(GameActions.PLAYER2_MOVE_DOWN)) player2.y += Racket.SPEED;
                if (input.actionActivated(GameActions.PLAYER2_MOVE_UP)) player2.y -= Racket.SPEED;
            }
        }
        // entities are updated last so every tick the moves are regarded
        updateEntities();
    }

    public boolean winTest() {
        if (scorePlayer1 == 11 || scorePlayer2 == 11) return true;
        if (scorePlayer1 >= 9 && scorePlayer1 - scorePlayer2 > 1) return true;
        return scorePlayer2 >= 9 && scorePlayer2 - scorePlayer1 > 1;
    }
}
