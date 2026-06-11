import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;

/**
 * Represents the drawing tab construction instance.
 * <p></p>
 * Assembles the vector drawing tools for this DrawingTabBuilder.
 *
 * @author Adi
 */
class DrawingTabBuilder {

    private final ApplicationCore applicationCore;

    public DrawingTabBuilder(ApplicationCore applicationCore) {
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

        JPanel toolsPanel = new JPanel();
        toolsPanel.setLayout(new BoxLayout(toolsPanel, BoxLayout.Y_AXIS));
        toolsPanel.setBorder(new TitledBorder("Tools"));

        ButtonGroup buttonGroup = new ButtonGroup();
        JRadioButton radioPen = new JRadioButton("Pen Tool", true);
        JRadioButton radioEraserClick = new JRadioButton("Object Eraser (Click)");
        JRadioButton radioEraserBrush = new JRadioButton("Brush Eraser (Wipe)");

        buttonGroup.add(radioPen);
        buttonGroup.add(radioEraserClick);
        buttonGroup.add(radioEraserBrush);

        radioPen.addActionListener(event -> this.applicationCore.getToolState().setDrawingSubtool(DrawingSubtool.PEN));
        radioEraserClick.addActionListener(event -> this.applicationCore.getToolState().setDrawingSubtool(DrawingSubtool.ERASE_CLICK));
        radioEraserBrush.addActionListener(event -> this.applicationCore.getToolState().setDrawingSubtool(DrawingSubtool.ERASE_BRUSH));

        toolsPanel.add(radioPen);
        toolsPanel.add(radioEraserClick);
        toolsPanel.add(radioEraserBrush);

        JButton buttonClear = new JButton("Clear All Drawings");
        buttonClear.addActionListener(event -> {
            this.applicationCore.getDataState().getCanvasDrawings().clear();
            this.applicationCore.refreshDisplay();
        });

        tabContainer.add(toolsPanel);
        tabContainer.add(buttonClear);

        return tabContainer;
    }

}
