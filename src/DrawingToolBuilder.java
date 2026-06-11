import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

/**
 * Represents the drawing tool options construction instance.
 *
 * @author Adi
 */
class DrawingToolBuilder {

    private final ApplicationCore applicationCore;

    public DrawingToolBuilder(ApplicationCore applicationCore) {
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

        JPanel toolsPanel = new JPanel();
        toolsPanel.setLayout(new BoxLayout(toolsPanel, BoxLayout.Y_AXIS));
        toolsPanel.setBorder(new TitledBorder("Tools"));
        toolsPanel.setBackground(ColorPalette.BACKGROUND_LIGHT);

        ButtonGroup buttonGroup = new ButtonGroup();
        JRadioButton radioPen = new JRadioButton("Pen Tool", true);
        JRadioButton radioEraserClick = new JRadioButton("Object Eraser");
        JRadioButton radioEraserBrush = new JRadioButton("Brush Eraser");

        radioPen.setBackground(ColorPalette.BACKGROUND_LIGHT);
        radioEraserClick.setBackground(ColorPalette.BACKGROUND_LIGHT);
        radioEraserBrush.setBackground(ColorPalette.BACKGROUND_LIGHT);

        buttonGroup.add(radioPen);
        buttonGroup.add(radioEraserClick);
        buttonGroup.add(radioEraserBrush);

        radioPen.addActionListener(event -> this.applicationCore.getToolState().setDrawingSubtool(DrawingSubtool.PEN));
        radioEraserClick.addActionListener(event -> this.applicationCore.getToolState().setDrawingSubtool(DrawingSubtool.ERASE_CLICK));
        radioEraserBrush.addActionListener(event -> this.applicationCore.getToolState().setDrawingSubtool(DrawingSubtool.ERASE_BRUSH));

        toolsPanel.add(radioPen);
        toolsPanel.add(radioEraserClick);
        toolsPanel.add(radioEraserBrush);

        JButton buttonClear = new JButton("Clear All");
        buttonClear.setBackground(ColorPalette.BUTTON_DANGER);
        buttonClear.setForeground(ColorPalette.TEXT_LIGHT);
        buttonClear.addActionListener(event -> {
            this.applicationCore.getDataState().getCanvasDrawings().clear();
            this.applicationCore.refreshDisplay();
        });

        optionsContainer.add(toolsPanel);
        optionsContainer.add(buttonClear);

        return optionsContainer;
    }

}
