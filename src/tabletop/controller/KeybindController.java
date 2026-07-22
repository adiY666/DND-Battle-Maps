package tabletop.controller;

import tabletop.main.ApplicationCore;
import tabletop.main.ToolType;
import tabletop.model.TokenModel;
import tabletop.ui.tabs.TokensTabBuilder;

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import javax.swing.JTextField;

/**
 * Handles global keyboard input events.
 *
 * @author Adi
 */
public class KeybindController implements KeyEventDispatcher {

    private final ApplicationCore applicationCore;

    public KeybindController(ApplicationCore applicationCore) {
        super();
        this.applicationCore = applicationCore;
    }

    public void registerGlobal() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(this);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        if(keyEvent.getID() == KeyEvent.KEY_PRESSED) {

            if(KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner() instanceof JTextField) {
                return false;
            }

            if(keyEvent.getKeyCode() == KeyEvent.VK_R) {
                Integer tokenId = this.applicationCore.getToolState().getSelectedTokenIdentifier();
                if(tokenId != null) {
                    TokenModel token = this.applicationCore.getDataState().getActiveTokens().get(tokenId);
                    if(token != null) {
                        token.setShowMovementRange(!token.isShowMovementRange());

                        if(this.applicationCore.onSelectionChanged != null) {
                            this.applicationCore.onSelectionChanged.run();
                        }
                        this.applicationCore.refreshDisplay();
                        return true;
                    }
                }
            } else if(keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
                this.applicationCore.advanceTurn();
                return true;
            } else if(keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE) {
                this.applicationCore.getToolState().setCurrentTool(ToolType.TOKEN);
                this.applicationCore.getToolState().setSelectedTokenIdentifier(null);
                this.applicationCore.getToolState().setSelectedTemplateIdentifier(null);

                if(this.applicationCore.onSelectionChanged != null) this.applicationCore.onSelectionChanged.run();
                if(this.applicationCore.onTemplateSelectionChanged != null) this.applicationCore.onTemplateSelectionChanged.run();

                this.applicationCore.refreshDisplay();
                return true;

            } else if(keyEvent.getKeyCode() == KeyEvent.VK_DELETE || keyEvent.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                Integer selectedTemplateId = this.applicationCore.getToolState().getSelectedTemplateIdentifier();
                if(selectedTemplateId != null) {
                    this.applicationCore.getDataState().getActiveTemplates().removeIf(t -> t.getIdentifier() == selectedTemplateId);
                    this.applicationCore.getToolState().setSelectedTemplateIdentifier(null);

                    if(this.applicationCore.onTemplateSelectionChanged != null) {
                        this.applicationCore.onTemplateSelectionChanged.run();
                    }
                    this.applicationCore.refreshDisplay();
                    return true;
                }
            } else if(keyEvent.getKeyCode() == KeyEvent.VK_S) {
                // NEW: Toggle Snap to Grid
                boolean currentSnap = this.applicationCore.getDataState().isSnapToGrid();
                this.applicationCore.getDataState().setSnapToGrid(!currentSnap);

                // Keep the UI checkbox checkmark in sync
                if(TokensTabBuilder.snapCheckBox != null) {
                    TokensTabBuilder.snapCheckBox.setSelected(!currentSnap);
                }

                return true;
            }
        }
        return false;
    }
}
