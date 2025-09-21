package game.test;


import game.keyHandling.InputActions;
import game.keyHandling.InputHandler;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;

public class InputHandlerTest extends Canvas implements Runnable {

    // Define a minimal action mapping (only SPACE)
    public enum TestActions implements InputActions {
        JUMP(KeyEvent.VK_SPACE);

        private final int[] codes;
        TestActions(int... codes) { this.codes = codes; }
        @Override public int[] getKeyCodes() { return codes; }
    }

    private final InputHandler<TestActions> inputHandler;

    // Game loop stuff
    private boolean running = true;
    private boolean flash = true;

    public InputHandlerTest() {
        setPreferredSize(new Dimension(200, 200));
        setBackground(Color.BLACK);
        setFocusable(true);

        inputHandler = new InputHandler<>(this);
        requestFocus(); // ensures we receive key events
    }

    @Override
    public void run() {
        while (running) {

            repaint(); // draw square depending on input

            inputHandler.update(); // update input state

            try {
                Thread.sleep(250); // 2 Hz refresh
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        int size = 100;
        int x = 50, y = 50;

        // Decide color based on input state
        if (inputHandler.actionJustPressed(TestActions.JUMP)) {
            g.setColor(Color.GREEN);  // Just pressed
            System.out.println("actionJustPressed triggered !");
        } else if (inputHandler.actionActivated(TestActions.JUMP)) {
            g.setColor(Color.BLUE);   // Held down
        } else if (inputHandler.actionJustReleased(TestActions.JUMP)) {
            g.setColor(Color.RED); // Just released
            System.out.println("actionJustReleased triggered !");
        } else {
            g.setColor(Color.GRAY);   // Idle
        }

        g.fillRect(x, y, size, size);

        g.setColor(Color.RED);
        if (flash) g.fillRect(0,0,25,25);
        flash = !flash;
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

