import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.Color;

/**
 * Represents the pins tab construction instance.
 * <p></p>
 * Assembles the map bookmarks view for this PinsTabBuilder.
 *
 * @author Adi
 */
class PinsTabBuilder {

    private final ApplicationCore applicationCore;

    public PinsTabBuilder(ApplicationCore applicationCore) {
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

        JButton addPinButton = new JButton("+ Drop Pin at Screen Center");
        addPinButton.setBackground(new Color(255, 152, 0));
        addPinButton.setForeground(Color.WHITE);

        addPinButton.addActionListener(event -> {
            String pinName = JOptionPane.showInputDialog("Enter name for map location:");
            if (pinName != null && !pinName.trim().isEmpty()) {
                DataState dataState = this.applicationCore.getDataState();
                int canvasWidth = this.applicationCore.getCanvasPanel().getWidth();
                int canvasHeight = this.applicationCore.getCanvasPanel().getHeight();
                Coordinate logicalPosition = dataState.convertScreenToLogical(canvasWidth / 2, canvasHeight / 2);

                int nextIdentifier = dataState.getNextPinIdentifier();
                dataState.setNextPinIdentifier(nextIdentifier + 1);

                PinModel pinModel = new PinModel(nextIdentifier, pinName, logicalPosition.getCoordinateHorizontal(), logicalPosition.getCoordinateVertical(), "Red");
                dataState.getMapPins().add(pinModel);

                this.applicationCore.getToolState().setSelectedPinIndex(dataState.getMapPins().size() - 1);
                this.applicationCore.refreshDisplay();
            }
        });

        tabContainer.add(addPinButton);
        return tabContainer;
    }

}
