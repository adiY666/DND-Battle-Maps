import tabletop.main.ApplicationCore;
import tabletop.model.InitiativeModel;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Represents the initiative tab construction instance.
 * <p></p>
 * Assembles the combat tracker view for this InitiativeTabBuilder.
 *
 * @author Adi
 */
class InitiativeTabBuilder {

    private final ApplicationCore applicationCore;

    public InitiativeTabBuilder(ApplicationCore applicationCore) {
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

        JPanel inputPanel = new JPanel();
        JTextField nameField = new JTextField("Name", 8);
        JTextField valueField = new JTextField("20", 3);
        JButton addButton = new JButton("Add");

        addButton.addActionListener(event -> {
            try {
                int initiativeValue = Integer.parseInt(valueField.getText().trim());
                InitiativeModel initiativeModel = new InitiativeModel(nameField.getText().trim(), initiativeValue);
                this.applicationCore.getDataState().getInitiativeList().add(initiativeModel);
                nameField.setText("");
                valueField.setText("");
            } catch (NumberFormatException exception) {
                // Ignore parse errors seamlessly
            }
        });

        inputPanel.add(nameField);
        inputPanel.add(valueField);
        inputPanel.add(addButton);
        tabContainer.add(inputPanel);

        return tabContainer;
    }

}
