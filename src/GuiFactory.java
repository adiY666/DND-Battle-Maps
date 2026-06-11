import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;

/**
 * Main factory for building the application GUI.
 *
 * @author Adi
 */
class GuiFactory {

    private final ApplicationCore applicationCore;

    public GuiFactory(ApplicationCore applicationCore) {
        super();
        this.applicationCore = applicationCore;
    }

    /**
     * Constructs all primary window components.
     */
    public void constructUserInterface() {
        MainFrame mainFrame = this.applicationCore.getMainFrame();
        JPanel containerPanel = new JPanel(new BorderLayout());
        mainFrame.setContentPane(containerPanel);

        // Stack the main toolbar and the new top dock using BoxLayout
        // Make sure the topContainer is visible and handles sizes correctly
        JPanel topContainer = new JPanel();
        topContainer.setLayout(new javax.swing.BoxLayout(topContainer, javax.swing.BoxLayout.Y_AXIS));

        ToolbarBuilder toolbarBuilder = new ToolbarBuilder(this.applicationCore);
        topContainer.add(toolbarBuilder.buildToolbar());

        ToolDockBuilder toolDockBuilder = new ToolDockBuilder(this.applicationCore);
        topContainer.add(toolDockBuilder.buildDock());

        containerPanel.add(topContainer, BorderLayout.NORTH);

        containerPanel.add(this.applicationCore.getCanvasPanel(), BorderLayout.CENTER);

        RightPanelBuilder rightPanelBuilder = new RightPanelBuilder(this.applicationCore);
        containerPanel.add(rightPanelBuilder.buildRightWrapper(), BorderLayout.EAST);
    }
}
