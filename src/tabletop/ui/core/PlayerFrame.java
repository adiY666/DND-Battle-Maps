package tabletop.ui.core;

import javax.swing.JFrame;
import java.awt.BorderLayout;
import tabletop.main.ApplicationCore;
import tabletop.ui.theme.ColorPalette;

/**
 * The secondary window specifically for the players to view.
 *
 * @author Adi
 */
public class PlayerFrame extends JFrame {

    private final ApplicationCore applicationCore;
    private final PlayerCanvasPanel playerCanvasPanel;

    public PlayerFrame(ApplicationCore applicationCore) {
        super("Player View - D&D Battle Map");
        this.applicationCore = applicationCore;
        this.setSize(1280, 720); // Default resolution
        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE); // Hides it instead of closing the whole app
        this.getContentPane().setBackground(ColorPalette.BACKGROUND_DARK);

        this.playerCanvasPanel = new PlayerCanvasPanel(this.applicationCore);
        this.setLayout(new BorderLayout());
        this.add(this.playerCanvasPanel, BorderLayout.CENTER);
    }

    public void repaintCanvas() {
        this.playerCanvasPanel.repaint();
    }
}
