package tabletop.ui.tabs;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import tabletop.main.ApplicationCore;
import tabletop.state.DataState;
import tabletop.model.TokenModel;
import tabletop.model.EffectModel;
import tabletop.ui.theme.ColorPalette;
import tabletop.ui.core.ColorPicker;

/**
 * Represents the tokens tab construction instance.
 *
 * @author Adi
 */
public class TokensTabBuilder {

    private final ApplicationCore applicationCore;
    private JPanel mainCardPanel;
    private CardLayout cardLayout;
    private JPanel listContainer;
    private JPanel detailsContainer;

    public TokensTabBuilder(ApplicationCore applicationCore) {
        super();
        this.applicationCore = applicationCore;
    }

    public JPanel buildTab() {
        JPanel tabContainer = new JPanel(new BorderLayout());
        tabContainer.setBackground(ColorPalette.BACKGROUND_DARK);

        this.cardLayout = new CardLayout();
        this.mainCardPanel = new JPanel(this.cardLayout);
        this.mainCardPanel.setBackground(ColorPalette.BACKGROUND_DARK);

        // --- View 1: The Token List ---
        JPanel listWrapper = new JPanel(new BorderLayout());
        listWrapper.setBackground(ColorPalette.BACKGROUND_DARK);

        JPanel topButtons = new JPanel(new GridLayout(1, 2, 5, 0));
        topButtons.setBackground(ColorPalette.BACKGROUND_DARK);

        JButton clearButton = new JButton("Clear All");
        clearButton.setBackground(ColorPalette.BUTTON_DANGER);
        clearButton.setForeground(ColorPalette.TEXT_LIGHT);
        clearButton.setFocusPainted(false);
        clearButton.addActionListener(event -> {
            this.applicationCore.getDataState().getActiveTokens().clear();
            this.applicationCore.getDataState().getTokenOrdering().clear();
            this.applicationCore.getToolState().setSelectedTokenIdentifier(null);
            this.applicationCore.refreshDisplay();
            this.refreshTokensList();
            this.refreshDetailsPanel();
            if(this.applicationCore.onEffectsChanged != null) this.applicationCore.onEffectsChanged.run();
        });

        JButton nextTurnBtn = new JButton("Next Turn (Enter)");
        nextTurnBtn.setBackground(ColorPalette.BUTTON_PRIMARY);
        nextTurnBtn.setForeground(ColorPalette.TEXT_LIGHT);
        nextTurnBtn.setFocusPainted(false);
        nextTurnBtn.addActionListener(event -> this.applicationCore.advanceTurn());

        topButtons.add(clearButton);
        topButtons.add(nextTurnBtn);
        listWrapper.add(topButtons, BorderLayout.NORTH);

        this.listContainer = new JPanel();
        this.listContainer.setLayout(new BoxLayout(this.listContainer, BoxLayout.Y_AXIS));
        this.listContainer.setBackground(ColorPalette.BACKGROUND_DARK);
        JScrollPane listScroll = new JScrollPane(this.listContainer);
        listScroll.setBorder(null);
        listWrapper.add(listScroll, BorderLayout.CENTER);


        // --- View 2: The Selected Details ---
        JPanel detailsWrapper = new JPanel(new BorderLayout());
        detailsWrapper.setBackground(ColorPalette.BACKGROUND_DARK);

        JButton backButton = new JButton("⬅ Back to Roster");
        backButton.setBackground(ColorPalette.BUTTON_PRIMARY);
        backButton.setForeground(ColorPalette.TEXT_LIGHT);
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> {
            this.applicationCore.getToolState().setSelectedTokenIdentifier(null);
            if(this.applicationCore.onSelectionChanged != null) {
                this.applicationCore.onSelectionChanged.run();
            }
            this.applicationCore.refreshDisplay();
        });
        detailsWrapper.add(backButton, BorderLayout.NORTH);

