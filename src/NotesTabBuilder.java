import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * Represents the notes tab construction instance.
 * <p></p>
 * Assembles the textual notes view for this NotesTabBuilder.
 *
 * @author Adi
 */
class NotesTabBuilder {

    private final ApplicationCore applicationCore;

    public NotesTabBuilder(ApplicationCore applicationCore) {
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

        JButton addNoteButton = new JButton("+ Create New Note");
        addNoteButton.addActionListener(event -> {
            this.applicationCore.getDataState().getUserNotes().add("New Note");
            // Triggers any related dynamic refreshes
        });

        tabContainer.add(addNoteButton);
        return tabContainer;
    }

}
