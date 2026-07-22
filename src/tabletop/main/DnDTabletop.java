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

    public static void main(String[] args) {

        // --- ADD THIS BLOCK ---
        // Forces Java to respect custom background colors instead of using the OS defaults
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        // ----------------------

        // Your existing startup code should look something like this:
        ApplicationCore applicationCore = new ApplicationCore();
        applicationCore.initializeApplication();
    }
}

