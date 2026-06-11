package tabletop.ui.core;

import tabletop.main.ApplicationCore;
import tabletop.main.ToolType;
import tabletop.state.ToolState;
import tabletop.ui.layout.RightPanelBuilder;
import tabletop.ui.layout.ToolbarBuilder;
import tabletop.ui.tabs.DrawingTabBuilder;
import tabletop.ui.tabs.PinsTabBuilder;
import tabletop.ui.tabs.RulersTabBuilder;
import tabletop.ui.theme.ColorPalette;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

/**
 * Main factory for building the application GUI.
 *
 * @author Adi
 */
public class GuiFactory {

    private final ApplicationCore applicationCore;

    public GuiFactory(ApplicationCore applicationCore) {
        super();
        this.applicationCore = applicationCore;
    }

    /**
     * Constructs all primary window components.
     */
    public void constructUserInterface() {
        // 1. FORCE CROSS-PLATFORM UI: This prevents Windows/Mac from overriding your colors
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 2. APPLY CUSTOM COLORS
        UIManager.put("TabbedPane.background", ColorPalette.BACKGROUND_DARK);
        UIManager.put("TabbedPane.foreground", Color.WHITE);
        UIManager.put("TabbedPane.selected", new Color(87, 90, 92));
        UIManager.put("TabbedPane.contentAreaColor", ColorPalette.BACKGROUND_DARK);
        UIManager.put("Panel.background", ColorPalette.BACKGROUND_DARK);

        MainFrame mainFrame = this.applicationCore.getMainFrame();
        JPanel containerPanel = new JPanel(new BorderLayout());
        containerPanel.setBackground(ColorPalette.BACKGROUND_DARK);
        mainFrame.setContentPane(containerPanel);

        // --- TOP AREA: Main Menu & Tool Tabs ---
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setBackground(ColorPalette.BACKGROUND_DARK);

        ToolbarBuilder toolbarBuilder = new ToolbarBuilder(this.applicationCore);
        topContainer.add(toolbarBuilder.buildToolbar(), BorderLayout.NORTH);

        JTabbedPane topToolTabs = new JTabbedPane();

        // 3. RESTRICT TAB HEIGHT: Prevents the massive stretching seen in your image
        topToolTabs.setPreferredSize(new Dimension(0, 100)); // Adjust '100' to make the bar taller or shorter

        // Build the tabs
        JPanel drawingTab = new DrawingTabBuilder(this.applicationCore).buildTab();
        JPanel rulersTab = new RulersTabBuilder(this.applicationCore).buildTab();
        JPanel pinsTab = new PinsTabBuilder(this.applicationCore).buildTab();

        // Force the background of the tab contents to match the dark theme
        drawingTab.setBackground(ColorPalette.BACKGROUND_DARK);
        rulersTab.setBackground(ColorPalette.BACKGROUND_DARK);
        pinsTab.setBackground(ColorPalette.BACKGROUND_DARK);

        topToolTabs.addTab("Drawing", drawingTab);
        topToolTabs.addTab("Rulers", rulersTab);
        topToolTabs.addTab("Map Pins", pinsTab);

        topToolTabs.addChangeListener(event -> {
            String selectedTab = topToolTabs.getTitleAt(topToolTabs.getSelectedIndex());
            ToolState toolState = this.applicationCore.getToolState();

            if ("Drawing".equals(selectedTab)) {
                toolState.setCurrentTool(ToolType.DRAWING);
            } else if ("Rulers".equals(selectedTab)) {
                toolState.setCurrentTool(ToolType.TEMPLATE);
            } else {
                toolState.setCurrentTool(ToolType.TOKEN);
            }
            this.applicationCore.refreshDisplay();
        });

        topContainer.add(topToolTabs, BorderLayout.CENTER);
        containerPanel.add(topContainer, BorderLayout.NORTH);

        // --- CENTER AREA: Canvas ---
        containerPanel.add(this.applicationCore.getCanvasPanel(), BorderLayout.CENTER);

        // --- RIGHT AREA: Characters & Info ---
        RightPanelBuilder rightPanelBuilder = new RightPanelBuilder(this.applicationCore);
        containerPanel.add(rightPanelBuilder.buildRightWrapper(), BorderLayout.EAST);
    }
}
