package tabletop.ui.core;

import javax.swing.JPanel;
import java.awt.Graphics;

import tabletop.main.ApplicationCore;
import tabletop.controller.InteractionController;
import tabletop.ui.theme.ColorPalette;

/**
 * Represents the primary drawing surface for the map.
 *
 * @author Adi
 */
public class CanvasPanel extends JPanel {

    private final ApplicationCore applicationCore;
    private final RenderEngine renderEngine;

    public CanvasPanel(ApplicationCore applicationCore) {
        super();
        this.applicationCore = applicationCore;
        this.renderEngine = new RenderEngine(this.applicationCore);

        this.setBackground(ColorPalette.BACKGROUND_DARK);
        this.setFocusable(true);
        this.requestFocusInWindow();

        InteractionController interactionController = new InteractionController(this.applicationCore);
        this.addMouseListener(interactionController);
        this.addMouseMotionListener(interactionController);
        this.addMouseWheelListener(interactionController);
    }

    @Override
    protected void paintComponent(Graphics renderGraphics) {
        super.paintComponent(renderGraphics);
        this.renderEngine.renderAll(renderGraphics, this.getWidth(), this.getHeight());
    }
}