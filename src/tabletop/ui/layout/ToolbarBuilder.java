package tabletop.ui.layout;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import javax.imageio.ImageIO;

import tabletop.main.ApplicationCore;
import tabletop.state.DataState;
import tabletop.state.Coordinate;
import tabletop.model.TokenModel;
import tabletop.ui.theme.ColorPalette;
import tabletop.ui.core.ToggleSwitch;

/**
 * Builds the top main menu toolbar.
 *
 * @author Adi
 */
public class ToolbarBuilder {

    private final ApplicationCore applicationCore;

    // We now use the custom ToggleSwitch
    public static ToggleSwitch snapCheckBox;

    public ToolbarBuilder(ApplicationCore applicationCore) {
        super();
        this.applicationCore = applicationCore;
    }

    public JPanel buildToolbar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        toolbar.setBackground(ColorPalette.TOOLBAR_BACKGROUND);

        JButton loadMapBtn = new JButton("Load Map");
        loadMapBtn.setBackground(ColorPalette.BUTTON_PRIMARY);
        loadMapBtn.setForeground(ColorPalette.TEXT_LIGHT);
        loadMapBtn.setFocusPainted(false);
        loadMapBtn.addActionListener(e -> this.handleLoadMap());

        JButton addMiniBtn = new JButton("Add Miniature");
        addMiniBtn.setBackground(ColorPalette.BUTTON_PRIMARY);
        addMiniBtn.setForeground(ColorPalette.TEXT_LIGHT);
        addMiniBtn.setFocusPainted(false);
        addMiniBtn.addActionListener(e -> this.handleTokenAdd());

        // Uses the new custom slider switch component
        snapCheckBox = new ToggleSwitch("Snap to Grid (S)");
        snapCheckBox.setBackground(ColorPalette.TOOLBAR_BACKGROUND);
        snapCheckBox.setForeground(ColorPalette.TEXT_LIGHT);
        snapCheckBox.setSelected(this.applicationCore.getDataState().isSnapToGrid());
        snapCheckBox.addActionListener(e -> {
            this.applicationCore.getDataState().setSnapToGrid(snapCheckBox.isSelected());
        });

        toolbar.add(loadMapBtn);
        toolbar.add(addMiniBtn);
        toolbar.add(snapCheckBox);

        return toolbar;
    }

    private void handleLoadMap() {
        JFileChooser fileChooser = new JFileChooser();
        if(fileChooser.showOpenDialog(this.applicationCore.getMainFrame()) == JFileChooser.APPROVE_OPTION) {
            this.applicationCore.getDataState().setBackgroundFilepath(fileChooser.getSelectedFile().getAbsolutePath());
            this.applicationCore.refreshDisplay();
        }
    }

    private void handleTokenAdd() {
        JFileChooser fileChooser = new JFileChooser();
        if(fileChooser.showOpenDialog(this.applicationCore.getMainFrame()) == JFileChooser.APPROVE_OPTION) {
            try {
                String imagePath = fileChooser.getSelectedFile().getAbsolutePath();
                java.awt.image.BufferedImage originalImage = ImageIO.read(new File(imagePath));
                String characterName = JOptionPane.showInputDialog("Enter character's name:", "Miniature");
                if(characterName == null) characterName = "Mini";

                DataState dataState = this.applicationCore.getDataState();
                Coordinate logicalPosition = dataState.convertScreenToLogical(this.applicationCore.getCanvasPanel().getWidth() / 2, this.applicationCore.getCanvasPanel().getHeight() / 2);

                int newIdentifier = dataState.getNextTokenIdentifier();
                dataState.setNextTokenIdentifier(newIdentifier + 1);

                TokenModel tokenModel = new TokenModel(newIdentifier, characterName, logicalPosition.getCoordinateHorizontal(), logicalPosition.getCoordinateVertical(), imagePath);
                tokenModel.setOriginalImage(originalImage);
                dataState.getActiveTokens().put(newIdentifier, tokenModel);
                dataState.getTokenOrdering().add(newIdentifier);

                this.applicationCore.getToolState().setSelectedTokenIdentifier(newIdentifier);
                this.applicationCore.refreshDisplay();

                if(this.applicationCore.onTokenListChanged != null) {
                    this.applicationCore.onTokenListChanged.run();
                }
                if(this.applicationCore.onSelectionChanged != null) {
                    this.applicationCore.onSelectionChanged.run();
                }

            } catch(Exception exception) {
                exception.printStackTrace();
            }
        }
    }
}
