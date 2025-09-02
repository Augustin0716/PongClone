package game.menu.menuComponent;
import game.menu.Menu;
import java.awt.Graphics;
import java.awt.Font;
import java.awt.Color;
public class Button<E extends Enum<E>> extends SelectableMenuComponent<E> {
private String label;
private String selectedLabel;
private E action;
private String showedLabel;
    public Button(Menu master, String label, E action) {
        super(master);
        this.label = label;
        this.selectedLabel = '<' + label + '>';
        this.showedLabel = label;
        this.action = action;
    }

    @Override
    public void render(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString(this.showedLabel, x, y);
    }

    @Override
    public void update() {

    }

    @Override
    public void toggleSelectionBehavior(boolean isSelected) {
        if (isSelected) this.showedLabel = this.selectedLabel;
        else this.showedLabel = this.label;
    }

    @Override
    public E click() {
        return this.action;
    }
}
