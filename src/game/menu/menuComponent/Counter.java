package game.menu.menuComponent;

import game.menu.Menu;
import java.awt.Graphics;

public class Counter extends StaticMenuComponent {
    private final int[] valueAsDigits;
    private final DigitFactory digitFactory;
    public Counter(Menu master, int baseValue, DigitFactory digitFactory, int showedDigits) {
        super(master);
        this.digitFactory = digitFactory;
        this.valueAsDigits = new int[showedDigits];
        this.setValue(baseValue);
    }

    public Counter(Menu master, int baseValue, DigitFactory digitFactory) {
        this(master, baseValue, digitFactory, 1);
    }

    @Override
    public void render(Graphics g) {
        int f = valueAsDigits.length - 1;
        for (int i = 0; i < valueAsDigits.length; i++) {
            int xx = x + digitFactory.size * 13 * i;
            g.drawImage(digitFactory.getDigit(valueAsDigits[f - i]), xx, y, null);
        }
    }

    @Override
    public void update() {
        // not used
    }

    public void setValue(int value) {
        int denominator = 1;
        for (int i = 0; i < valueAsDigits.length; i++) {
            valueAsDigits[i] = (value / denominator % 10);
            denominator *= 10;
        }
    }

}
