package game.test;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class RoundedRectangleTest extends JFrame {

    private float roundedProportion = 0.5f;
    private int quality = 10;
    private final int MAX_Q = 50;
    private int width = 200;
    private final int MAX_W = 200;
    private int height = 200;
    private final int MAX_H = 200;
    private final Consumer<Graphics2D> poly = (g) -> g.fillPolygon(RoundedRectangle.getRoundedRectAsPolygon(50, 50, width, height, roundedProportion, quality));
    private final Consumer <Graphics2D> area = (g) -> g.fill(RoundedRectangle.getRoundedRectAsArea(50, 50, width, height, roundedProportion));
    private final Consumer<Graphics2D> hollow = (g) -> g.fill(RoundedRectangle.getHollowRoundedRect(50, 50, width, height, roundedProportion, 0.15f));
    public Consumer<Graphics2D> paintAction = poly;

    public RoundedRectangleTest() {
        super("RoundedRectangle Visual Test");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        Canvas canvas = new Canvas() {

            @Override
            public void paint(Graphics g) {
                Graphics2D g1 = (Graphics2D) g;
                paintAction.accept(g1);
                g1.dispose();
            }
        };
        add(canvas, BorderLayout.CENTER);

        // Interface simple pour changer les paramètres
        JPanel controls = new JPanel();
        controls.setLayout(new FlowLayout());

        JSlider roundedSlider = new JSlider(1, 0, 100, (int) (roundedProportion * 100));
        JSlider qualitySlider = new JSlider(1, 0, MAX_Q, quality);
        JSlider widthSlider = new JSlider(1, 0,MAX_W, width);
        JSlider heightSlider = new JSlider(1, 0, MAX_H, height);


        controls.add(new JLabel("Roundedness"));
        controls.add(roundedSlider);
        controls.add(new JLabel("Quality"));
        controls.add(qualitySlider);
        controls.add(new JLabel("Width"));
        controls.add(widthSlider);
        controls.add(new Label("Height"));
        controls.add(heightSlider);



        roundedSlider.addChangeListener(e -> {
            roundedProportion = roundedSlider.getValue() / 100f;
            canvas.repaint();
        });

        qualitySlider.addChangeListener(e -> {
            quality = qualitySlider.getValue();
            if (paintAction == poly) canvas.repaint();
        });

        widthSlider.addChangeListener(e -> {
            width = widthSlider.getValue();
            canvas.repaint();
        });

        heightSlider.addChangeListener(e -> {
            height = heightSlider.getValue();
            canvas.repaint();
        });

        add(controls, BorderLayout.EAST);

        JPanel options = new JPanel();
        options.setLayout(new FlowLayout(FlowLayout.CENTER));

        JRadioButton polyRadio = new JRadioButton("Polygon", true);
        JRadioButton areaRadio = new JRadioButton("Area");
        JRadioButton hollowRadio = new JRadioButton("Hollow");

        options.add(polyRadio);
        options.add(areaRadio);
        options.add(hollowRadio);

        polyRadio.addActionListener((e) -> {
            if(polyRadio.isSelected()) return;
            polyRadio.setSelected(true);
            areaRadio.setSelected(false);
            hollowRadio.setSelected(false);
            paintAction = poly;
            canvas.repaint();
        });
        areaRadio.addActionListener((e) -> {
            if(areaRadio.isSelected()) return;
            polyRadio.setSelected(false);
            areaRadio.setSelected(true);
            hollowRadio.setSelected(false);
            paintAction = area;
            canvas.repaint();
        });
        hollowRadio.addActionListener((e) -> {
            if(hollowRadio.isSelected()) return;
            polyRadio.setSelected(false);
            areaRadio.setSelected(false);
            hollowRadio.setSelected(true);
            paintAction = hollow;
            canvas.repaint();
        });

        add(options, BorderLayout.NORTH);

        setVisible(true);
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(RoundedRectangleTest::new);
    }

}

