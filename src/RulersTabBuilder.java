import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.GridLayout;

/**
 * Represents the rulers tab construction instance.
 * <p></p>
 * Assembles the template spawner for this RulersTabBuilder.
 *
 * @author Adi
 */
class RulersTabBuilder {

    private final ApplicationCore applicationCore;

    public RulersTabBuilder(ApplicationCore applicationCore) {
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

        JPanel buttonsPanel = new JPanel(new GridLayout(1, 3, 2, 2));
        buttonsPanel.add(this.createSpawnButton("+ Circle", "circle"));
        buttonsPanel.add(this.createSpawnButton("+ Cone", "cone"));
        buttonsPanel.add(this.createSpawnButton("+ Line", "line"));

        tabContainer.add(buttonsPanel);
        return tabContainer;
    }

    private JButton createSpawnButton(String labelText, String templateType) {
        JButton spawnButton = new JButton(labelText);
        spawnButton.setBackground(ColorPalette.BUTTON_PRIMARY);
        spawnButton.setForeground(Color.WHITE);
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
