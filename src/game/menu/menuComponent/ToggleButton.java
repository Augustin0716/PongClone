package game.menu.menuComponent;

import game.menu.LoopingList;
import game.menu.Menu;
import java.awt.Graphics;
import java.util.Map;
import java.awt.Font;
import java.awt.Color;

public class ToggleButton<E extends Enum<E>> extends SelectableMenuComponent<E> {
    public Map<String, E> options;
    public String label;
    public LoopingList<String> optionsNames;
    public String output;

    public ToggleButton(Menu<E> master, String label, Map<String, E> options) {
        super(master);
        this.options = options;
        this.label = label;
        this.optionsNames = new LoopingList<>(options.keySet());
        this.output = this.label + " : " + optionsNames.current();
    }

    @Override
    public void render(Graphics g) {
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.setColor(Color.WHITE);
        g.drawString(output, x, y);
    }

    @Override
    public void toggleSelectionBehavior(boolean isSelected) {
        if (isSelected) this.output = label + " : <" + optionsNames.current() + '>';
        else this.output = label + " : " + optionsNames.current();
    }

    @Override
    public E click() {
        output = label + " : <" + optionsNames.current() + '>';
        return options.get(optionsNames.next());
    }

    @Override
    public void placeFromCenter(int x, int y) {
        //TODO : complete this method
    }
}
