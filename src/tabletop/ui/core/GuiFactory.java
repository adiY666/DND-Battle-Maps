package tabletop.ui.core;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import tabletop.main.ApplicationCore;
import tabletop.main.ToolType;
import tabletop.model.PinModel;
import tabletop.state.Coordinate;
import tabletop.ui.layout.ToolbarBuilder;
import tabletop.ui.tabs.DrawingTabBuilder;
import tabletop.ui.tabs.EffectsTabBuilder;
import tabletop.ui.tabs.NotesTabBuilder;
import tabletop.ui.tabs.PinsTabBuilder;
import tabletop.ui.tabs.RulersTabBuilder;
import tabletop.ui.tabs.TokensTabBuilder;
import tabletop.ui.theme.ColorPalette;

/**
 * Responsible for assembling the main user interface.
 *
 * @author Adi
 */
public class GuiFactory {
    private final ApplicationCore applicationCore;

    public GuiFactory(ApplicationCore applicationCore) {
        this.applicationCore = applicationCore;
    }

    public void constructUserInterface() {
        MainFrame mainFrame = this.applicationCore.getMainFrame();
        mainFrame.setLayout(new BorderLayout());

        // 1. Right Sidebar Tabs (Declared first so the top toolbar can navigate to it)
        JTabbedPane rightSidebarTabs = new JTabbedPane();
        rightSidebarTabs.setBackground(ColorPalette.BACKGROUND_DARK);
        rightSidebarTabs.setForeground(ColorPalette.TEXT_LIGHT);
        rightSidebarTabs.setFocusable(false);

        rightSidebarTabs.addTab("Tokens", new TokensTabBuilder(this.applicationCore).buildTab());
        rightSidebarTabs.addTab("Effects", new EffectsTabBuilder(this.applicationCore).buildTab());
        rightSidebarTabs.addTab("Notes", new NotesTabBuilder(this.applicationCore).buildTab());
        rightSidebarTabs.addTab("Pins List", new PinsTabBuilder(this.applicationCore).buildTab());

        rightSidebarTabs.setPreferredSize(new Dimension(350, mainFrame.getHeight()));
        rightSidebarTabs.setMinimumSize(new Dimension(300, 0));

        // 2. Build the Top Tabbed Toolbar
        JTabbedPane topToolbarsTabs = new JTabbedPane();
        topToolbarsTabs.setBackground(ColorPalette.BACKGROUND_DARK);
        topToolbarsTabs.setForeground(ColorPalette.TEXT_LIGHT);
        topToolbarsTabs.setFocusable(false);

        ToolbarBuilder systemToolbar = new ToolbarBuilder(this.applicationCore);

        topToolbarsTabs.addTab("System & Map", systemToolbar.buildToolbar());
        topToolbarsTabs.addTab("Draw Tool", new DrawingTabBuilder(this.applicationCore).buildTab());
        topToolbarsTabs.addTab("Rulers & AoE", new RulersTabBuilder(this.applicationCore).buildTab());

        // Build the custom "Map Pins" top panel
        JPanel pinsTopPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        pinsTopPanel.setBackground(ColorPalette.BACKGROUND_DARK);

        JButton dropPinBtn = new JButton("+ Drop New Pin");
        this.styleButton(dropPinBtn, ColorPalette.BUTTON_SUCCESS);
        dropPinBtn.addActionListener(e -> {
            JPanel popUpPanel = new JPanel(new GridLayout(3, 2, 5, 10));
            popUpPanel.setBackground(ColorPalette.BACKGROUND_DARK);

            JLabel nameLabel = new JLabel("Pin Title:");
            nameLabel.setForeground(ColorPalette.TEXT_LIGHT);
            JTextField nameField = new JTextField("New Map Location");
            nameField.setBackground(ColorPalette.INPUT_BACKGROUND);
            nameField.setForeground(ColorPalette.TEXT_LIGHT);

            JLabel colorLabel = new JLabel("Pin Color:");
            colorLabel.setForeground(ColorPalette.TEXT_LIGHT);
            ColorPicker colorPicker = new ColorPicker();

            JLabel shareLabel = new JLabel("Visible to Players?");
            shareLabel.setForeground(ColorPalette.TEXT_LIGHT);
            JCheckBox sharedBox = new JCheckBox();
            sharedBox.setBackground(ColorPalette.BACKGROUND_DARK);
            sharedBox.setFocusPainted(false);

            popUpPanel.add(nameLabel);
            popUpPanel.add(nameField);
            popUpPanel.add(colorLabel);
            popUpPanel.add(colorPicker);
            popUpPanel.add(shareLabel);
            popUpPanel.add(sharedBox);

            int result = JOptionPane.showConfirmDialog(this.applicationCore.getMainFrame(), popUpPanel, "Create New Map Pin", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                Coordinate center = this.applicationCore.getDataState().convertScreenToLogical(this.applicationCore.getCanvasPanel().getWidth() / 2, this.applicationCore.getCanvasPanel().getHeight() / 2);
                PinModel newPin = new PinModel(center.getCoordinateHorizontal(), center.getCoordinateVertical());

                newPin.setTitle(nameField.getText().trim());
                newPin.setPinColor(colorPicker.getSelectedColor());
                newPin.setShared(sharedBox.isSelected());

                this.applicationCore.getDataState().getMapPins().add(newPin);
                this.applicationCore.getToolState().setSelectedPinIndex(this.applicationCore.getDataState().getMapPins().size() - 1);

                // Automatically jump to the Pins List right-tab so they can edit notes immediately
                for (int i = 0; i < rightSidebarTabs.getTabCount(); i++) {
                    if (rightSidebarTabs.getTitleAt(i).equals("Pins List")) {
                        rightSidebarTabs.setSelectedIndex(i);
                        break;
                    }
                }

                if(this.applicationCore.onPinSelectionChanged != null) this.applicationCore.onPinSelectionChanged.run();
                this.applicationCore.refreshDisplay();
            }
        });

        JButton clearPinsBtn = new JButton("Clear All Pins");
        this.styleButton(clearPinsBtn, ColorPalette.BUTTON_DANGER);
        clearPinsBtn.addActionListener(e -> {
            this.applicationCore.getDataState().getMapPins().clear();
            this.applicationCore.getToolState().setSelectedPinIndex(null);
            if(this.applicationCore.onPinSelectionChanged != null) this.applicationCore.onPinSelectionChanged.run();
            this.applicationCore.refreshDisplay();
        });

        ToggleSwitch sharePinsToggle = new ToggleSwitch("Broadcast Shared Pins");
        sharePinsToggle.setBackground(ColorPalette.BACKGROUND_DARK);
        sharePinsToggle.setForeground(ColorPalette.BUTTON_SUCCESS);
        sharePinsToggle.setSelected(this.applicationCore.getToolState().isShowSharedPinsOnPlayerScreen());
        sharePinsToggle.addActionListener(e -> {
            this.applicationCore.getToolState().setShowSharedPinsOnPlayerScreen(sharePinsToggle.isSelected());
            this.applicationCore.refreshDisplay();
        });

        JButton pinsListBtn = new JButton("Open Pins List");
        this.styleButton(pinsListBtn, ColorPalette.BUTTON_PRIMARY);
        pinsListBtn.addActionListener(e -> {
            for (int i = 0; i < rightSidebarTabs.getTabCount(); i++) {
                if (rightSidebarTabs.getTitleAt(i).equals("Pins List")) {
                    rightSidebarTabs.setSelectedIndex(i);
                    break;
                }
            }
        });

        pinsTopPanel.add(dropPinBtn);
        pinsTopPanel.add(clearPinsBtn);
        pinsTopPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        pinsTopPanel.add(sharePinsToggle);
        pinsTopPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        pinsTopPanel.add(pinsListBtn);

        topToolbarsTabs.addTab("Map Pins", pinsTopPanel);

        // Automatically change the active tool based on the selected tab
        topToolbarsTabs.addChangeListener(e -> {
            int selectedIndex = topToolbarsTabs.getSelectedIndex();
            if (selectedIndex == 1) {
                this.applicationCore.getToolState().setCurrentTool(ToolType.DRAWING);
            } else if (selectedIndex == 2) {
                this.applicationCore.getToolState().setCurrentTool(ToolType.TEMPLATE);
            } else {
                this.applicationCore.getToolState().setCurrentTool(ToolType.TOKEN);
            }
        });

        // CONSTRAIN HEIGHT TO REMOVE DEAD SPACE (75 is perfect for a tab header + 1 row of buttons)
        topToolbarsTabs.setPreferredSize(new Dimension(mainFrame.getWidth(), 75));
        mainFrame.add(topToolbarsTabs, BorderLayout.NORTH);

        // 3. Build the Main Canvas (Map)
        CanvasPanel canvasPanel = this.applicationCore.getCanvasPanel();

        // 4. Split the screen
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, canvasPanel, rightSidebarTabs);
        splitPane.setResizeWeight(1.0);
        splitPane.setDividerSize(5);
        splitPane.setBorder(null);

        mainFrame.add(splitPane, BorderLayout.CENTER);
    }

    private void styleButton(JButton button, java.awt.Color backgroundColor) {
        button.setBackground(backgroundColor);
        button.setForeground(ColorPalette.TEXT_LIGHT);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorderPainted(false);
    }
}
