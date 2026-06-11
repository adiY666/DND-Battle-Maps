import javax.swing.*;
import java.awt.*;

/**
 * Represents the tokens tab construction instance.
 *
 * @author Adi
 */
class TokensTabBuilder {

    private final ApplicationCore applicationCore;
    private JPanel listContainer;

    public TokensTabBuilder(ApplicationCore applicationCore) {
        super();
        this.applicationCore = applicationCore;
    }

    /**
     * Builds the tab interface.
     *
     * @return the tab panel component
     */
    public JPanel buildTab() {
        JPanel tabContainer = new JPanel(new BorderLayout());
        tabContainer.setBackground(ColorPalette.BACKGROUND_DARK);

        JButton clearButton = new JButton("Clear All Tokens");
        clearButton.setBackground(new Color(244, 67, 54));
        clearButton.setForeground(Color.WHITE);
        clearButton.setFocusPainted(false);
        clearButton.addActionListener(event -> {
            this.applicationCore.getDataState().getActiveTokens().clear();
            this.applicationCore.getDataState().getTokenOrdering().clear();
            this.applicationCore.getToolState().setSelectedTokenIdentifier(null);
            this.applicationCore.refreshDisplay();
            this.refreshTokensList();
        });

        tabContainer.add(clearButton, BorderLayout.NORTH);

        // Container for the dynamic list of tokens
        this.listContainer = new JPanel();
        this.listContainer.setLayout(new BoxLayout(this.listContainer, BoxLayout.Y_AXIS));
        this.listContainer.setBackground(ColorPalette.BACKGROUND_DARK);

        JScrollPane scrollPane = new JScrollPane(this.listContainer);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(ColorPalette.BACKGROUND_DARK);
        tabContainer.add(scrollPane, BorderLayout.CENTER);

        // Register this tab to listen to the new addition signal
        this.applicationCore.onTokenListChanged = this::refreshTokensList;

        this.refreshTokensList();
        return tabContainer;
    }

    /**
     * Clears and rebuilds the UI list of tokens based on the current DataState.
     */
    public void refreshTokensList() {
        this.listContainer.removeAll();
        DataState dataState = this.applicationCore.getDataState();

        // Loop backwards to show newest tokens at the top (like the python version)
        for (int i = dataState.getTokenOrdering().size() - 1; i >= 0; i--) {
            Integer tokenId = dataState.getTokenOrdering().get(i);
            TokenModel token = dataState.getActiveTokens().get(tokenId);
            if (token == null) continue;

            // Row Wrapper
            JPanel row = new JPanel(new BorderLayout());
            row.setBackground(ColorPalette.BACKGROUND_DARK);
            row.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

            // Name
            JLabel nameLabel = new JLabel(token.getDisplayName());
            nameLabel.setForeground(Color.WHITE);
            row.add(nameLabel, BorderLayout.CENTER);

            // Buttons
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
            buttonPanel.setBackground(ColorPalette.BACKGROUND_DARK);

            JButton jumpBtn = new JButton("Jump");
            jumpBtn.setBackground(ColorPalette.BUTTON_PRIMARY);
            jumpBtn.setForeground(Color.WHITE);
            jumpBtn.setMargin(new Insets(2, 5, 2, 5));
            jumpBtn.addActionListener(e -> this.jumpToToken(token));

            JButton editBtn = new JButton("Edit");
            editBtn.setBackground(new Color(255, 152, 0));
            editBtn.setForeground(Color.WHITE);
            editBtn.setMargin(new Insets(2, 5, 2, 5));
            editBtn.addActionListener(e -> this.editToken(token));

            buttonPanel.add(jumpBtn);
            buttonPanel.add(editBtn);
            row.add(buttonPanel, BorderLayout.EAST);

            this.listContainer.add(row);
            this.listContainer.add(Box.createRigidArea(new Dimension(0, 5))); // Spacing
        }

        this.listContainer.revalidate();
        this.listContainer.repaint();
    }

    private void jumpToToken(TokenModel token) {
        DataState dataState = this.applicationCore.getDataState();
        int canvasWidth = this.applicationCore.getCanvasPanel().getWidth();
        int canvasHeight = this.applicationCore.getCanvasPanel().getHeight();

        // Calculate the camera pan required to center the token
        double targetX = (canvasWidth / 2.0) - ((token.getPositionHorizontal() + token.getGridScale() / 2.0) * dataState.calculateCellDimension());
        double targetY = (canvasHeight / 2.0) - ((token.getPositionVertical() + token.getGridScale() / 2.0) * dataState.calculateCellDimension());

        dataState.setPanHorizontal(targetX);
        dataState.setPanVertical(targetY);
        this.applicationCore.getToolState().setSelectedTokenIdentifier(token.getIdentifier());
        this.applicationCore.refreshDisplay();
    }

    private void editToken(TokenModel token) {
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));

        JTextField nameField = new JTextField(token.getDisplayName());
        // Translate grid speed to feet (1 square = 5 ft)
        JTextField speedField = new JTextField(String.valueOf(token.getMovementSpeed() * 5));
        JTextField sizeField = new JTextField(String.valueOf(token.getGridScale()));

        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Speed (ft):"));
        panel.add(speedField);
        panel.add(new JLabel("Size (squares):"));
        panel.add(sizeField);

        int result = JOptionPane.showConfirmDialog(this.applicationCore.getMainFrame(), panel, "Edit " + token.getDisplayName(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                token.setDisplayName(nameField.getText().trim());
                // Translate feet back to grid speed
                token.setMovementSpeed(Integer.parseInt(speedField.getText().trim()) / 5);
                token.setGridScale(Integer.parseInt(sizeField.getText().trim()));

                this.refreshTokensList();
                this.applicationCore.refreshDisplay();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this.applicationCore.getMainFrame(), "Invalid number format for speed or size.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
