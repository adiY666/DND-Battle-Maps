import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * Represents the tokens tab construction instance.
 * <p></p>
 * Assembles the miniature list view for this TokensTabBuilder.
 *
 * @author Adi
 */
class TokensTabBuilder {

    private final ApplicationCore applicationCore;

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
        JPanel tabContainer = new JPanel();
        tabContainer.setLayout(new BoxLayout(tabContainer, BoxLayout.Y_AXIS));

        JButton clearButton = new JButton("Clear All Tokens");
        clearButton.addActionListener(event -> {
            this.applicationCore.getDataState().getActiveTokens().clear();
            this.applicationCore.getDataState().getTokenOrdering().clear();
            this.applicationCore.getToolState().setSelectedTokenIdentifier(null);
            this.applicationCore.refreshDisplay();
        });

        tabContainer.add(clearButton);
        return tabContainer;
    }

}
