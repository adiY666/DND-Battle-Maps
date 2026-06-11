package tabletop.controller;

import tabletop.main.ApplicationCore;
import tabletop.model.TokenModel;
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

            // Safety Check: Do not trigger shortcuts if the user is typing in a text field
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
            }
        }
        return false;
    }
}
