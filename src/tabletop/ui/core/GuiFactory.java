package tabletop.ui.core;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JTabbedPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import tabletop.main.ApplicationCore;
import tabletop.ui.layout.ToolbarBuilder;
import tabletop.ui.tabs.TokensTabBuilder;
import tabletop.ui.tabs.PinsTabBuilder;
import tabletop.ui.theme.ColorPalette;

/**
 * Responsible for assembling the main user interface.
 *
 * @author Adi
 */
public class GuiFactory {

    private final ApplicationCore applicationCore;

    public GuiFactory(ApplicationCore applicationCore) {
        super();
        this.applicationCore = applicationCore;
    }

    public void constructUserInterface() {
        MainFrame mainFrame = this.applicationCore.getMainFrame();
        mainFrame.setLayout(new BorderLayout());

        // 1. Build the Top Toolbar
        ToolbarBuilder toolbarBuilder = new ToolbarBuilder(this.applicationCore);
        mainFrame.add(toolbarBuilder.buildToolbar(), BorderLayout.NORTH);

        // 2. Build the Main Canvas (Map)
        CanvasPanel canvasPanel = this.applicationCore.getCanvasPanel();

        // 3. Build the Tabbed Sidebar
        JTabbedPane rightSidebarTabs = new JTabbedPane();
        rightSidebarTabs.setBackground(ColorPalette.BACKGROUND_DARK);
        rightSidebarTabs.setForeground(ColorPalette.TEXT_LIGHT);
        rightSidebarTabs.setFocusable(false);

        // Add the Tokens Tab
        TokensTabBuilder tokensTabBuilder = new TokensTabBuilder(this.applicationCore);
        rightSidebarTabs.addTab("Tokens & Roster", tokensTabBuilder.buildTab());

        // Add the new Map Pins Tab
        PinsTabBuilder pinsTabBuilder = new PinsTabBuilder(this.applicationCore);
        rightSidebarTabs.addTab("DM Notes & Pins", pinsTabBuilder.buildTab());

        // Enforce a nice wide size for the sidebar so you can read everything easily
        rightSidebarTabs.setPreferredSize(new Dimension(350, mainFrame.getHeight()));
        rightSidebarTabs.setMinimumSize(new Dimension(300, 0));

        // 4. Split the screen between the Canvas and the Tabbed Sidebar
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, canvasPanel, rightSidebarTabs);
        splitPane.setResizeWeight(1.0); // Map gets all the extra space when you resize the window
        splitPane.setDividerSize(5);
        splitPane.setBorder(null);

        mainFrame.add(splitPane, BorderLayout.CENTER);
    }
}
