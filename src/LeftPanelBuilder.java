import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;

/**
 * Represents the left panel construction instance.
 * <p></p>
 * Assembles the left side tabs and wrapper for this LeftPanelBuilder.
 *
 * @author Adi
 */
class LeftPanelBuilder {

    private final ApplicationCore applicationCore;

    public LeftPanelBuilder(ApplicationCore applicationCore) {
        super();
        this.applicationCore = applicationCore;
    }

    /**
     * Builds the left collapsible wrapper.
     *
     * @return the ready panel component
     */
    public JPanel buildLeftWrapper() {
        JPanel leftWrapper = new JPanel(new BorderLayout());
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(285, 0));

        JButton buttonToggle = new JButton("◀");
        buttonToggle.setMargin(new Insets(0, 0, 0, 0));
        buttonToggle.addActionListener(event -> {
            boolean isVisible = leftPanel.isVisible();
            leftPanel.setVisible(!isVisible);
            buttonToggle.setText(isVisible ? "▶" : "◀");
        });

        JTabbedPane leftNotebook = new JTabbedPane();
        EffectsTabBuilder effectsTabBuilder = new EffectsTabBuilder(this.applicationCore);
        RulersTabBuilder rulersTabBuilder = new RulersTabBuilder(this.applicationCore);
        DrawingTabBuilder drawingTabBuilder = new DrawingTabBuilder(this.applicationCore);

        leftNotebook.addTab("Effects", effectsTabBuilder.buildTab());
        leftNotebook.addTab("Rulers", rulersTabBuilder.buildTab());
        leftNotebook.addTab("Drawing", drawingTabBuilder.buildTab());

        leftNotebook.addChangeListener(event -> {
            String selectedTab = leftNotebook.getTitleAt(leftNotebook.getSelectedIndex());
            ToolState toolState = this.applicationCore.getToolState();
            if ("Drawing".equals(selectedTab)) toolState.setCurrentTool(ToolType.DRAWING);
            else if ("Rulers".equals(selectedTab)) toolState.setCurrentTool(ToolType.TEMPLATE);
            else toolState.setCurrentTool(ToolType.TOKEN);
            this.applicationCore.refreshDisplay();
        });

        leftPanel.add(leftNotebook, BorderLayout.CENTER);
        leftWrapper.add(leftPanel, BorderLayout.CENTER);
        leftWrapper.add(buttonToggle, BorderLayout.EAST);

        return leftWrapper;
    }

}
