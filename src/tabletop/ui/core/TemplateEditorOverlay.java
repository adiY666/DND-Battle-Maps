package tabletop.ui.core;

import javax.swing.*;
import java.awt.*;
import tabletop.main.ApplicationCore;
import tabletop.model.TemplateModel;
import tabletop.state.Coordinate;
import tabletop.state.DataState;
import tabletop.state.ToolState;
import tabletop.ui.theme.ColorPalette;

/**
 * Represents a floating editor panel that anchors to selected map templates.
 *
 * @author Adi
 */
public class TemplateEditorOverlay extends JPanel {

    private final ApplicationCore applicationCore;
    private final JSpinner primarySpinner;
    private final JSpinner secondarySpinner;
    private final JSpinner angleSpinner;
    private final JLabel primaryLabel;
    private final JLabel secondaryLabel;
    private final JLabel angleLabel;
    private boolean isUpdating = false;

    public TemplateEditorOverlay(ApplicationCore applicationCore) {
        super();
        this.applicationCore = applicationCore;
        this.setBackground(new Color(43, 43, 43, 230));
        this.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(ColorPalette.BUTTON_PRIMARY, 2), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        this.setLayout(new GridLayout(3, 2, 5, 5));

        this.primaryLabel = new JLabel("Radius:");
        this.primaryLabel.setForeground(ColorPalette.TEXT_LIGHT);
        this.primarySpinner = new JSpinner(new SpinnerNumberModel(15.0, 1.0, 500.0, 5.0));

        this.secondaryLabel = new JLabel("Width:");
        this.secondaryLabel.setForeground(ColorPalette.TEXT_LIGHT);
        this.secondarySpinner = new JSpinner(new SpinnerNumberModel(15.0, 1.0, 500.0, 5.0));

        this.angleLabel = new JLabel("Angle:");
        this.angleLabel.setForeground(ColorPalette.TEXT_LIGHT);
        this.angleSpinner = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 360.0, 5.0));

        this.primarySpinner.addChangeListener(e -> this.pushChanges());
        this.secondarySpinner.addChangeListener(e -> this.pushChanges());
        this.angleSpinner.addChangeListener(e -> this.pushChanges());

        this.add(this.primaryLabel);
        this.add(this.primarySpinner);
        this.add(this.secondaryLabel);
        this.add(this.secondarySpinner);
        this.add(this.angleLabel);
        this.add(this.angleSpinner);
    }

    private void pushChanges() {
        if(this.isUpdating) return;
        ToolState toolState = this.applicationCore.getToolState();
        Integer selectedId = toolState.getSelectedTemplateIdentifier();

        if(selectedId != null) {
            for(TemplateModel tm : this.applicationCore.getDataState().getActiveTemplates()) {
                if(tm.getIdentifier() == selectedId) {
                    tm.setPrimarySize(((Number) this.primarySpinner.getValue()).doubleValue());
                    tm.setSecondarySize(((Number) this.secondarySpinner.getValue()).doubleValue());
                    tm.setHeadingAngle(((Number) this.angleSpinner.getValue()).doubleValue());

                    if(this.applicationCore.onTemplateSelectionChanged != null) {
                        this.applicationCore.onTemplateSelectionChanged.run();
                    }
                    this.applicationCore.refreshDisplay();
                    break;
                }
            }
        }
    }

    public void updateOverlayPositionAndData() {
        ToolState toolState = this.applicationCore.getToolState();
        DataState dataState = this.applicationCore.getDataState();
        Integer selectedId = toolState.getSelectedTemplateIdentifier();

        if(selectedId == null) {
            this.setVisible(false);
            return;
        }

        TemplateModel activeTemplate = null;
        for(TemplateModel tm : dataState.getActiveTemplates()) {
            if(tm.getIdentifier() == selectedId) {
                activeTemplate = tm;
                break;
            }
        }

        if(activeTemplate == null) {
            this.setVisible(false);
            return;
        }

        this.isUpdating = true;

        String type = activeTemplate.getGeometryType();
        if("circle".equals(type)) {
            this.primaryLabel.setText("Radius (ft):");
            this.primaryLabel.setVisible(true); this.primarySpinner.setVisible(true);
            this.secondaryLabel.setVisible(false); this.secondarySpinner.setVisible(false);
            this.angleLabel.setVisible(false); this.angleSpinner.setVisible(false);
        } else if("square".equals(type)) {
            this.primaryLabel.setText("Width (ft):");
            this.secondaryLabel.setText("Length (ft):");
            this.primaryLabel.setVisible(true); this.primarySpinner.setVisible(true);
            this.secondaryLabel.setVisible(true); this.secondarySpinner.setVisible(true);
            this.angleLabel.setVisible(true); this.angleSpinner.setVisible(true);
        } else if("cone".equals(type)) {
            this.primaryLabel.setText("Length (ft):");
            this.secondaryLabel.setText("Spread (deg):");
            this.primaryLabel.setVisible(true); this.primarySpinner.setVisible(true);
            this.secondaryLabel.setVisible(true); this.secondarySpinner.setVisible(true);
            this.angleLabel.setVisible(true); this.angleSpinner.setVisible(true);
        } else if("line".equals(type)) {
            this.primaryLabel.setText("Length (ft):");
            this.secondaryLabel.setText("Width (ft):");
            this.primaryLabel.setVisible(true); this.primarySpinner.setVisible(true);
            this.secondaryLabel.setVisible(true); this.secondarySpinner.setVisible(true);
            this.angleLabel.setVisible(true); this.angleSpinner.setVisible(true);
        }

        if(((Number) this.primarySpinner.getValue()).doubleValue() != activeTemplate.getPrimarySize()) {
            this.primarySpinner.setValue(activeTemplate.getPrimarySize());
        }
        if(((Number) this.secondarySpinner.getValue()).doubleValue() != activeTemplate.getSecondarySize()) {
            this.secondarySpinner.setValue(activeTemplate.getSecondarySize());
        }
        if(((Number) this.angleSpinner.getValue()).doubleValue() != activeTemplate.getHeadingAngle()) {
            this.angleSpinner.setValue(activeTemplate.getHeadingAngle());
        }

        Coordinate screenPos = dataState.convertLogicalToScreen(activeTemplate.getPositionHorizontal(), activeTemplate.getPositionVertical());
        Dimension prefSize = this.getPreferredSize();

        int targetX = (int) screenPos.getCoordinateHorizontal() - (prefSize.width / 2);
        int targetY = (int) screenPos.getCoordinateVertical() - prefSize.height - 20;

        targetX = Math.max(0, targetX);
        targetY = Math.max(0, targetY);

        Rectangle currentBounds = this.getBounds();
        if(currentBounds.x != targetX || currentBounds.y != targetY || currentBounds.width != prefSize.width || currentBounds.height != prefSize.height) {
            this.setBounds(targetX, targetY, prefSize.width, prefSize.height);
        }

        if(!this.isVisible()) {
            this.setVisible(true);
        }

        this.isUpdating = false;
    }
}
