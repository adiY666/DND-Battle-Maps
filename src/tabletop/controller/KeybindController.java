package tabletop.controller;

import tabletop.main.ApplicationCore;
import tabletop.model.TokenModel;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Handles keyboard input events.
 *
 * @author Adi
 */
public class KeybindController extends KeyAdapter {

    private final ApplicationCore applicationCore;

    public KeybindController(ApplicationCore applicationCore) {
        super();
        this.applicationCore = applicationCore;
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        if(keyEvent.getKeyCode() == KeyEvent.VK_R) {
            Integer tokenId = this.applicationCore.getToolState().getSelectedTokenIdentifier();
            if(tokenId != null) {
                TokenModel token = this.applicationCore.getDataState().getActiveTokens().get(tokenId);
                if(token != null) {
                    token.setShowMovementRange(!token.isShowMovementRange());
                    this.applicationCore.refreshDisplay();
                }
            }
        }
    }
}
