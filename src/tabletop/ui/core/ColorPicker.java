package tabletop.ui.core;

import javax.swing.*;
import java.awt.*;

import tabletop.ui.theme.ColorPalette;
import tabletop.util.ColorUtility;

/**
 * Represents a custom color selection popup.
 *
 * @author Adi
 */
public class ColorPicker extends JButton {

    private String selectedColorName = "Red";
    private final ColorUtility colorUtility;
    private final String[] availableColors = {"Red", "Green", "Blue", "Yellow", "Purple", "Orange", "Black", "White", "Cyan", "Magenta"};

    // NEW: Callback for live updates
    public Runnable onColorChanged;

    public ColorPicker() {
        super("Select Color: Red");
        this.colorUtility = new ColorUtility();
        this.setBackground(ColorPalette.INPUT_BACKGROUND);
        this.setForeground(ColorPalette.TEXT_LIGHT);
        this.setFocusPainted(false);

        this.addActionListener(e -> this.showColorPopup());
    }

    private void showColorPopup() {
        JPopupMenu popup = new JPopupMenu();
        popup.setBackground(ColorPalette.BACKGROUND_DARK);
        popup.setLayout(new GridLayout(2, 5, 5, 5));
        popup.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        for(String colorName : this.availableColors) {
            JButton colorBtn = new JButton();
            colorBtn.setPreferredSize(new Dimension(30, 30));
            colorBtn.setBackground(this.colorUtility.retrieveColor(colorName));
            colorBtn.setToolTipText(colorName);
            colorBtn.setFocusPainted(false);
            colorBtn.setBorder(BorderFactory.createLineBorder(ColorPalette.BACKGROUND_LIGHT));

            colorBtn.addActionListener(ev -> {
                this.selectedColorName = colorName;
                this.setText("Select Color: " + colorName);
                popup.setVisible(false);

                // NEW: Trigger the callback when a color is clicked
                if(this.onColorChanged != null) {
                    this.onColorChanged.run();
                }
            });
            popup.add(colorBtn);
        }
        popup.show(this, 0, this.getHeight());
    }

    public String getSelectedColor() {
        return this.selectedColorName;
    }
}
