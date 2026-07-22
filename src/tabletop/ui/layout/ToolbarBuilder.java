package tabletop.ui.layout;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import javax.imageio.ImageIO;

import tabletop.main.ApplicationCore;
import tabletop.state.DataState;
import tabletop.state.Coordinate;
import tabletop.model.TokenModel;
import tabletop.ui.theme.ColorPalette;
import tabletop.ui.core.PlayerFrame;
import tabletop.util.SaveLoadUtility;

/**
 * Builds the top main menu toolbar.
 *
 * @author Adi
 */
public class ToolbarBuilder {

    private final ApplicationCore applicationCore;

    public ToolbarBuilder(ApplicationCore applicationCore) {
        super();
        this.applicationCore = applicationCore;
    }

    public JPanel buildToolbar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        toolbar.setBackground(ColorPalette.TOOLBAR_BACKGROUND);

        JButton saveBtn = new JButton("Save Session");
        this.styleButton(saveBtn, ColorPalette.BUTTON_SUCCESS);
        saveBtn.addActionListener(e -> this.handleSaveSession());

        JButton loadBtn = new JButton("Load Session");
        this.styleButton(loadBtn, ColorPalette.BUTTON_WARNING);
        loadBtn.addActionListener(e -> this.handleLoadSession());

        JButton loadMapBtn = new JButton("Set Background");
        this.styleButton(loadMapBtn, ColorPalette.BUTTON_PRIMARY);
        loadMapBtn.addActionListener(e -> this.handleLoadMap());

        JButton addMiniBtn = new JButton("Add Miniature");
        this.styleButton(addMiniBtn, ColorPalette.BUTTON_PRIMARY);
        addMiniBtn.addActionListener(e -> this.handleTokenAdd());

        JButton nextTurnBtn = new JButton("Next Turn (Enter)");
        this.styleButton(nextTurnBtn, ColorPalette.BUTTON_PRIMARY);
        nextTurnBtn.addActionListener(e -> this.applicationCore.advanceTurn());

        toolbar.add(saveBtn);
        toolbar.add(loadBtn);
        toolbar.add(Box.createRigidArea(new Dimension(10, 0)));
        toolbar.add(loadMapBtn);
        toolbar.add(addMiniBtn);
        toolbar.add(nextTurnBtn);

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
            if(this.applicationCore.getPlayerFrame() instanceof PlayerFrame) {
                ((PlayerFrame) this.applicationCore.getPlayerFrame()).toggleFullScreen();
            }
        });

        // --- NEW: Blackout Button instead of a ToggleSwitch ---
        JButton blackoutBtn = new JButton("Toggle Blackout");
        this.styleButton(blackoutBtn, ColorPalette.BUTTON_WARNING);
        blackoutBtn.addActionListener(e -> {
            boolean isCurrentlyBlackedOut = this.applicationCore.getToolState().isPlayerScreenBlackout();
            this.applicationCore.getToolState().setPlayerScreenBlackout(!isCurrentlyBlackedOut);
            this.applicationCore.refreshDisplay();
        });

        toolbar.add(launchPlayerBtn);
        toolbar.add(fullscreenPlayerBtn);
        toolbar.add(blackoutBtn);

        return toolbar;
    }

    private void styleButton(JButton button, Color backgroundColor) {
        button.setBackground(backgroundColor);
        button.setForeground(ColorPalette.TEXT_LIGHT);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorderPainted(false);
    }

    private void handleSaveSession() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("D&D Map Files (*.dndmap)", "dndmap"));
        if(fileChooser.showSaveDialog(this.applicationCore.getMainFrame()) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().endsWith(".dndmap")) {
                file = new File(file.getAbsolutePath() + ".dndmap");
            }
            if (SaveLoadUtility.saveSession(this.applicationCore.getDataState(), file)) {
                JOptionPane.showMessageDialog(this.applicationCore.getMainFrame(), "Session saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this.applicationCore.getMainFrame(), "Failed to save session. Check console for errors.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleLoadSession() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("D&D Map Files (*.dndmap)", "dndmap"));
        if(fileChooser.showOpenDialog(this.applicationCore.getMainFrame()) == JFileChooser.APPROVE_OPTION) {
            DataState loadedState = SaveLoadUtility.loadSession(fileChooser.getSelectedFile());
            if (loadedState != null) {
                this.applicationCore.setDataState(loadedState);

                this.applicationCore.getToolState().setSelectedTokenIdentifier(null);
                this.applicationCore.getToolState().setSelectedPinIndex(null);
                this.applicationCore.getToolState().setSelectedTemplateIdentifier(null);

                this.applicationCore.refreshDisplay();

                if(this.applicationCore.onTokenListChanged != null) this.applicationCore.onTokenListChanged.run();
                if(this.applicationCore.onSelectionChanged != null) this.applicationCore.onSelectionChanged.run();
                if(this.applicationCore.onPinSelectionChanged != null) this.applicationCore.onPinSelectionChanged.run();

                JOptionPane.showMessageDialog(this.applicationCore.getMainFrame(), "Session loaded successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this.applicationCore.getMainFrame(), "Failed to load session. File may be corrupted.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
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

                if(this.applicationCore.onTokenListChanged != null) this.applicationCore.onTokenListChanged.run();
                if(this.applicationCore.onSelectionChanged != null) this.applicationCore.onSelectionChanged.run();

            } catch(Exception exception) {
                exception.printStackTrace();
            }
        }
    }
}
