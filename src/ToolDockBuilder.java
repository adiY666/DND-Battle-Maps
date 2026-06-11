import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Color;
import java.awt.Dimension;

/**
 * Represents the tool dock construction instance.
 * <p></p>
 * Assembles the top tool dock for this ToolDockBuilder.
 *
 * @author Adi
 */
class ToolDockBuilder {

    private final ApplicationCore applicationCore;
    private JPanel dockContainer;
    private JPanel optionsPanel;
    private final RulersToolBuilder rulersToolBuilder;
    private final DrawingToolBuilder drawingToolBuilder;
    private final PinsToolBuilder pinsToolBuilder;
    private String currentOpenCategory = null;

    public ToolDockBuilder(ApplicationCore applicationCore) {
        super();
        this.applicationCore = applicationCore;
        this.rulersToolBuilder = new RulersToolBuilder(applicationCore);
        this.drawingToolBuilder = new DrawingToolBuilder(applicationCore);
        this.pinsToolBuilder = new PinsToolBuilder(applicationCore);
    }

    /**
     * Builds the top tool dock.
     *
     * @return the ready panel component
     */
    public JPanel buildDock() {
        this.dockContainer = new JPanel();
        // Use BoxLayout Y_AXIS so the options panel drops down below the categories naturally
        this.dockContainer.setLayout(new BoxLayout(this.dockContainer, BoxLayout.Y_AXIS));
        this.dockContainer.setBackground(ColorPalette.TOOLBAR_BACKGROUND);
        this.dockContainer.setOpaque(true);

        JPanel categoryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        categoryPanel.setBackground(ColorPalette.TOOLBAR_BACKGROUND);
        categoryPanel.setOpaque(true);
        // Force the category panel to maintain its height
        categoryPanel.setMinimumSize(new Dimension(0, 40));
        categoryPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        this.optionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        this.optionsPanel.setBackground(ColorPalette.BACKGROUND_LIGHT);
        this.optionsPanel.setOpaque(true);
        this.optionsPanel.setVisible(false);
        this.optionsPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.GRAY));
        // Force the options panel to maintain a specific height when visible
        this.optionsPanel.setMinimumSize(new Dimension(0, 45));
        this.optionsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        JButton rulersButton = this.createCategoryButton("Rulers");
        rulersButton.addActionListener(event -> this.toggleOptions("Rulers",
                this.rulersToolBuilder.buildOptions(), ToolType.TEMPLATE));

        JButton drawingButton = this.createCategoryButton("Draw");
        drawingButton.addActionListener(event -> this.toggleOptions("Draw",
                this.drawingToolBuilder.buildOptions(), ToolType.DRAWING));

        JButton pinsButton = this.createCategoryButton("Pins");
        pinsButton.addActionListener(event -> this.toggleOptions("Pins",
                this.pinsToolBuilder.buildOptions(), ToolType.TOKEN));

        categoryPanel.add(rulersButton);
        categoryPanel.add(drawingButton);
        categoryPanel.add(pinsButton);

        this.dockContainer.add(categoryPanel);
        this.dockContainer.add(this.optionsPanel);

        return this.dockContainer;
    }

    private JButton createCategoryButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(ColorPalette.BACKGROUND_DARK);
        button.setForeground(ColorPalette.TEXT_LIGHT);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        return button;
    }

    private void toggleOptions(String categoryName, JPanel panel, ToolType toolType) {
        if (categoryName.equals(this.currentOpenCategory)) {
            this.optionsPanel.setVisible(false);
            this.currentOpenCategory = null;
            this.applicationCore.getToolState().setCurrentTool(ToolType.TOKEN);
        } else {
            this.optionsPanel.removeAll();
            this.optionsPanel.add(panel);
            this.optionsPanel.setVisible(true);
            this.currentOpenCategory = categoryName;
            this.applicationCore.getToolState().setCurrentTool(toolType);
        }

        this.optionsPanel.revalidate();
        this.optionsPanel.repaint();

        MainFrame mainFrame = this.applicationCore.getMainFrame();
        // Pack forces the layout manager to strictly remeasure bounds based on visibility
        mainFrame.pack();
        // Re-apply maximized state after packing
        mainFrame.setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);

        this.applicationCore.refreshDisplay();
    }

}
