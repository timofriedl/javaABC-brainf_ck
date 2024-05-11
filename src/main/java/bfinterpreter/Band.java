package bfinterpreter;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Band {
    private static final double BLOCK_SIZE = 200.0;
    private static final double BLOCK_MARGIN = BLOCK_SIZE * 0.125;

    private List<Long> values;
    private int targetBandPointer;
    private double bandPointer;

    public Band() {
        values = new CopyOnWriteArrayList<>();
        values.add(0L);
    }

    public void incrementPtr() {
        if (++targetBandPointer >= values.size()) {
            values.add(0L);
        }
    }

    public void decrementPtr() {
        if (--targetBandPointer < 0) {
            List<Long> newValues = new CopyOnWriteArrayList<>();
            newValues.add(0L);
            newValues.addAll(values);
            values = newValues;
            targetBandPointer = 0;
        }
    }

    public void incrementVal() {
        values.set(targetBandPointer, getValue() + 1);
    }

    public void decrementVal() {
        values.set(targetBandPointer, getValue() - 1);
    }

    public long getValue() {
        return values.get(targetBandPointer);
    }

    public void storeValue(long value) {
        values.set(targetBandPointer, value);
    }

    public void tick() {
        bandPointer += (targetBandPointer - bandPointer) * 0.2;
    }

    public void render(Graphics2D g) {
        var it = values.iterator();
        for (int i = 0; it.hasNext(); i++) {
            long value = it.next();
            double cx = (i - bandPointer) * (BLOCK_SIZE + BLOCK_MARGIN);
            var rect = new Rectangle2D.Double(cx - BLOCK_SIZE / 2.0, -BLOCK_SIZE / 2.0, BLOCK_SIZE, BLOCK_SIZE);
            var upper = new Rectangle2D.Double(rect.x, rect.y, rect.width, rect.height * 0.5);
            var lower = new Rectangle2D.Double(rect.x, rect.y + rect.height * 0.5, rect.width, rect.height * 0.5);

            g.setColor(Color.DARK_GRAY);
            g.draw(rect);
            RenderUtil.renderCenteredString(g, Long.toString(value), upper);

            if (value >= 32L && value <= 126) {
                g.setColor(Color.ORANGE);
                RenderUtil.renderCenteredString(g, "'" + (char) value + "'", lower);
            }
        }

        // Render arrow
        g.setColor(Color.RED);
        RenderUtil.renderArrow(g, 0.0, -BLOCK_SIZE / 2.0 - 100.0, 0.0, -BLOCK_SIZE / 2.0 - 20.0, 15.0);
    }

    public double width() {
        return values.size() * BLOCK_SIZE + (values.size() - 1) * BLOCK_MARGIN;
    }
}
