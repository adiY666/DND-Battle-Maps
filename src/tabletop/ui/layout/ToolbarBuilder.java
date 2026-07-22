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
import tabletop.ui.core.PlayerFrame;

/**
 * Builds the top main menu toolbar.
 *
 * @author Adi
 */
public class ToolbarBuilder {

    private final ApplicationCore applicationCore;

    public static ToggleSwitch snapCheckBox;
    public static ToggleSwitch blackoutCheckBox;

    public ToolbarBuilder(ApplicationCore applicationCore) {
        super();
        this.applicationCore = applicationCore;
    }

    public JPanel buildToolbar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        toolbar.setBackground(ColorPalette.TOOLBAR_BACKGROUND);

        JButton loadMapBtn = new JButton("Load Map");
        this.styleButton(loadMapBtn, ColorPalette.BUTTON_PRIMARY);
        loadMapBtn.addActionListener(e -> this.handleLoadMap());

        JButton addMiniBtn = new JButton("Add Miniature");
        this.styleButton(addMiniBtn, ColorPalette.BUTTON_PRIMARY);
        addMiniBtn.addActionListener(e -> this.handleTokenAdd());

        JButton nextTurnBtn = new JButton("Next Turn (Enter)");
        this.styleButton(nextTurnBtn, ColorPalette.BUTTON_PRIMARY);
        nextTurnBtn.addActionListener(e -> this.applicationCore.advanceTurn());

        snapCheckBox = new ToggleSwitch("Snap to Grid (S)");
        snapCheckBox.setBackground(ColorPalette.TOOLBAR_BACKGROUND);
        snapCheckBox.setForeground(ColorPalette.TEXT_LIGHT);
        snapCheckBox.setSelected(this.applicationCore.getDataState().isSnapToGrid());
        snapCheckBox.addActionListener(e -> {
            this.applicationCore.getDataState().setSnapToGrid(snapCheckBox.isSelected());
        });

        toolbar.add(loadMapBtn);
        toolbar.add(addMiniBtn);
        toolbar.add(nextTurnBtn);
        toolbar.add(snapCheckBox);

        toolbar.add(Box.createRigidArea(new Dimension(20, 0)));

        JButton launchPlayerBtn = new JButton("Launch Player View");
        this.styleButton(launchPlayerBtn, ColorPalette.BUTTON_WARNING);
        launchPlayerBtn.addActionListener(e -> {
            this.applicationCore.getPlayerFrame().setVisible(true);
            this.applicationCore.refreshDisplay();
        });

        JButton fullscreenPlayerBtn = new JButton("Toggle Fullscreen (F11)");
        this.styleButton(fullscreenPlayerBtn, ColorPalette.BUTTON_WARNING);
        fullscreenPlayerBtn.addActionListener(e -> {
            // Check if it's our specific PlayerFrame to safely call the custom method
            if(this.applicationCore.getPlayerFrame() instanceof PlayerFrame) {
                ((PlayerFrame) this.applicationCore.getPlayerFrame()).toggleFullScreen();
            }
        });

        blackoutCheckBox = new ToggleSwitch("Stop Sharing (Blackout)");
        blackoutCheckBox.setBackground(ColorPalette.TOOLBAR_BACKGROUND);
        blackoutCheckBox.setForeground(ColorPalette.BUTTON_DANGER);
        blackoutCheckBox.setSelected(this.applicationCore.getToolState().isPlayerScreenBlackout());
        blackoutCheckBox.addActionListener(e -> {
            this.applicationCore.getToolState().setPlayerScreenBlackout(blackoutCheckBox.isSelected());
            this.applicationCore.refreshDisplay();
        });

        toolbar.add(launchPlayerBtn);
        toolbar.add(fullscreenPlayerBtn);
        toolbar.add(blackoutCheckBox);

        return toolbar;
    }

    private void styleButton(JButton button, Color backgroundColor) {
        button.setBackground(backgroundColor);
        button.setForeground(ColorPalette.TEXT_LIGHT);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorderPainted(false);
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
