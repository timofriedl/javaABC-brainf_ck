package bfinterpreter;

import javax.swing.*;
import java.awt.*;

import static java.awt.RenderingHints.*;
import static java.awt.RenderingHints.VALUE_RENDER_QUALITY;

public class Display extends JPanel {
    private final BfInterpreter main;

    public Display(BfInterpreter main) {
        super(new BorderLayout());
        this.main = main;
        main.setContentPane(this);
    }

    @Override
    public void paint(Graphics g) {
        var g2 = (Graphics2D) g;
        g2.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(KEY_RENDERING, VALUE_RENDER_QUALITY);
        main.render(g2);
    }
}
