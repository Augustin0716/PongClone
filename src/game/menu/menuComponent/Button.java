package game.menu.menuComponent;

import game.menu.Menu;
import game.test.RoundedRectangle;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.Font;

public class Button<E extends Enum<E>> extends SelectableMenuComponent<E> {
    /**
     * The action to pass on when clicking the button, which is then interpreted by the MenuMaster
     */
    private final E action;
    /**
     * The button display when it's not selected
     */
    private final BufferedImage normalDisplay;
    /**
     * The button display when it's selected
     */
    private final BufferedImage selectedDisplay;
    /**
     * This variable is used so it's not necessary to check each time whether the button is selected
     */
    private BufferedImage showedDisplay;

    public Button(Menu master, String label, Font font, E action) {
        super(master);
        this.action = action;
        FontRenderContext frc = new FontRenderContext(null, true, true);
        Rectangle2D bounds = font.getStringBounds(label, frc);
        width = (int) (bounds.getWidth() + 40);
        height = (int) (bounds.getHeight() + 30);

        normalDisplay = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        selectedDisplay = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2;

        GradientPaint gp = new GradientPaint(0f, 0f, Color.WHITE, 0, height, new Color(200,220,220));


        g2 = normalDisplay.createGraphics();
        g2.setPaint(gp);
        g2.setFont(font);

        g2.fill(RoundedRectangle.getHollowRoundedRect(5,5,width - 10, height - 10, 0.7f, 5));
        g2.drawString(label, 20, height - 20);
        g2.dispose();

        g2 = selectedDisplay.createGraphics();
        g2.setPaint(gp);
        g2.setFont(font);
        g2.fill(RoundedRectangle.getHollowRoundedRect(0, 0,width, height, 0.7f, 10));
        g2.drawString(label, 20, height - 20);
        g2.dispose();

        showedDisplay = normalDisplay;
    }

    @Override
    public void render(Graphics g) {
        g.drawImage(showedDisplay, x, y, null);
    }

    @Override
    public void toggleSelectionBehavior(boolean isSelected) {
        showedDisplay = isSelected? selectedDisplay:normalDisplay;
    }

    @Override
    public E click() {
        return this.action;
    }

    @Override
    public void placeFromCenter(int x, int y) {
        this.x = x - width / 2;
        this.y = y - height / 2;
    }
}
