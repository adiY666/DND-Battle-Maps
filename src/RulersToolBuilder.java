import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.GridLayout;

/**
 * Represents the rulers tool options construction instance.
 *
 * @author Adi
 */
class RulersToolBuilder {

    private final ApplicationCore applicationCore;

    public RulersToolBuilder(ApplicationCore applicationCore) {
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

        JPanel buttonsPanel = new JPanel(new GridLayout(3, 1, 0, 5));
        buttonsPanel.setBackground(ColorPalette.BACKGROUND_LIGHT);

        buttonsPanel.add(this.createSpawnButton("+ Circle", "circle"));
        buttonsPanel.add(this.createSpawnButton("+ Cone", "cone"));
        buttonsPanel.add(this.createSpawnButton("+ Line", "line"));

        optionsContainer.add(buttonsPanel);
        return optionsContainer;
    }

    private JButton createSpawnButton(String labelText, String templateType) {
        JButton spawnButton = new JButton(labelText);
        spawnButton.setBackground(ColorPalette.BUTTON_PRIMARY);
        spawnButton.setForeground(ColorPalette.TEXT_LIGHT);
        spawnButton.addActionListener(event -> this.spawnTemplate(templateType));
        return spawnButton;
    }

    private void spawnTemplate(String templateType) {
        DataState dataState = this.applicationCore.getDataState();
        int canvasWidth = this.applicationCore.getCanvasPanel().getWidth();
        int canvasHeight = this.applicationCore.getCanvasPanel().getHeight();
        Coordinate logicalPosition = dataState.convertScreenToLogical(canvasWidth / 2, canvasHeight / 2);

        TemplateModel templateModel = new TemplateModel();
        int nextIdentifier = dataState.getNextTemplateIdentifier();
        dataState.setNextTemplateIdentifier(nextIdentifier + 1);

        templateModel.setIdentifier(nextIdentifier);
        templateModel.setGeometryType(templateType);
        templateModel.setPositionHorizontal(logicalPosition.getCoordinateHorizontal());
        templateModel.setPositionVertical(logicalPosition.getCoordinateVertical());
        templateModel.setPrimarySize(20.0);
        templateModel.setSecondarySize("cone".equals(templateType) ? 60.0 : 10.0);
        templateModel.setHeadingAngle(0.0);
        templateModel.setDisplayColor("Blue");

        dataState.getActiveTemplates().add(templateModel);
        this.applicationCore.getToolState().setSelectedTemplateIdentifier(nextIdentifier);
        this.applicationCore.refreshDisplay();
    }

}
