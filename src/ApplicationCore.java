/**
 * Represents the primary engine instance.
 * <p></p>
 * Orchestrates the user interface and data state for this ApplicationCore.
 *
 * @author Adi
 */
class ApplicationCore {

    private final DataState dataState;
    private final ToolState toolState;
    private final MainFrame mainFrame;
    private final CanvasPanel canvasPanel;

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
