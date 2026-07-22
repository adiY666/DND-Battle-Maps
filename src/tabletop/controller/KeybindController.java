package tabletop.controller;

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import javax.swing.JTextField;
import javax.swing.JTextArea;

import tabletop.main.ApplicationCore;
import tabletop.model.TokenModel;
import tabletop.ui.tabs.TokensTabBuilder;

/**
 * Intercepts keyboard shortcuts for quick map and token actions globally.
 *
 * @author Adi
 */

public class KeybindController implements KeyEventDispatcher {

    private final ApplicationCore applicationCore;

    public KeybindController(ApplicationCore applicationCore) {
        this.applicationCore = applicationCore;
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(this);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        if (e.getID() != KeyEvent.KEY_PRESSED) {
            return false;
        }

        if (e.getSource() instanceof JTextField || e.getSource() instanceof JTextArea) {
            return false;
        }

        if (e.getKeyCode() == KeyEvent.VK_S) {
            boolean isCurrentlySnapping = this.applicationCore.getDataState().isSnapToGrid();
            this.applicationCore.getDataState().setSnapToGrid(!isCurrentlySnapping);

            if (TokensTabBuilder.snapCheckBox != null) {
                TokensTabBuilder.snapCheckBox.setSelected(!isCurrentlySnapping);
            }
            return true;
        }

        if (e.getKeyCode() == KeyEvent.VK_R) {
            Integer selectedTokenId = this.applicationCore.getToolState().getSelectedTokenIdentifier();

            if (selectedTokenId != null) {
                TokenModel token = this.applicationCore.getDataState().getActiveTokens().get(selectedTokenId);
                if (token != null) {
                    token.setShowMovementRange(!token.isShowMovementRange());
                    this.applicationCore.refreshDisplay();

                    if (this.applicationCore.onSelectionChanged != null) {
                        this.applicationCore.onSelectionChanged.run();
                    }
                }
            }
            return true;
        }

        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            this.applicationCore.advanceTurn();
            return true;
        }

        return false;
    }
}
