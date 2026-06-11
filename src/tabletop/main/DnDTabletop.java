package tabletop.main;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Represents the main entry point class.
 * <p></p>
 * Initializes the entire virtual tabletop application for this tabletop.main.DnDTabletop.
 *
 * @author Adi
 */
public class DnDTabletop {

    /**
     * Launches this application.
     *
     * @param arguments the command line arguments
     */
    public static void main(String... arguments) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch(Exception exception) {
            exception.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> {
            ApplicationCore applicationCore = new ApplicationCore();
            applicationCore.initializeApplication();
        });
    }

}

