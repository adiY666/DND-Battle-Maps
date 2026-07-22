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
    }

    @Override
    protected void paintComponent(Graphics renderGraphics) {
        super.paintComponent(renderGraphics);

        if(this.applicationCore.getToolState().isPlayerScreenBlackout()) {
            renderGraphics.setColor(Color.BLACK);
            renderGraphics.fillRect(0, 0, this.getWidth(), this.getHeight());
        } else {
            this.renderEngine.renderAll(renderGraphics, this.getWidth(), this.getHeight(), true);
        }
    }
}
