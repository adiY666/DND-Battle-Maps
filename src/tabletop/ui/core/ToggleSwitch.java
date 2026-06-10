package tabletop.ui.core;

import javax.swing.JCheckBox;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Dimension;
import java.awt.Cursor;
import tabletop.ui.theme.ColorPalette;

/**
 * Represents a custom toggle switch UI component.
 *
 * @author Adi
 */
public class ToggleSwitch extends JCheckBox {

    public ToggleSwitch(String text) {
        super(text);
        this.setOpaque(false);
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
        this.setFocusPainted(false);
        this.setForeground(ColorPalette.TEXT_LIGHT);
    }

    @Override
    protected void paintComponent(Graphics renderGraphics) {
        Graphics2D vectorGraphics = (Graphics2D) renderGraphics;
        vectorGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int switchWidth = 36;
        int switchHeight = 18;
        int yPosition = (this.getHeight() - switchHeight) / 2;

        if(this.isSelected()) {
            vectorGraphics.setColor(ColorPalette.BUTTON_SUCCESS);
            vectorGraphics.fillRoundRect(0, yPosition, switchWidth, switchHeight, switchHeight, switchHeight);
            vectorGraphics.setColor(ColorPalette.TEXT_LIGHT);
            vectorGraphics.fillOval(switchWidth - switchHeight + 2, yPosition + 2, switchHeight - 4, switchHeight - 4);
        } else {
            vectorGraphics.setColor(ColorPalette.INPUT_BACKGROUND);
            vectorGraphics.fillRoundRect(0, yPosition, switchWidth, switchHeight, switchHeight, switchHeight);
            vectorGraphics.setColor(ColorPalette.TEXT_LIGHT);
            vectorGraphics.fillOval(2, yPosition + 2, switchHeight - 4, switchHeight - 4);
        }

        // Draw the label text next to the switch
        vectorGraphics.setColor(this.getForeground());
        int textY = (this.getHeight() + vectorGraphics.getFontMetrics().getAscent()) / 2 - 2;
        vectorGraphics.drawString(this.getText(), switchWidth + 8, textY);
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension originalSize = super.getPreferredSize();
        return new Dimension(originalSize.width + 45, originalSize.height);
    }
}
