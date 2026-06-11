package tabletop.main;

import tabletop.ui.core.MainFrame;
import tabletop.ui.core.CanvasPanel;
import tabletop.ui.core.GuiFactory;
import tabletop.state.DataState;
import tabletop.state.ToolState;
import tabletop.controller.KeybindController;

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

    public Runnable onTokenListChanged;
    public Runnable onSelectionChanged;
    public Runnable onEffectsChanged;

    public ApplicationCore() {
        super();
        this.dataState = new DataState();
        this.toolState = new ToolState();
        this.mainFrame = new MainFrame(this);
        this.canvasPanel = new CanvasPanel(this);

        // Register the global shortcut controller
        new KeybindController(this).registerGlobal();
    }

    public void initializeApplication() {
        GuiFactory guiFactory = new GuiFactory(this);
        guiFactory.constructUserInterface();
        this.mainFrame.setVisible(true);
        this.refreshDisplay();
    }

    public void refreshDisplay() {
        this.canvasPanel.repaint();
    }

    /**
     * Iterates through all tokens, reducing their active effect durations by 1.
     * Removes effects that have expired and refreshes the UI.
     */
    public void advanceTurn() {
        boolean effectsChanged = false;

        for(tabletop.model.TokenModel token : this.dataState.getActiveTokens().values()) {
            java.util.Iterator<tabletop.model.EffectModel> iterator = token.getActiveEffects().iterator();
            while(iterator.hasNext()) {
                tabletop.model.EffectModel effect = iterator.next();
                effect.setRemainingTurns(effect.getRemainingTurns() - 1);
                if(effect.getRemainingTurns() <= 0) {
                    iterator.remove();
                }
                effectsChanged = true;
            }
        }

        if(effectsChanged) {
            if(this.onEffectsChanged != null) this.onEffectsChanged.run();
            if(this.onSelectionChanged != null) this.onSelectionChanged.run();
            this.refreshDisplay();
        }
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
