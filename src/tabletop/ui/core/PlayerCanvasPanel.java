package tabletop.ui.core;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Color;
import tabletop.main.ApplicationCore;
import tabletop.ui.theme.ColorPalette;

/**
 * Represents the drawing surface for the secondary player screen.
 *
 * @author Adi
 */
public class PlayerCanvasPanel extends JPanel {

    private final ApplicationCore applicationCore;
    private final RenderEngine renderEngine;

    public PlayerCanvasPanel(ApplicationCore applicationCore) {
        super();
        this.applicationCore = applicationCore;
        this.renderEngine = new RenderEngine(this.applicationCore);
        this.setBackground(ColorPalette.BACKGROUND_DARK);

        // No InteractionController is added here! Players cannot click or drag this screen.
    }

    @Override
    protected void paintComponent(Graphics renderGraphics) {
        super.paintComponent(renderGraphics);

        if(this.applicationCore.getToolState().isPlayerScreenBlackout()) {
            // Draw a completely black screen if the DM stops sharing
            renderGraphics.setColor(Color.BLACK);
            renderGraphics.fillRect(0, 0, this.getWidth(), this.getHeight());
        } else {
            // Otherwise, render the exact same map and camera angle the DM sees
            this.renderEngine.renderAll(renderGraphics, this.getWidth(), this.getHeight());
        }
    }
}
