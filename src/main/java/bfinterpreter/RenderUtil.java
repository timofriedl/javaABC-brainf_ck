package bfinterpreter;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

public abstract class RenderUtil {
    public static void renderCenteredString(Graphics2D g, String text, Rectangle2D bounds) {
        int minFontSize = 1;
        int maxFontSize = 100; // Set an upper limit for the font size
        int fontSize = binarySearchForFontSize(g, text, bounds, minFontSize, maxFontSize) - 1;

        // Adjust font size to fit within bounds
        Font font = g.getFont().deriveFont((float) fontSize);
        g.setFont(font);

        // Calculate position to center the text within bounds
        double x = bounds.getX() + (bounds.getWidth() - g.getFontMetrics(font).stringWidth(text)) / 2 + 1; // Add 1 pixel offset
        double y = bounds.getY() + (bounds.getHeight() - g.getFontMetrics(font).getHeight()) / 2 + g.getFontMetrics(font).getAscent() + 1; // Add 1 pixel offset

        // Render the centered string
        g.drawString(text, (float) x, (float) y);
    }

    private static int binarySearchForFontSize(Graphics2D g, String text, Rectangle2D bounds, int minFontSize, int maxFontSize) {
        int fontSize = minFontSize;
        while (minFontSize <= maxFontSize) {
            fontSize = (minFontSize + maxFontSize) / 2;
            Font font = g.getFont().deriveFont(Font.PLAIN, fontSize);
            g.setFont(font);
            Rectangle2D textBounds = g.getFontMetrics(font).getStringBounds(text, g);

            if (textBounds.getWidth() <= bounds.getWidth() && textBounds.getHeight() <= bounds.getHeight()) {
                minFontSize = fontSize + 1;
            } else {
                maxFontSize = fontSize - 1;
            }
        }
        return fontSize;
    }

    public static void renderArrow(Graphics2D g, double fromX, double fromY, double toX, double toY, double arrowLength) {
        g.draw(new Line2D.Double(fromX, fromY, toX - ((toX - fromX) * 0.05), toY - ((toY - fromY) * 0.05)));

        // Calculate angle of the line
        double angle = Math.atan2(toY - fromY, toX - fromX);

        // Angle offset to create arrowhead
        double arrowAngleOffset = Math.PI / 4;

        // Calculate points for arrowhead
        double arrowX1 = toX - arrowLength * Math.cos(angle - arrowAngleOffset);
        double arrowY1 = toY - arrowLength * Math.sin(angle - arrowAngleOffset);
        double arrowX2 = toX - arrowLength * Math.cos(angle + arrowAngleOffset);
        double arrowY2 = toY - arrowLength * Math.sin(angle + arrowAngleOffset);

        // Draw lines for arrowhead
        g.draw(new Line2D.Double(toX, toY, arrowX1, arrowY1));
        g.draw(new Line2D.Double(toX, toY, arrowX2, arrowY2));
    }
}
