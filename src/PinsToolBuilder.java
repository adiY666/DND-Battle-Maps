import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 * Represents the pins tool options construction instance.
 *
 * @author Adi
 */
class PinsToolBuilder {

    private final ApplicationCore applicationCore;

    public PinsToolBuilder(ApplicationCore applicationCore) {
        super();
        this.applicationCore = applicationCore;
    }

    /**
     * Builds the options interface.
     *
     * @return the options panel component
     */
    public JPanel buildOptions() {
        JPanel optionsContainer = new JPanel();
        optionsContainer.setLayout(new BoxLayout(optionsContainer, BoxLayout.Y_AXIS));
        optionsContainer.setBorder(new EmptyBorder(10, 10, 10, 10));
        optionsContainer.setBackground(ColorPalette.BACKGROUND_LIGHT);

        JButton addPinButton = new JButton("+ Drop Pin");
        addPinButton.setBackground(ColorPalette.BUTTON_WARNING);
        addPinButton.setForeground(ColorPalette.TEXT_LIGHT);

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

        optionsContainer.add(addPinButton);
        return optionsContainer;
    }

}
