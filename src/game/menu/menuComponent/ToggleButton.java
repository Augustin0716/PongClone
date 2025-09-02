package game.menu.menuComponent;

import game.menu.Menu;
import java.awt.Graphics;
import java.util.Map;
import java.awt.Font;
import java.awt.Color;

public class ToggleButton<E extends Enum<E>> extends SelectableMenuComponent<E> {
    public Map<String, E> options;
    public String label;
    public int currentSelection = 0;
    public String[] optionsNames;
    public String output;
    public ToggleButton(Menu master, String label, Map<String, E> options) {
        super(master);
        this.options = options;
        this.label = label;
        this.optionsNames = options.keySet().toArray(new String[0]);
        this.output = this.label + " : " + optionsNames[currentSelection];
    }

    @Override
    public void render(Graphics g) {
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.setColor(Color.WHITE);
        g.drawString(output, x, y);
    }

    @Override
    public void update() {

    }

    @Override
    public void toggleSelectionBehavior(boolean isSelected) {
        if (isSelected) this.output = label + " : <" + optionsNames[currentSelection] + '>';
        else this.output = label + " : " + optionsNames[currentSelection];
    }

    @Override
    public E click() {
        currentSelection++;
        output = label + " : <" + optionsNames[currentSelection] + '>';
        return options.get(optionsNames[currentSelection]);
    }
}
