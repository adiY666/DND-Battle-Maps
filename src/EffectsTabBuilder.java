import tabletop.main.ApplicationCore;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Represents the effects tab construction instance.
 * <p></p>
 * Assembles the status effects view for this EffectsTabBuilder.
 *
 * @author Adi
 */
class EffectsTabBuilder {

    private final ApplicationCore applicationCore;

    public EffectsTabBuilder(ApplicationCore applicationCore) {
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

        JLabel placeholderLabel = new JLabel("Select a token to manage effects.");
        tabContainer.add(placeholderLabel);

        return tabContainer;
    }

}
