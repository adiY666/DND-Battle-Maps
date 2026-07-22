package tabletop.main;

import tabletop.state.DataState;
import tabletop.state.ToolState;
import tabletop.ui.core.CanvasPanel;
import tabletop.ui.core.GuiFactory;
import tabletop.ui.core.MainFrame;
import tabletop.ui.core.PlayerFrame;
import tabletop.model.TokenModel;
import tabletop.model.EffectModel;

/**
 * The central hub of the Virtual Tabletop application.
 * Manages the initialization of the GUI and holds the primary state.
 *
 * @author Adi
 */
public class ApplicationCore {

    private DataState dataState;
    private final ToolState toolState;

    private MainFrame mainFrame;
    private CanvasPanel canvasPanel;
    private PlayerFrame playerFrame;

    // Callback hooks to tell the UI tabs to update when data changes
    public Runnable onTokenListChanged;
    public Runnable onSelectionChanged;
    public Runnable onPinSelectionChanged;
    public Runnable onTemplateSelectionChanged;
    public Runnable onEffectsChanged; // Restored!

    public ApplicationCore() {
        super();
        this.dataState = new DataState();
        this.toolState = new ToolState();
    }

    public void initializeApplication() {
        // 1. Initialize the core window frames and drawing canvases
        this.mainFrame = new MainFrame(this);
        this.canvasPanel = new CanvasPanel(this);
        this.playerFrame = new PlayerFrame(this);

        // 2. Delegate the complex layout building to the Factory
        GuiFactory guiFactory = new GuiFactory(this);
        guiFactory.constructUserInterface();

        // 3. Launch the application
        this.mainFrame.setVisible(true);
    }

    public DataState getDataState() {
        return this.dataState;
    }

    public void setDataState(DataState dataState) {
        this.dataState = dataState;
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

    public PlayerFrame getPlayerFrame() {
        return this.playerFrame;
    }

    /**
     * Forces both the DM map and the Player map to instantly redraw.
     */
    public void refreshDisplay() {
        if (this.canvasPanel != null) {
            this.canvasPanel.repaint();
        }
        if (this.playerFrame != null) {
            this.playerFrame.repaintCanvas();
        }
    }

    /**
     * Advances the turn tracker.
     * Decrements the duration of active effects on all tokens and cleans up expired ones.
     */
    public void advanceTurn() {
        if (this.dataState.getActiveTokens() != null) {
            for (TokenModel token : this.dataState.getActiveTokens().values()) {
                if (token.getActiveEffects() != null) {
                    java.util.Iterator<EffectModel> iterator = token.getActiveEffects().iterator();
                    while (iterator.hasNext()) {
                        EffectModel effect = iterator.next();
                        effect.setRemainingTurns(effect.getRemainingTurns() - 1);

                        // Remove the effect if its duration has hit 0
                        if (effect.getRemainingTurns() <= 0) {
                            iterator.remove();
                        }
                    }
                }
            }
        }

        this.refreshDisplay();

        if (this.onSelectionChanged != null) this.onSelectionChanged.run();
        if (this.onEffectsChanged != null) this.onEffectsChanged.run();
    }
}
