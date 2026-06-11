import javax.swing.*;

/**
 * Represents the primary window instance.
 * <p></p>
 * Encapsulates the OS window configurations for this MainFrame.
 *
 * @author Adi
 */
class MainFrame extends JFrame {

    private final ApplicationCore applicationCore;

    public MainFrame(ApplicationCore applicationCore) {
        super("D&D Virtual Tabletop - Advanced Edition");
        this.applicationCore = applicationCore;
        this.setSize(Constants.MAXIMUM_WINDOW_WIDTH, Constants.MAXIMUM_WINDOW_HEIGHT);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

}
