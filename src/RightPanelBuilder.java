import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;

/**
 * Represents the right panel construction instance.
 * <p></p>
 * Assembles the right side tabs and wrapper for this RightPanelBuilder.
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
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setPreferredSize(new Dimension(285, 0));

        JButton buttonToggle = new JButton("▶");
        buttonToggle.setMargin(new Insets(0, 0, 0, 0));
        buttonToggle.addActionListener(event -> {
            boolean isVisible = rightPanel.isVisible();
            rightPanel.setVisible(!isVisible);
            buttonToggle.setText(isVisible ? "◀" : "▶");
        });

        JTabbedPane rightNotebook = new JTabbedPane();
        TokensTabBuilder tokensTabBuilder = new TokensTabBuilder(this.applicationCore);
        InitiativeTabBuilder initiativeTabBuilder = new InitiativeTabBuilder(this.applicationCore);
        EffectsTabBuilder effectsTabBuilder = new EffectsTabBuilder(this.applicationCore);
        NotesTabBuilder notesTabBuilder = new NotesTabBuilder(this.applicationCore);

        rightNotebook.addTab("Tokens", tokensTabBuilder.buildTab());
        rightNotebook.addTab("Initiative", initiativeTabBuilder.buildTab());
        rightNotebook.addTab("Effects", effectsTabBuilder.buildTab());
        rightNotebook.addTab("Notes", notesTabBuilder.buildTab());

        rightPanel.add(rightNotebook, BorderLayout.CENTER);
        rightWrapper.add(rightPanel, BorderLayout.CENTER);
        rightWrapper.add(buttonToggle, BorderLayout.WEST);

        return rightWrapper;
    }

}
