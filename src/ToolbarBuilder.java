import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Represents the toolbar construction instance.
 * <p></p>
 * Assembles the top menu actions for this ToolbarBuilder.
 *
 * @author Adi
 */
class ToolbarBuilder {

    private final ApplicationCore applicationCore;

    public ToolbarBuilder(ApplicationCore applicationCore) {
        super();
        this.applicationCore = applicationCore;
    }

    /**
     * Builds the final toolbar.
     *
     * @return the ready toolbar component
     */
    public JToolBar buildToolbar() {
        JToolBar mainToolbar = new JToolBar();
        mainToolbar.setFloatable(false);
        mainToolbar.setBackground(new Color(43, 43, 43));

        JButton buttonBackground = this.createButton("Background", event -> this.handleBackgroundLoad());
        JButton buttonAddToken = this.createButton("+ Miniature", event -> this.handleTokenAdd(), new Color(76, 175, 80));
        JButton buttonSave = this.createButton("Save", event -> this.handleSave());
        JButton buttonLoad = this.createButton("Load", event -> this.handleLoad());

        JCheckBox checkSnapping = new JCheckBox("Snap to Grid", this.applicationCore.getDataState().isGridSnapping());
        checkSnapping.setBackground(mainToolbar.getBackground());
        checkSnapping.setForeground(Color.WHITE);
        checkSnapping.addActionListener(event -> this.applicationCore.getDataState().setGridSnapping(checkSnapping.isSelected()));

        mainToolbar.add(buttonBackground);
        mainToolbar.addSeparator();
        mainToolbar.add(buttonAddToken);
        mainToolbar.addSeparator();
        mainToolbar.add(buttonSave);
        mainToolbar.add(buttonLoad);
        mainToolbar.addSeparator();
        mainToolbar.add(checkSnapping);

        return mainToolbar;
    }

    private JButton createButton(String labelText, ActionListener actionListener) {
        return this.createButton(labelText, actionListener, null);
    }

    private JButton createButton(String labelText, ActionListener actionListener, Color backgroundColor) {
        JButton newButton = new JButton(labelText);
        newButton.addActionListener(actionListener);
        newButton.setFocusable(false);
        if (backgroundColor != null) {
            newButton.setBackground(backgroundColor);
            newButton.setForeground(Color.WHITE);
            newButton.setFont(newButton.getFont().deriveFont(Font.BOLD));
        }
        return newButton;
    }

    private void handleBackgroundLoad() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(this.applicationCore.getMainFrame()) == JFileChooser.APPROVE_OPTION) {
            this.applicationCore.getDataState().setBackgroundFilepath(fileChooser.getSelectedFile().getAbsolutePath());
            this.applicationCore.getDataState().setPanHorizontal(0.0);
            this.applicationCore.getDataState().setPanVertical(0.0);
            this.applicationCore.getDataState().setZoomLevel(1.0);
            this.applicationCore.refreshDisplay();
        }
    }

    private void handleTokenAdd() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(this.applicationCore.getMainFrame()) == JFileChooser.APPROVE_OPTION) {
            try {
                String imagePath = fileChooser.getSelectedFile().getAbsolutePath();
                java.awt.image.BufferedImage originalImage = ImageIO.read(new File(imagePath));
                String characterName = JOptionPane.showInputDialog("Enter character's name:", "Miniature");
                if (characterName == null) characterName = "Mini";

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

                // NEW: Trigger the token tab to update its list dynamically
                if (this.applicationCore.onTokenListChanged != null) {
                    this.applicationCore.onTokenListChanged.run();
                }

            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    private void handleSave() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(this.applicationCore.getMainFrame()) == JFileChooser.APPROVE_OPTION) {
            FileUtility fileUtility = new FileUtility();
            fileUtility.saveState(this.applicationCore.getDataState(), new File(fileChooser.getSelectedFile() + ".sav"));
        }
    }

    private void handleLoad() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(this.applicationCore.getMainFrame()) == JFileChooser.APPROVE_OPTION) {
            FileUtility fileUtility = new FileUtility();
            DataState loadedState = fileUtility.loadState(fileChooser.getSelectedFile());
            if (loadedState != null) {
                // To safely swap the datastate, a setter in ApplicationCore would be used,
                // but since fields are final to meet constraints, we map the properties over.
                // Simplified implementation restores basic map info.
                this.applicationCore.getDataState().setBackgroundFilepath(loadedState.getBackgroundFilepath());
                this.applicationCore.refreshDisplay();
            }
        }
    }

}
