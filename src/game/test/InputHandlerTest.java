package game.test;


import game.keyHandling.InputActions;
import game.keyHandling.InputHandler;
import game.Renderable;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.JFrame;

public class InputHandlerTest extends Canvas implements Runnable, Renderable {

    /**
     * Define a minimal action mapping (only SPACE)
     */
    public enum TestActions implements InputActions {
        SPACE(KeyEvent.VK_SPACE);

        private final int[] codes;
        TestActions(int... codes) { this.codes = codes; }
        @Override public int[] getKeyCodes() { return codes; }
    }

    /**
     * Window's dimensions
     */
    private final int SIDE = 200;


    /**
     * the input handler is what we're testing here
     */
    private final InputHandler<TestActions> inputHandler;

    /**
     * Game loop stuff
     */
    private boolean running = true;

    /**
     * Debug queue, which works as history
     */
    private final Queue<Color> history = new ConcurrentLinkedQueue<>();

    public InputHandlerTest() {
        setPreferredSize(new Dimension(SIDE, SIDE));
        setBackground(Color.BLACK);
        setFocusable(true);

        inputHandler = new InputHandler<>(this);
        requestFocus(); // ensures we receive key events
    }

    @Override
    public void render(Graphics unused) {
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) {
            createBufferStrategy(2);
            return;
        }
        Graphics g = bs.getDrawGraphics();
        paint(g);

        g.dispose();
        bs.show();
    }

    @Override
    public void paint(Graphics g) {
        final int HISTORY_SIZE = 50;
        int interval = SIDE / HISTORY_SIZE;
        int size = SIDE / 2;
        int offSet = SIDE / 4;

        Object[] colors = history.toArray();
        for (int i = 0; i < colors.length; i++) {
            g.setColor((Color) colors[i]);
            g.fillRect((i % HISTORY_SIZE) * interval, 0, interval, interval);
        }

        // Decide color based on input state
        if (inputHandler.actionJustPressed(TestActions.SPACE)) {
            g.setColor(Color.GREEN);  // Just pressed
        } else if (inputHandler.actionActivated(TestActions.SPACE)) {
            g.setColor(Color.BLUE);   // Held down
        } else if (inputHandler.actionJustReleased(TestActions.SPACE)) {
            g.setColor(Color.RED); // Just released
        } else {
            g.setColor(Color.GRAY);   // Idle
        }
        g.fillRect(offSet, offSet, size, size);

        history.add(g.getColor());
        if (history.size() > HISTORY_SIZE) history.remove();

    }

    @Override
    public void run() {
        while (running) {

            inputHandler.update(); // update input state

            render(null); // draw square depending on input

            try {
                Thread.sleep(50); // 20 Hz refresh or so, no need to be precise here
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.exit(0); // can't be reached yet
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("InputHandler Test");
        InputHandlerTest testCanvas = new InputHandlerTest();

        frame.add(testCanvas);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        new Thread(testCanvas).start();
    }
}

