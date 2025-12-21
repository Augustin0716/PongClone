package game.menu.menuComponent;

import game.menu.Menu;
import java.awt.Graphics;

/**
 * A StaticMenuComponent subclass that holds a value and display it. It relies on an initialized DigitFactory object
 * to render. It can display a limited number of digits, which cap its count limit to 10^showedDigits (with showedDigits
 * a parameter for {@link #Counter(Menu, int, DigitFactory, int)  Counter}. Once initialized, this component must be
 * placed using the MenuComponent inherited method <code>setPos</code>.
 */
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
            int xx = x + digitFactory.size * 14 * i;
            g.drawImage(digitFactory.getDigit(valueAsDigits[f - i]), xx, y, null);
        }
    }

    /**
     * It doesn't actually set a value as one may expect, it actually stocks up the digit in <code>valueAsDigit</code>
     * as following : {units, dozens, hundreds, thousands, ...} and so on, until the list is filled. For instance,
     * <code>16</code> will be stocked as <code>{6,1}</code> in valueAsDigits if the counter shows up to the dozens, or
     * <code>{6,1,0}</code> if it shows up to the hundreds. If the number exceeds the limit of the counter, the digits
     * representing the highest power of 10 won't be shown, for instance putting 2004 in a 2-digit counter will show
     * <code>04</code>, as the hundreds and thousands can't and won't be shown.
     * @param value a positive integer to be displayed
     */
    public void setValue(int value) {
        int denominator = 1;
        for (int i = 0; i < valueAsDigits.length; i++) {
            valueAsDigits[i] = (value / denominator % 10);
            denominator *= 10;
        }
    }

    @Override
    public void placeFromCenter(int x, int y) {
        this.x = x - digitFactory.size * 5;
        this.y = y - digitFactory.size * 9;
    }
}
