import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;

/**
 * Represents the right panel construction instance.
 *
 * @author Adi
 */
class RightPanelBuilder {

    private final ApplicationCore applicationCore;

    public RightPanelBuilder(ApplicationCore applicationCore) {
        super();
        this.applicationCore = applicationCore;
    }

    /**
     * Builds the right collapsible wrapper.
     *
     * @return the ready panel component
     */
    public JPanel buildRightWrapper() {
        JPanel rightWrapper = new JPanel(new BorderLayout());
        rightWrapper.setBackground(ColorPalette.BACKGROUND_DARK);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setPreferredSize(new Dimension(285, 0));
        rightPanel.setBackground(ColorPalette.BACKGROUND_DARK);

        JButton buttonToggle = new JButton("▶");
        buttonToggle.setMargin(new Insets(0, 0, 0, 0));
        buttonToggle.setBackground(ColorPalette.BACKGROUND_DARK);
        buttonToggle.setForeground(Color.WHITE);
        buttonToggle.setFocusPainted(false); // Removes the ugly highlight box

        buttonToggle.addActionListener(event -> {
            boolean isVisible = rightPanel.isVisible();
            rightPanel.setVisible(!isVisible);
            buttonToggle.setText(isVisible ? "◀" : "▶");
        });

        JTabbedPane rightNotebook = new JTabbedPane();

        TokensTabBuilder charactersTabBuilder = new TokensTabBuilder(this.applicationCore);
        NotesTabBuilder generalInfoTabBuilder = new NotesTabBuilder(this.applicationCore);

        rightNotebook.addTab("Characters", charactersTabBuilder.buildTab());
        rightNotebook.addTab("General Information", generalInfoTabBuilder.buildTab());

        rightPanel.add(rightNotebook, BorderLayout.CENTER);
        rightWrapper.add(rightPanel, BorderLayout.CENTER);
        rightWrapper.add(buttonToggle, BorderLayout.WEST);

        return rightWrapper;
    }
}
