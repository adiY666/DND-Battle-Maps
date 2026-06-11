import javax.swing.*;
import java.awt.*;

/**
 * Represents the primary drawing area instance.
 * <p></p>
 * Displays the entire tabletop board for this CanvasPanel.
 *
 * @author Adi
 */
class CanvasPanel extends JPanel {

    private final ApplicationCore applicationCore;
    private final RenderEngine renderEngine;

    public CanvasPanel(ApplicationCore applicationCore) {
        super();
        this.applicationCore = applicationCore;
        this.renderEngine = new RenderEngine(applicationCore);
        InteractionController interactionController = new InteractionController(applicationCore);
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
