package tabletop.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import tabletop.ui.theme.ColorPalette;

/**
 * Dynamically generates default token images in-memory so
 * no external image files are required.
 *
 * @author Adi
 */
public class TokenGenerator {

    public static BufferedImage generate(String name) {
        int size = 200; // High resolution for zooming
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();

        // Make the circle smooth instead of jagged
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Draw background circle
        g2d.setColor(ColorPalette.BUTTON_PRIMARY); // Uses your standard blue theme color
        g2d.fillOval(5, 5, size - 10, size - 10);

        // Draw white border ring
        g2d.setColor(ColorPalette.TEXT_LIGHT);
        g2d.setStroke(new java.awt.BasicStroke(8));
        g2d.drawOval(5, 5, size - 10, size - 10);

        // Draw the first letter of the name in the center
        if (name != null && !name.trim().isEmpty()) {
            String initial = name.trim().substring(0, 1).toUpperCase();
            g2d.setFont(new Font("SansSerif", Font.BOLD, 100));
            FontMetrics fm = g2d.getFontMetrics();
            int x = (size - fm.stringWidth(initial)) / 2;
            int y = ((size - fm.getHeight()) / 2) + fm.getAscent();
            g2d.drawString(initial, x, y);
        }

        g2d.dispose();
        return img;
    }
}
