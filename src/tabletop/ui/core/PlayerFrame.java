package tabletop.ui.core;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;

import tabletop.main.ApplicationCore;
import tabletop.ui.theme.ColorPalette;

/**
 * Represents the secondary window broadcasted to the players.
 *
 * @author Adi
 */
public class PlayerFrame extends JFrame {

    private final ApplicationCore applicationCore;
    private final PlayerCanvasPanel playerCanvasPanel;
    private boolean isFullScreen = false;

    public PlayerFrame(ApplicationCore applicationCore) {
        super("D&D Virtual Tabletop - Player View");
        this.applicationCore = applicationCore;

        this.setSize(1280, 720);
        this.setLayout(new BorderLayout());
        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        this.getContentPane().setBackground(ColorPalette.BACKGROUND_DARK);

        this.playerCanvasPanel = new PlayerCanvasPanel(this.applicationCore);
        this.add(this.playerCanvasPanel, BorderLayout.CENTER);

        this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0), "toggleFullscreen");

        this.getRootPane().getActionMap().put("toggleFullscreen", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleFullScreen();
            }
        });
    }

    public void toggleFullScreen() {

        this.dispose();

        if (!this.isFullScreen) {
            this.setUndecorated(true);
            this.setExtendedState(JFrame.MAXIMIZED_BOTH);
            this.isFullScreen = true;
        } else {
            this.setUndecorated(false);
            this.setExtendedState(JFrame.NORMAL);
            this.setSize(1280, 720);
            this.isFullScreen = false;
        }

        this.setVisible(true);
    }

    /**
     * Forces the player's canvas to redraw. Called by ApplicationCore.
     */
    public void repaintCanvas() {
        if (this.playerCanvasPanel != null) {
            this.playerCanvasPanel.repaint();
        }
    }
}
