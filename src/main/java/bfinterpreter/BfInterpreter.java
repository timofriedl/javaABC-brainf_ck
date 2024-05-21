package bfinterpreter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Rectangle2D;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class BfInterpreter extends JFrame implements KeyListener {
    private double zoom = 1.0;

    private String program = """
            ++      (Erste Zelle: 2)
            >
            +++     (Zweite Zelle: 3)
            [-<+>]  (Schleife)
            <       (Nach Addition zurück zur linken Zelle)
            """;
    private String output;
    private int instructionPointer;
    private Band band;

    private double executionSpeed = 1.0;
    private long ticks;

    private boolean waitingForInput;

    public BfInterpreter() {
        super("BF Interpreter");
        setSize(1080, 720);
        setResizable(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        addKeyListener(this);

        band = new Band();

        reset();

        setContentPane(new Display(this));
        start();
        setVisible(true);
    }

    private void reset() {
        band = new Band();
        instructionPointer = -1;
        waitingForInput = false;
        ticks = 0L;
        program = program.replaceAll("[^><+\\-.,\\[\\]]", "");
        output = "";
        tick();
    }

    private void start() {
        //noinspection resource
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            try {
                tick();
                getContentPane().repaint();
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(0);
            }
        }, 0L, 17L, TimeUnit.MILLISECONDS);
    }

    private void jump(int delta, char stopChar) {
        int level = 0;
        for (; instructionPointer >= 0 && instructionPointer < program.length(); instructionPointer += delta) {
            char instruction = program.charAt(instructionPointer);
            level += switch (instruction) {
                case '[' -> 1;
                case ']' -> -1;
                default -> 0;
            };

            if (level == 0 && instruction == stopChar)
                return;
        }

        throw new IllegalStateException("Illegal brackets");
    }

    private void executeNextInstruction() {
        if (++instructionPointer >= program.length())
            return;

        executionSpeed *= 1.05;

        char instruction = program.charAt(instructionPointer);
        switch (instruction) {
            case '>' -> band.incrementPtr();
            case '<' -> band.decrementPtr();
            case '+' -> band.incrementVal();
            case '-' -> band.decrementVal();
            case '.' -> {
                char c = (char) band.getValue();
                System.out.print(c);
                output += c;
            }
            case ',' -> waitingForInput = true;
            case '[' -> {
                if (band.getValue() == 0L)
                    jump(1, ']');
            }
            case ']' -> {
                if (band.getValue() != 0L)
                    jump(-1, '[');
            }
            default -> executeNextInstruction();
        }
    }

    private synchronized void input(char userInput) {
        band.storeValue(userInput);
        waitingForInput = false;
    }

    private void tick() {
        if (!waitingForInput && ticks++ >= (long) (60.0 / executionSpeed)) {
            executeNextInstruction();
            ticks = 0L;
        }

        band.tick();

        double targetZoom = getContentPane().getWidth() / (2.0 * band.width());
        targetZoom = Math.min(1.0, Math.max(0.2, targetZoom));
        zoom += (targetZoom - zoom) * 0.2;
    }

    private void renderProgram(Graphics2D g, int w, int h) {
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(2F));
        double margin = 0.1 * h;
        double s = (w - 2.0 * margin) / program.length();
        if (s > h * 0.075)
            s = h * 0.075;

        for (int i = 0; i < program.length(); i++) {
            double hPadding = Math.max(margin, (w - program.length() * s) / 2.0);
            double x = hPadding + i / (double) program.length() * (w - 2.0 * hPadding);
            char instruction = program.charAt(i);

            g.setColor(switch (instruction) {
                case '>', '<' -> Color.BLUE;
                case '.', ',' -> Color.ORANGE;
                case '[', ']' -> Color.GREEN;
                default -> Color.BLACK;
            });

            Rectangle2D.Double rect = new Rectangle2D.Double(x, margin, s, s);
            RenderUtil.renderCenteredString(g, Character.toString(instruction), rect);

            if (i == instructionPointer) {
                g.setColor(Color.RED);
                g.draw(rect);
            }
        }
    }

    private void renderOutput(Graphics2D g, int w, int h) {
        g.setColor(Color.BLACK);
        var rect = new Rectangle2D.Double(w * 0.05, h * 0.75, w * 0.9, h * 0.125);
        RenderUtil.renderCenteredString(g, output, rect);
    }

    public void render(Graphics2D g) {
        int w = getContentPane().getWidth();
        int h = getContentPane().getHeight();

        // Render background
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, w, h);

        // Render program and output
        g.setFont(Fonts.DEFAULT.deriveFont(Font.BOLD));
        renderProgram(g, w, h);
        renderOutput(g, w, h);

        // Shift canvas
        g.translate(w / 2.0, h / 2.0);
        g.scale(zoom, zoom);

        // Render band
        g.setStroke(new BasicStroke(6F));
        band.render(g);
    }

    public static void main(String[] args) {
        new BfInterpreter();
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // ignore
    }

    @Override
    public synchronized void keyPressed(KeyEvent e) {
        if (!waitingForInput)
            return;

        char c = e.getKeyChar();
        if (Character.isDefined(c)) {
            input(c);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // ignore
    }
}
