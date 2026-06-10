package tabletop.ui.tabs;

import javax.swing.*;
import java.awt.*;
import tabletop.main.ApplicationCore;
import tabletop.main.DrawingSubtool;
import tabletop.main.ToolType;
import tabletop.state.ToolState;
import tabletop.ui.theme.ColorPalette;
import tabletop.ui.core.ColorPicker;

/**
 * Represents the drawing tools tab construction instance.
 *
 * @author Adi
 */
public class DrawingTabBuilder {

    private final ApplicationCore applicationCore;

    public DrawingTabBuilder(ApplicationCore applicationCore) {
        super();
        this.applicationCore = applicationCore;
    }

    public JPanel buildTab() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        panel.setBackground(ColorPalette.BACKGROUND_DARK);

        ToolState toolState = this.applicationCore.getToolState();

        // 1. Pen vs Eraser Toggle
        JPanel toolPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        toolPanel.setBackground(ColorPalette.BACKGROUND_DARK);

        JButton penBtn = new JButton("Pen");
        JButton eraserBtn = new JButton("Eraser");

        Runnable updateButtons = () -> {
            boolean isPen = toolState.getDrawingSubtool() == null || toolState.getDrawingSubtool() == DrawingSubtool.PEN;
            penBtn.setBackground(isPen ? ColorPalette.BUTTON_SUCCESS : ColorPalette.LIST_ROW_BACKGROUND);
            eraserBtn.setBackground(!isPen ? ColorPalette.BUTTON_SUCCESS : ColorPalette.LIST_ROW_BACKGROUND);
        };

        penBtn.setForeground(ColorPalette.TEXT_LIGHT);
        penBtn.setFocusPainted(false);
        penBtn.addActionListener(e -> {
            toolState.setCurrentTool(ToolType.DRAWING); // Re-activate drawing if cancelled
            toolState.setDrawingSubtool(DrawingSubtool.PEN);
            updateButtons.run();
        });

        eraserBtn.setForeground(ColorPalette.TEXT_LIGHT);
        eraserBtn.setFocusPainted(false);
        eraserBtn.addActionListener(e -> {
            toolState.setCurrentTool(ToolType.DRAWING); // Re-activate drawing if cancelled
            toolState.setDrawingSubtool(DrawingSubtool.ERASER);
            updateButtons.run();
        });

        updateButtons.run();
        toolPanel.add(penBtn);
        toolPanel.add(eraserBtn);
        panel.add(toolPanel);

        // 2. Color Picker
        JPanel colorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        colorPanel.setBackground(ColorPalette.BACKGROUND_DARK);
        JLabel colorLabel = new JLabel("Paint Color:");
        colorLabel.setForeground(ColorPalette.TEXT_LIGHT);
        ColorPicker colorPicker = new ColorPicker();
        colorPicker.onColorChanged = () -> toolState.setCurrentDrawingColor(colorPicker.getSelectedColor());
        colorPanel.add(colorLabel);
        colorPanel.add(colorPicker);
        panel.add(colorPanel);

        // 3. Stroke Width (Size)
        JPanel widthPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        widthPanel.setBackground(ColorPalette.BACKGROUND_DARK);
        JLabel widthLabel = new JLabel("Thickness:");
        widthLabel.setForeground(ColorPalette.TEXT_LIGHT);
        JSlider widthSlider = new JSlider(1, 20, toolState.getCurrentDrawingStrokeWidth());
        widthSlider.setBackground(ColorPalette.BACKGROUND_DARK);
        widthSlider.addChangeListener(e -> toolState.setCurrentDrawingStrokeWidth(widthSlider.getValue()));
        widthPanel.add(widthLabel);
        widthPanel.add(widthSlider);
        panel.add(widthPanel);

        // 4. Opacity (See-through)
        JPanel opacityPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        opacityPanel.setBackground(ColorPalette.BACKGROUND_DARK);
        JLabel opacityLabel = new JLabel("Opacity:");
        opacityLabel.setForeground(ColorPalette.TEXT_LIGHT);
        JSlider opacitySlider = new JSlider(10, 100, (int) (toolState.getCurrentDrawingOpacity() * 100));
        opacitySlider.setBackground(ColorPalette.BACKGROUND_DARK);
        opacitySlider.addChangeListener(e -> toolState.setCurrentDrawingOpacity(opacitySlider.getValue() / 100.0));
        opacityPanel.add(opacityLabel);
        opacityPanel.add(opacitySlider);
        panel.add(opacityPanel);

        // 5. Clear Drawings Button
        JButton clearBtn = new JButton("Clear Map Drawings");
        clearBtn.setBackground(ColorPalette.BUTTON_DANGER);
        clearBtn.setForeground(ColorPalette.TEXT_LIGHT);
        clearBtn.setFocusPainted(false);
        clearBtn.addActionListener(e -> {
            this.applicationCore.getDataState().getCanvasDrawings().clear();
            toolState.setCurrentTool(ToolType.TOKEN); // Stop drawing mode, revert to drag/pan
            this.applicationCore.refreshDisplay();
        });
        panel.add(clearBtn);

        return panel;
    }
}
