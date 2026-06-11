package tabletop.main;

import tabletop.state.DataState;
import tabletop.state.ToolState;
import tabletop.ui.core.CanvasPanel;
import tabletop.ui.core.GuiFactory;
import tabletop.ui.core.MainFrame;

/**
 * Represents the primary engine instance.
 *
 * @author Adi
 */
public class ApplicationCore {

    private final DataState dataState;
    private final ToolState toolState;
    private final MainFrame mainFrame;
    private final CanvasPanel canvasPanel;

    // NEW: Callback to trigger list refreshes across panels
    public Runnable onTokenListChanged;

    public ApplicationCore() {
        super();
        this.dataState = new DataState();
        this.toolState = new ToolState();
        this.mainFrame = new MainFrame(this);
        this.canvasPanel = new CanvasPanel(this);
    }

    /**
     * Starts the application processes.
     */
    public void initializeApplication() {
        GuiFactory guiFactory = new GuiFactory(this);
        guiFactory.constructUserInterface();
        this.mainFrame.setVisible(true);
        this.refreshDisplay();
    }

    /**
     * Triggers a visual update.
     */
    public void refreshDisplay() {
        this.canvasPanel.repaint();
    }

    public DataState getDataState() {
        return this.dataState;
    }

    public ToolState getToolState() {
        return this.toolState;
    }

    public MainFrame getMainFrame() {
        return this.mainFrame;
    }

    public CanvasPanel getCanvasPanel() {
        return this.canvasPanel;
    }
}
