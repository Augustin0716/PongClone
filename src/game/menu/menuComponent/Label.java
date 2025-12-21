package game.menu.menuComponent;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

import game.menu.Menu;

public class Label extends StaticMenuComponent {
    private String label;
    private final Font font = new Font("Arial", Font.BOLD, 24);
    private final FontRenderContext frc = new FontRenderContext(null, true, true);
    public Label(Menu master, String label) {
        super(master);
        this.label = label;
        Rectangle2D bounds = font.getStringBounds(label, frc);
        width = (int) bounds.getWidth();
        height = (int) bounds.getHeight();
    }

    @Override
    public void render(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(font);
        g.drawString(label, x, y);
    }

    public void editLabel(String newLabel) {
        this.label = newLabel;
        Rectangle2D bounds = font.getStringBounds(label, frc);
        width = (int) bounds.getWidth();
        height = (int) bounds.getHeight();
    }

    @Override
    public void placeFromCenter(int x, int y) {
        this.x = x - width / 2;
        this.y = y - height / 2;
    }
}
