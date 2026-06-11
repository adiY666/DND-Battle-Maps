import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;

/**
 * Represents the tool dock construction instance.
 * <p></p>
 * Assembles the left side tool dock.
 *
 * @author Adi
 */
class ToolDockBuilder {

    private final ApplicationCore applicationCore;
    private JPanel optionsPanel;
    private final RulersToolBuilder rulersToolBuilder;
    private final DrawingToolBuilder drawingToolBuilder;
    private final PinsToolBuilder pinsToolBuilder;

    public ToolDockBuilder(ApplicationCore applicationCore) {
        super();
        this.applicationCore = applicationCore;
        this.rulersToolBuilder = new RulersToolBuilder(applicationCore);
        this.drawingToolBuilder = new DrawingToolBuilder(applicationCore);
        this.pinsToolBuilder = new PinsToolBuilder(applicationCore);
    }

    /**
     * Builds the left tool dock.
     *
     * @return the ready panel component
     */
    public JPanel buildDock() {
        JPanel dockContainer = new JPanel(new BorderLayout());

        JPanel categoryPanel = new JPanel();
        categoryPanel.setLayout(new BoxLayout(categoryPanel, BoxLayout.Y_AXIS));
        categoryPanel.setBackground(ColorPalette.TOOLBAR_BACKGROUND);
        categoryPanel.setPreferredSize(new Dimension(80, 0));

        this.optionsPanel = new JPanel(new BorderLayout());
        this.optionsPanel.setBackground(ColorPalette.BACKGROUND_LIGHT);
        this.optionsPanel.setPreferredSize(new Dimension(200, 0));
        this.optionsPanel.setVisible(false);

        JButton rulersButton = this.createCategoryButton("Rulers");
        rulersButton.addActionListener(event -> this.showOptions(this.rulersToolBuilder.buildOptions(), ToolType.TEMPLATE));

        JButton drawingButton = this.createCategoryButton("Draw");
        drawingButton.addActionListener(event -> this.showOptions(this.drawingToolBuilder.buildOptions(), ToolType.DRAWING));

        JButton pinsButton = this.createCategoryButton("Pins");
        pinsButton.addActionListener(event -> this.showOptions(this.pinsToolBuilder.buildOptions(), ToolType.TOKEN));

        categoryPanel.add(rulersButton);
        categoryPanel.add(drawingButton);
        categoryPanel.add(pinsButton);

        dockContainer.add(categoryPanel, BorderLayout.WEST);
        dockContainer.add(this.optionsPanel, BorderLayout.CENTER);

        return dockContainer;
    }

    private JButton createCategoryButton(String text) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(80, 40));
        button.setBackground(ColorPalette.BACKGROUND_DARK);
        button.setForeground(ColorPalette.TEXT_LIGHT);
        button.setFocusPainted(false);
        return button;
    }

    private void showOptions(JPanel panel, ToolType toolType) {
        this.optionsPanel.removeAll();
        this.optionsPanel.add(panel, BorderLayout.NORTH);
        this.optionsPanel.setVisible(true);
        this.optionsPanel.revalidate();
        this.optionsPanel.repaint();

        this.applicationCore.getToolState().setCurrentTool(toolType);
        this.applicationCore.refreshDisplay();
    }

}