        this.detailsContainer = new JPanel();
        this.detailsContainer.setLayout(new BoxLayout(this.detailsContainer, BoxLayout.Y_AXIS));
        this.detailsContainer.setBackground(ColorPalette.BACKGROUND_DARK);
        JScrollPane detailsScroll = new JScrollPane(this.detailsContainer);

        javax.swing.border.TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(ColorPalette.BACKGROUND_LIGHT), "Selected Details");
        border.setTitleColor(ColorPalette.TEXT_DARK);
        detailsScroll.setBorder(border);
        detailsWrapper.add(detailsScroll, BorderLayout.CENTER);

        // Bind views
        this.mainCardPanel.add(listWrapper, "LIST");
        this.mainCardPanel.add(detailsWrapper, "DETAILS");

        tabContainer.add(this.mainCardPanel, BorderLayout.CENTER);

        this.applicationCore.onTokenListChanged = this::refreshTokensList;
        this.applicationCore.onSelectionChanged = this::refreshDetailsPanel;

        this.refreshTokensList();
        this.refreshDetailsPanel();
        return tabContainer;
    }

    public void refreshTokensList() {
        this.listContainer.removeAll();
        DataState dataState = this.applicationCore.getDataState();

        for(int i = dataState.getTokenOrdering().size() - 1; i >= 0; i--) {
            Integer tokenId = dataState.getTokenOrdering().get(i);
            TokenModel token = dataState.getActiveTokens().get(tokenId);
            if(token == null) continue;

            JPanel row = new JPanel(new BorderLayout());
            row.setBackground(ColorPalette.LIST_ROW_BACKGROUND);
            row.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

            JLabel nameLabel = new JLabel(token.getDisplayName());
            nameLabel.setForeground(ColorPalette.TEXT_LIGHT);
            row.add(nameLabel, BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
            buttonPanel.setBackground(ColorPalette.LIST_ROW_BACKGROUND);

            JButton jumpBtn = new JButton("Jump");
            jumpBtn.setBackground(ColorPalette.BUTTON_PRIMARY);
            jumpBtn.setForeground(ColorPalette.TEXT_LIGHT);
            jumpBtn.setMargin(new Insets(2, 5, 2, 5));
            jumpBtn.addActionListener(e -> this.jumpToToken(token));

            JButton editBtn = new JButton("Edit");
            editBtn.setBackground(ColorPalette.BUTTON_WARNING);
            editBtn.setForeground(ColorPalette.TEXT_LIGHT);
            editBtn.setMargin(new Insets(2, 5, 2, 5));
            editBtn.addActionListener(e -> this.editToken(token));

            JButton deleteBtn = new JButton("X");
            deleteBtn.setBackground(ColorPalette.BUTTON_DANGER);
            deleteBtn.setForeground(ColorPalette.TEXT_LIGHT);
            deleteBtn.setMargin(new Insets(2, 5, 2, 5));
            deleteBtn.addActionListener(e -> this.deleteToken(token));

            buttonPanel.add(jumpBtn);
            buttonPanel.add(editBtn);
            buttonPanel.add(deleteBtn);
            row.add(buttonPanel, BorderLayout.EAST);

            this.listContainer.add(row);
            this.listContainer.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        this.listContainer.revalidate();
        this.listContainer.repaint();
    }

    public void refreshDetailsPanel() {
        this.detailsContainer.removeAll();
        Integer selectedId = this.applicationCore.getToolState().getSelectedTokenIdentifier();

        if(selectedId == null) {
            this.cardLayout.show(this.mainCardPanel, "LIST");
        } else {
            this.cardLayout.show(this.mainCardPanel, "DETAILS");

            TokenModel token = this.applicationCore.getDataState().getActiveTokens().get(selectedId);
            if(token != null) {

                JPanel imgPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
                imgPanel.setBackground(ColorPalette.BACKGROUND_DARK);
                if(token.getOriginalImage() != null) {
                    Image scaledImg = token.getOriginalImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
                    imgPanel.add(new JLabel(new ImageIcon(scaledImg)));
                }
                JButton changeImgBtn = new JButton("Change Image");
                changeImgBtn.setBackground(ColorPalette.BUTTON_PRIMARY);
                changeImgBtn.setForeground(ColorPalette.TEXT_LIGHT);
                changeImgBtn.addActionListener(e -> {
                    JFileChooser fileChooser = new JFileChooser();
                    if(fileChooser.showOpenDialog(this.applicationCore.getMainFrame()) == JFileChooser.APPROVE_OPTION) {
                        try {
                            String imagePath = fileChooser.getSelectedFile().getAbsolutePath();
                            BufferedImage newImg = ImageIO.read(new File(imagePath));
                            token.setImageFilepath(imagePath);
                            token.setOriginalImage(newImg);
                            this.refreshDetailsPanel();
                            this.applicationCore.refreshDisplay();
                        } catch(Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });
                imgPanel.add(changeImgBtn);
                this.detailsContainer.add(imgPanel);

                JPanel formPanel = new JPanel(new GridLayout(3, 2, 5, 5));
                formPanel.setBackground(ColorPalette.BACKGROUND_DARK);

                JTextField nameField = new JTextField(token.getDisplayName());
                nameField.setBackground(ColorPalette.INPUT_BACKGROUND);
                nameField.setForeground(ColorPalette.TEXT_LIGHT);

                JTextField speedField = new JTextField(String.valueOf(token.getMovementSpeed() * 5));
                speedField.setBackground(ColorPalette.INPUT_BACKGROUND);
                speedField.setForeground(ColorPalette.TEXT_LIGHT);

                JTextField sizeField = new JTextField(String.valueOf(token.getGridScale()));
                sizeField.setBackground(ColorPalette.INPUT_BACKGROUND);
                sizeField.setForeground(ColorPalette.TEXT_LIGHT);

                JLabel nameLabel = new JLabel("Name:");
                nameLabel.setForeground(ColorPalette.TEXT_LIGHT);
                JLabel speedLabel = new JLabel("Speed (ft):");
                speedLabel.setForeground(ColorPalette.TEXT_LIGHT);
                JLabel sizeLabel = new JLabel("Size (sq):");
                sizeLabel.setForeground(ColorPalette.TEXT_LIGHT);

                formPanel.add(nameLabel);
                formPanel.add(nameField);
                formPanel.add(speedLabel);
                formPanel.add(speedField);
                formPanel.add(sizeLabel);
                formPanel.add(sizeField);
                this.detailsContainer.add(formPanel);

                JButton saveBtn = new JButton("Save Profile");
                saveBtn.setBackground(ColorPalette.BUTTON_SUCCESS);
                saveBtn.setForeground(ColorPalette.TEXT_LIGHT);
                saveBtn.addActionListener(e -> {
                    try {
                        token.setDisplayName(nameField.getText().trim());
                        token.setMovementSpeed(Integer.parseInt(speedField.getText().trim()) / 5);
                        token.setGridScale(Integer.parseInt(sizeField.getText().trim()));
                        this.refreshTokensList();
                        if(this.applicationCore.onEffectsChanged != null) this.applicationCore.onEffectsChanged.run();
                        this.applicationCore.refreshDisplay();
                    } catch(NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this.applicationCore.getMainFrame(), "Invalid format.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                });
                this.detailsContainer.add(Box.createRigidArea(new Dimension(0, 5)));
                this.detailsContainer.add(saveBtn);
                this.detailsContainer.add(Box.createRigidArea(new Dimension(0, 10)));

                JButton toggleRangeBtn = new JButton(token.isShowMovementRange() ? "Hide Range (R)" : "Show Range (R)");
                toggleRangeBtn.setBackground(ColorPalette.BUTTON_PRIMARY);
                toggleRangeBtn.setForeground(ColorPalette.TEXT_LIGHT);
                toggleRangeBtn.addActionListener(e -> {
                    token.setShowMovementRange(!token.isShowMovementRange());
                    this.refreshDetailsPanel();
                    this.applicationCore.refreshDisplay();
                });
                this.detailsContainer.add(toggleRangeBtn);
                this.detailsContainer.add(Box.createRigidArea(new Dimension(0, 5)));

                JButton addEffectBtn = new JButton("+ Add Effect");
                addEffectBtn.setBackground(ColorPalette.BUTTON_WARNING);
                addEffectBtn.setForeground(ColorPalette.TEXT_LIGHT);
                addEffectBtn.addActionListener(e -> this.showAddEffectDialog(token));
                this.detailsContainer.add(addEffectBtn);
                this.detailsContainer.add(Box.createRigidArea(new Dimension(0, 5)));

                JPanel effectsListPanel = new JPanel();
                effectsListPanel.setLayout(new BoxLayout(effectsListPanel, BoxLayout.Y_AXIS));
                effectsListPanel.setBackground(ColorPalette.BACKGROUND_DARK);

                for(EffectModel effect : token.getActiveEffects()) {
                    JPanel effRow = new JPanel(new BorderLayout());
                    effRow.setBackground(ColorPalette.LIST_ROW_BACKGROUND);
                    effRow.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
                    effRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

                    JLabel effLabel = new JLabel(effect.getEffectName() + " (" + effect.getRemainingTurns() + " turns)");
                    effLabel.setForeground(ColorPalette.TEXT_LIGHT);
                    effRow.add(effLabel, BorderLayout.CENTER);

                    JButton removeEffBtn = new JButton("X");
                    removeEffBtn.setBackground(ColorPalette.BUTTON_DANGER);
                    removeEffBtn.setForeground(ColorPalette.TEXT_LIGHT);
                    removeEffBtn.setMargin(new Insets(0, 5, 0, 5));
                    removeEffBtn.addActionListener(e -> {
                        token.getActiveEffects().remove(effect);
                        this.refreshDetailsPanel();
                        if(this.applicationCore.onEffectsChanged != null) this.applicationCore.onEffectsChanged.run();
                        this.applicationCore.refreshDisplay();
                    });
                    effRow.add(removeEffBtn, BorderLayout.EAST);

                    effectsListPanel.add(effRow);
                    effectsListPanel.add(Box.createRigidArea(new Dimension(0, 5)));
                }

                JScrollPane effectsScroll = new JScrollPane(effectsListPanel);
                effectsScroll.setPreferredSize(new Dimension(0, 120));
                effectsScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
                effectsScroll.setBorder(BorderFactory.createLineBorder(ColorPalette.BACKGROUND_LIGHT));
                this.detailsContainer.add(effectsScroll);
            }
        }
        this.detailsContainer.revalidate();
        this.detailsContainer.repaint();
    }

    private void showAddEffectDialog(TokenModel token) {
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        panel.setBackground(ColorPalette.BACKGROUND_DARK);

        JTextField nameField = new JTextField();
        JTextField durationField = new JTextField("5");
        ColorPicker colorPicker = new ColorPicker();

        nameField.setBackground(ColorPalette.INPUT_BACKGROUND);
        nameField.setForeground(ColorPalette.TEXT_LIGHT);
        durationField.setBackground(ColorPalette.INPUT_BACKGROUND);
        durationField.setForeground(ColorPalette.TEXT_LIGHT);

        JLabel nameLabel = new JLabel("Effect Name:");
        nameLabel.setForeground(ColorPalette.TEXT_LIGHT);
        JLabel durLabel = new JLabel("Duration (Turns):");
        durLabel.setForeground(ColorPalette.TEXT_LIGHT);
        JLabel colorLabel = new JLabel("Color:");
        colorLabel.setForeground(ColorPalette.TEXT_LIGHT);

        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(durLabel);
        panel.add(durationField);
        panel.add(colorLabel);
        panel.add(colorPicker);

        JButton okButton = new JButton("Finish");
        okButton.setBackground(ColorPalette.BUTTON_SUCCESS);
        okButton.setForeground(ColorPalette.TEXT_LIGHT);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBackground(ColorPalette.BUTTON_SUCCESS);
        cancelButton.setForeground(ColorPalette.TEXT_LIGHT);

        Object[] options = {okButton, cancelButton};
        JOptionPane optionPane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null, options, options[0]);
        JDialog dialog = optionPane.createDialog(this.applicationCore.getMainFrame(), "Add Effect");

        okButton.addActionListener(e -> {
            optionPane.setValue(JOptionPane.OK_OPTION);
            dialog.dispose();
        });

        cancelButton.addActionListener(e -> {
            optionPane.setValue(JOptionPane.CANCEL_OPTION);
            dialog.dispose();
        });

        dialog.setVisible(true);

        Object result = optionPane.getValue();
        if(result != null && result.equals(JOptionPane.OK_OPTION)) {
            try {
                String effectName = nameField.getText().trim();
                int turns = Integer.parseInt(durationField.getText().trim());
                String color = colorPicker.getSelectedColor();

                token.getActiveEffects().add(new EffectModel(effectName, turns, color));
                this.refreshDetailsPanel();
                if(this.applicationCore.onEffectsChanged != null) this.applicationCore.onEffectsChanged.run();
                this.applicationCore.refreshDisplay();
            } catch(NumberFormatException ex) {
                JOptionPane.showMessageDialog(this.applicationCore.getMainFrame(), "Invalid duration.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void jumpToToken(TokenModel token) {
        DataState dataState = this.applicationCore.getDataState();
        int canvasWidth = this.applicationCore.getCanvasPanel().getWidth();
        int canvasHeight = this.applicationCore.getCanvasPanel().getHeight();
        double targetX = (canvasWidth / 2.0) - ((token.getPositionHorizontal() + token.getGridScale() / 2.0) * dataState.calculateCellDimension());
        double targetY = (canvasHeight / 2.0) - ((token.getPositionVertical() + token.getGridScale() / 2.0) * dataState.calculateCellDimension());

        dataState.setPanHorizontal(targetX);
        dataState.setPanVertical(targetY);
        this.applicationCore.getToolState().setSelectedTokenIdentifier(token.getIdentifier());
        if(this.applicationCore.onSelectionChanged != null) {
            this.applicationCore.onSelectionChanged.run();
        }
        this.applicationCore.refreshDisplay();
    }

    private void editToken(TokenModel token) {
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        panel.setBackground(ColorPalette.BACKGROUND_DARK);

        JTextField nameField = new JTextField(token.getDisplayName());
        JTextField speedField = new JTextField(String.valueOf(token.getMovementSpeed() * 5));
        JTextField sizeField = new JTextField(String.valueOf(token.getGridScale()));

        nameField.setBackground(ColorPalette.INPUT_BACKGROUND);
        nameField.setForeground(ColorPalette.TEXT_LIGHT);
        speedField.setBackground(ColorPalette.INPUT_BACKGROUND);
        speedField.setForeground(ColorPalette.TEXT_LIGHT);
        sizeField.setBackground(ColorPalette.INPUT_BACKGROUND);
        sizeField.setForeground(ColorPalette.TEXT_LIGHT);

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setForeground(ColorPalette.TEXT_LIGHT);

        JLabel speedLabel = new JLabel("Speed (ft):");
        speedLabel.setForeground(ColorPalette.TEXT_LIGHT);

        JLabel sizeLabel = new JLabel("Size (squares):");
        sizeLabel.setForeground(ColorPalette.TEXT_LIGHT);

        JLabel layerLabel = new JLabel("Layering:");
        layerLabel.setForeground(ColorPalette.TEXT_LIGHT);

        JPanel layerBtnPanel = new JPanel(new GridLayout(1, 2, 5, 0));
        layerBtnPanel.setBackground(ColorPalette.BACKGROUND_DARK);

        JButton backwardBtn = new JButton("Back");
        backwardBtn.setBackground(ColorPalette.BUTTON_PRIMARY);
        backwardBtn.setForeground(ColorPalette.TEXT_LIGHT);
        backwardBtn.addActionListener(e -> this.moveTokenBackward(token));

        JButton forwardBtn = new JButton("Front");
        forwardBtn.setBackground(ColorPalette.BUTTON_PRIMARY);
        forwardBtn.setForeground(ColorPalette.TEXT_LIGHT);
        forwardBtn.addActionListener(e -> this.moveTokenForward(token));

        layerBtnPanel.add(backwardBtn);
        layerBtnPanel.add(forwardBtn);

        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(speedLabel);
        panel.add(speedField);
        panel.add(sizeLabel);
        panel.add(sizeField);
        panel.add(layerLabel);
        panel.add(layerBtnPanel);

        JButton okButton = new JButton("OK");
        okButton.setBackground(ColorPalette.BUTTON_SUCCESS);
        okButton.setForeground(ColorPalette.TEXT_LIGHT);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBackground(ColorPalette.BUTTON_SUCCESS);
        cancelButton.setForeground(ColorPalette.TEXT_LIGHT);

        Object[] options = {okButton, cancelButton};

        JOptionPane optionPane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null, options, options[0]);
        JDialog dialog = optionPane.createDialog(this.applicationCore.getMainFrame(), "Edit " + token.getDisplayName());

        okButton.addActionListener(e -> {
            optionPane.setValue(JOptionPane.OK_OPTION);
            dialog.dispose();
        });

        cancelButton.addActionListener(e -> {
            optionPane.setValue(JOptionPane.CANCEL_OPTION);
            dialog.dispose();
        });

        dialog.setVisible(true);

        Object result = optionPane.getValue();

        if(result != null && result.equals(JOptionPane.OK_OPTION)) {
            try {
                token.setDisplayName(nameField.getText().trim());
                token.setMovementSpeed(Integer.parseInt(speedField.getText().trim()) / 5);
                token.setGridScale(Integer.parseInt(sizeField.getText().trim()));

                this.refreshTokensList();
                if(this.applicationCore.onEffectsChanged != null) this.applicationCore.onEffectsChanged.run();
                this.applicationCore.refreshDisplay();
            } catch(NumberFormatException ex) {
                JOptionPane.showMessageDialog(this.applicationCore.getMainFrame(), "Invalid number format for speed or size.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteToken(TokenModel token) {
        JLabel messageLabel = new JLabel("Remove '" + token.getDisplayName() + "' from the map?");
        messageLabel.setForeground(ColorPalette.TEXT_LIGHT);

        int confirm = JOptionPane.showConfirmDialog(this.applicationCore.getMainFrame(), messageLabel, "Delete Token", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);

        if(confirm == JOptionPane.YES_OPTION) {
            DataState dataState = this.applicationCore.getDataState();

            dataState.getActiveTokens().remove(token.getIdentifier());
            dataState.getTokenOrdering().remove((Integer) token.getIdentifier());

            if(java.util.Objects.equals(this.applicationCore.getToolState().getSelectedTokenIdentifier(), token.getIdentifier())) {
                this.applicationCore.getToolState().setSelectedTokenIdentifier(null);
                if(this.applicationCore.onSelectionChanged != null) {
                    this.applicationCore.onSelectionChanged.run();
                }
            }

            this.refreshTokensList();
            if(this.applicationCore.onEffectsChanged != null) this.applicationCore.onEffectsChanged.run();
            this.applicationCore.refreshDisplay();
        }
    }

    private void moveTokenForward(TokenModel token) {
        java.util.List<Integer> ordering = this.applicationCore.getDataState().getTokenOrdering();
        int index = ordering.indexOf(token.getIdentifier());

        if(index != -1 && index < ordering.size() - 1) {
            ordering.remove(index);
            ordering.add(index + 1, token.getIdentifier());
            this.refreshTokensList();
            this.applicationCore.refreshDisplay();
        }
    }

    private void moveTokenBackward(TokenModel token) {
        java.util.List<Integer> ordering = this.applicationCore.getDataState().getTokenOrdering();
        int index = ordering.indexOf(token.getIdentifier());

        if(index > 0) {
            ordering.remove(index);
            ordering.add(index - 1, token.getIdentifier());
            this.refreshTokensList();
            this.applicationCore.refreshDisplay();
        }
    }
}
