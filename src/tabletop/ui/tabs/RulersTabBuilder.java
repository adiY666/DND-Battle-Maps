package tabletop.ui.tabs;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;

import tabletop.main.ApplicationCore;
import tabletop.main.ToolType;
import tabletop.state.ToolState;
import tabletop.model.TemplateModel;
import tabletop.ui.theme.ColorPalette;
import tabletop.ui.core.ColorPicker;

/**
 * Represents the rulers and templates tab construction instance.
 *
 * @author Adi
 */
public class RulersTabBuilder {

    private final ApplicationCore applicationCore;

    private JComboBox<String> typeBox;
    private ColorPicker colorPicker;
    private JSpinner primarySpinner;
    private JSpinner secondarySpinner;
    private JSpinner angleSpinner;

    private JLabel primaryLabel;
    private JLabel secondaryLabel;
    private JLabel angleLabel;

    private JPanel secondaryPanel;
    private JPanel anglePanel;

    private boolean isUpdatingProgrammatically = false;

    public RulersTabBuilder(ApplicationCore applicationCore) {
        super();
        this.applicationCore = applicationCore;
    }

    public JPanel buildTab() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panel.setBackground(ColorPalette.BACKGROUND_DARK);

        ToolState toolState = this.applicationCore.getToolState();

        // 1. Dropdown Type Selection
        JPanel typePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        typePanel.setBackground(ColorPalette.BACKGROUND_DARK);
        JLabel typeLabel = new JLabel("Shape:");
        typeLabel.setForeground(ColorPalette.TEXT_LIGHT);

        this.typeBox = new JComboBox<>(new String[]{"circle", "square", "cone", "line"});
        this.typeBox.setBackground(ColorPalette.INPUT_BACKGROUND);
        this.typeBox.setForeground(ColorPalette.TEXT_LIGHT);
        this.typeBox.addActionListener(e -> this.handleTypeChange());

