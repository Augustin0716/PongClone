package game.menu.menuComponent;

import java.awt.*;

import game.menu.Menu;

public class Label extends StaticMenuComponent {
    String label;
    public Label(Menu master, String label) {
        super(master);
        this.label = label;
    }

    @Override
    public void render(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString(label, x, y);
    }

    @Override
    public void update() {

    }

    public void editLabel(String newLabel) {
        this.label = newLabel;
    }
}
