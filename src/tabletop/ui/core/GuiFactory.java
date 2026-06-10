package tabletop.ui.core;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;

import tabletop.main.ApplicationCore;
import tabletop.main.ToolType;
import tabletop.state.ToolState;
import tabletop.ui.layout.ToolbarBuilder;
import tabletop.ui.layout.RightPanelBuilder;
import tabletop.ui.tabs.DrawingTabBuilder;
import tabletop.ui.tabs.RulersTabBuilder;
import tabletop.ui.tabs.PinsTabBuilder;
import tabletop.ui.theme.ColorPalette;

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

    public void constructUserInterface() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch(Exception e) {
            e.printStackTrace();
        }

        UIManager.put("TabbedPane.background", ColorPalette.LIST_ROW_BACKGROUND);
        UIManager.put("TabbedPane.foreground", ColorPalette.TEXT_LIGHT);
        UIManager.put("TabbedPane.selected", new Color(87, 90, 92));
        UIManager.put("TabbedPane.contentAreaColor", ColorPalette.BACKGROUND_DARK);
        UIManager.put("Panel.background", ColorPalette.BACKGROUND_DARK);

        MainFrame mainFrame = this.applicationCore.getMainFrame();
        JPanel containerPanel = new JPanel(new BorderLayout());
        containerPanel.setBackground(ColorPalette.BACKGROUND_DARK);
        mainFrame.setContentPane(containerPanel);

        // --- TOP COLLAPSIBLE WRAPPER ---
        JPanel topWrapper = new JPanel(new BorderLayout());
        topWrapper.setBackground(ColorPalette.BACKGROUND_DARK);

        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setBackground(ColorPalette.BACKGROUND_DARK);

        ToolbarBuilder toolbarBuilder = new ToolbarBuilder(this.applicationCore);
        topContainer.add(toolbarBuilder.buildToolbar(), BorderLayout.NORTH);

        JTabbedPane topToolTabs = new JTabbedPane();
        topToolTabs.setPreferredSize(new Dimension(0, 80));

        JPanel drawingTab = new DrawingTabBuilder(this.applicationCore).buildTab();
        JPanel rulersTab = new RulersTabBuilder(this.applicationCore).buildTab();
        JPanel pinsTab = new PinsTabBuilder(this.applicationCore).buildTab();

        drawingTab.setBackground(ColorPalette.BACKGROUND_DARK);
        rulersTab.setBackground(ColorPalette.BACKGROUND_DARK);
        pinsTab.setBackground(ColorPalette.BACKGROUND_DARK);

        topToolTabs.addTab("Drawing", drawingTab);
        topToolTabs.addTab("Rulers", rulersTab);
        topToolTabs.addTab("Map Pins", pinsTab);

        topToolTabs.addChangeListener(event -> {
            String selectedTab = topToolTabs.getTitleAt(topToolTabs.getSelectedIndex());
            ToolState toolState = this.applicationCore.getToolState();

            if("Drawing".equals(selectedTab)) {
                toolState.setCurrentTool(ToolType.DRAWING);
            } else if("Rulers".equals(selectedTab)) {
                toolState.setCurrentTool(ToolType.TEMPLATE);
            } else {
                toolState.setCurrentTool(ToolType.TOKEN);
            }
            this.applicationCore.refreshDisplay();
        });

        topContainer.add(topToolTabs, BorderLayout.CENTER);

        // --- THE COLLAPSE ARROW ---
        JButton topToggleBtn = new JButton("▲");
        topToggleBtn.setMargin(new Insets(0, 0, 0, 0));
        topToggleBtn.setBackground(ColorPalette.LIST_ROW_BACKGROUND);
        topToggleBtn.setForeground(ColorPalette.TEXT_LIGHT);
        topToggleBtn.setFocusPainted(false);
        topToggleBtn.setPreferredSize(new Dimension(0, 15));

        topToggleBtn.addActionListener(event -> {
            boolean isVisible = topContainer.isVisible();
            topContainer.setVisible(!isVisible);
            topToggleBtn.setText(isVisible ? "▼" : "▲");

            ToolState toolState = this.applicationCore.getToolState();
            if(isVisible) {
                // The panel was just hidden. Force the tool into Drag/Pan mode.
                toolState.setCurrentTool(ToolType.TOKEN);
            } else {
                // The panel was just shown. Restore tool based on active tab.
                String selectedTab = topToolTabs.getTitleAt(topToolTabs.getSelectedIndex());
                if("Drawing".equals(selectedTab)) {
                    toolState.setCurrentTool(ToolType.DRAWING);
                } else if("Rulers".equals(selectedTab)) {
                    toolState.setCurrentTool(ToolType.TEMPLATE);
                } else {
                    toolState.setCurrentTool(ToolType.TOKEN);
                }
            }
            // Ensure the main frame re-lays out the screen to accommodate the shifting UI
            this.applicationCore.getMainFrame().revalidate();
            this.applicationCore.refreshDisplay();
        });

        topWrapper.add(topContainer, BorderLayout.CENTER);
        topWrapper.add(topToggleBtn, BorderLayout.SOUTH);

        // Bind the completed top wrapper
        containerPanel.add(topWrapper, BorderLayout.NORTH);

        // --- CENTER AREA: Canvas ---
        containerPanel.add(this.applicationCore.getCanvasPanel(), BorderLayout.CENTER);

        // --- RIGHT AREA: Characters & Info ---
        RightPanelBuilder rightPanelBuilder = new RightPanelBuilder(this.applicationCore);
        containerPanel.add(rightPanelBuilder.buildRightWrapper(), BorderLayout.EAST);
    }
}