        // NEW: When the user clicks the dropdown list, automatically re-arm the ruler placement tool
        this.typeBox.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                if(!isUpdatingProgrammatically && toolState.getSelectedTemplateIdentifier() == null) {
                    toolState.setCurrentTool(ToolType.TEMPLATE);
                    applicationCore.refreshDisplay();
                }
            }
            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}
            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {}
        });

        typePanel.add(typeLabel);
        typePanel.add(this.typeBox);
        panel.add(typePanel);

        // 2. Color Picker
        JPanel colorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        colorPanel.setBackground(ColorPalette.BACKGROUND_DARK);
        JLabel colorLabel = new JLabel("Color:");
        colorLabel.setForeground(ColorPalette.TEXT_LIGHT);
        this.colorPicker = new ColorPicker();
        this.colorPicker.onColorChanged = () -> {
            this.pushValuesToState();
            // Re-arm if changing color for a new ruler
            if(toolState.getSelectedTemplateIdentifier() == null) {
                toolState.setCurrentTool(ToolType.TEMPLATE);
            }
        };
        colorPanel.add(colorLabel);
        colorPanel.add(this.colorPicker);
        panel.add(colorPanel);

        // 3. Primary Size
        JPanel primaryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        primaryPanel.setBackground(ColorPalette.BACKGROUND_DARK);
        this.primaryLabel = new JLabel("Radius (ft):");
        this.primaryLabel.setForeground(ColorPalette.TEXT_LIGHT);
        this.primarySpinner = new JSpinner(new SpinnerNumberModel(15.0, 1.0, 500.0, 5.0));
        this.primarySpinner.addChangeListener(this::spinnerChanged);
        primaryPanel.add(this.primaryLabel);
        primaryPanel.add(this.primarySpinner);
        panel.add(primaryPanel);

        // 4. Secondary Size
        this.secondaryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        this.secondaryPanel.setBackground(ColorPalette.BACKGROUND_DARK);
        this.secondaryLabel = new JLabel("Width (ft):");
        this.secondaryLabel.setForeground(ColorPalette.TEXT_LIGHT);
        this.secondarySpinner = new JSpinner(new SpinnerNumberModel(15.0, 1.0, 500.0, 5.0));
        this.secondarySpinner.addChangeListener(this::spinnerChanged);
        this.secondaryPanel.add(this.secondaryLabel);
        this.secondaryPanel.add(this.secondarySpinner);
        panel.add(this.secondaryPanel);

        // 5. Angle
        this.anglePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        this.anglePanel.setBackground(ColorPalette.BACKGROUND_DARK);
        this.angleLabel = new JLabel("Angle:");
        this.angleLabel.setForeground(ColorPalette.TEXT_LIGHT);
        this.angleSpinner = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 360.0, 5.0));
        this.angleSpinner.addChangeListener(this::spinnerChanged);
        this.anglePanel.add(this.angleLabel);
        this.anglePanel.add(this.angleSpinner);
        panel.add(this.anglePanel);

        // 6. Action Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        btnPanel.setBackground(ColorPalette.BACKGROUND_DARK);

        // NEW: Explicit button to drop selection and ready a new ruler
        JButton placeBtn = new JButton("+ Place New");
        placeBtn.setBackground(ColorPalette.BUTTON_SUCCESS);
        placeBtn.setForeground(ColorPalette.TEXT_LIGHT);
        placeBtn.setFocusPainted(false);
        placeBtn.addActionListener(e -> {
            toolState.setSelectedTemplateIdentifier(null);
            toolState.setCurrentTool(ToolType.TEMPLATE);
            if(this.applicationCore.onTemplateSelectionChanged != null) {
                this.applicationCore.onTemplateSelectionChanged.run();
            }
            this.applicationCore.refreshDisplay();
        });

        JButton deleteBtn = new JButton("Clear All Rulers");
        deleteBtn.setBackground(ColorPalette.BUTTON_DANGER);
        deleteBtn.setForeground(ColorPalette.TEXT_LIGHT);
        deleteBtn.setFocusPainted(false);
        deleteBtn.addActionListener(e -> {
            this.applicationCore.getDataState().getActiveTemplates().clear();
            toolState.setSelectedTemplateIdentifier(null);
            this.applicationCore.refreshDisplay();
        });

        btnPanel.add(placeBtn);
        btnPanel.add(deleteBtn);
        panel.add(btnPanel);

        this.applicationCore.onTemplateSelectionChanged = this::pullValuesFromState;

        this.pullValuesFromState();
        return panel;
    }

    private void handleTypeChange() {
        String type = (String) this.typeBox.getSelectedItem();
        if(type == null) return;

        if("circle".equals(type)) {
            this.primaryLabel.setText("Radius (ft):");
            this.secondaryPanel.setVisible(false);
            this.anglePanel.setVisible(false);
        } else if("square".equals(type)) {
            this.primaryLabel.setText("Width (ft):");
            this.secondaryLabel.setText("Length (ft):");
            this.secondaryPanel.setVisible(true);
            this.anglePanel.setVisible(true);
        } else if("cone".equals(type)) {
            this.primaryLabel.setText("Length (ft):");
            this.secondaryLabel.setText("Spread (deg):");
            this.secondaryPanel.setVisible(true);
            this.anglePanel.setVisible(true);
        } else if("line".equals(type)) {
            this.primaryLabel.setText("Length (ft):");
            this.secondaryLabel.setText("Width (ft):");
            this.secondaryPanel.setVisible(true);
            this.anglePanel.setVisible(true);
        }

        this.pushValuesToState();
    }

    private void spinnerChanged(ChangeEvent e) {
        if(!this.isUpdatingProgrammatically) {
            this.pushValuesToState();

            // Re-arm tool if setting defaults for a new ruler
            if(this.applicationCore.getToolState().getSelectedTemplateIdentifier() == null) {
                this.applicationCore.getToolState().setCurrentTool(ToolType.TEMPLATE);
            }
        }
    }

    private void pullValuesFromState() {
        this.isUpdatingProgrammatically = true;
        ToolState toolState = this.applicationCore.getToolState();
        Integer selectedId = toolState.getSelectedTemplateIdentifier();

        String type = toolState.getDefaultTemplateType();
        String color = toolState.getDefaultTemplateColor();
        double pSize = toolState.getDefaultTemplatePrimarySize();
        double sSize = toolState.getDefaultTemplateSecondarySize();
        double angle = toolState.getDefaultTemplateAngle();

        if(selectedId != null) {
            for(TemplateModel tm : this.applicationCore.getDataState().getActiveTemplates()) {
                if(tm.getIdentifier() == selectedId) {
                    type = tm.getGeometryType();
                    color = tm.getDisplayColor();
                    pSize = tm.getPrimarySize();
                    sSize = tm.getSecondarySize();
                    angle = tm.getHeadingAngle();
                    break;
                }
            }
        }

        this.typeBox.setSelectedItem(type);
        this.primarySpinner.setValue(pSize);
        this.secondarySpinner.setValue(sSize);
        this.angleSpinner.setValue(angle);

        this.handleTypeChange();
        this.isUpdatingProgrammatically = false;
    }

    private void pushValuesToState() {
        if(this.isUpdatingProgrammatically) return;

        ToolState toolState = this.applicationCore.getToolState();
        Integer selectedId = toolState.getSelectedTemplateIdentifier();

        String type = (String) this.typeBox.getSelectedItem();
        String color = this.colorPicker.getSelectedColor();
        double pSize = ((Number) this.primarySpinner.getValue()).doubleValue();
        double sSize = ((Number) this.secondarySpinner.getValue()).doubleValue();
        double angle = ((Number) this.angleSpinner.getValue()).doubleValue();

        if(selectedId != null) {
            for(TemplateModel tm : this.applicationCore.getDataState().getActiveTemplates()) {
                if(tm.getIdentifier() == selectedId) {
                    tm.setGeometryType(type);
                    tm.setDisplayColor(color);
                    tm.setPrimarySize(pSize);
                    tm.setSecondarySize(sSize);
                    tm.setHeadingAngle(angle);
                    this.applicationCore.refreshDisplay();
                    break;
                }
            }
        } else {
            toolState.setDefaultTemplateType(type);
            toolState.setDefaultTemplateColor(color);
            toolState.setDefaultTemplatePrimarySize(pSize);
            toolState.setDefaultTemplateSecondarySize(sSize);
            toolState.setDefaultTemplateAngle(angle);

            // Re-arm the tool so the next click automatically places a ruler
            toolState.setCurrentTool(ToolType.TEMPLATE);
        }
    }
}
