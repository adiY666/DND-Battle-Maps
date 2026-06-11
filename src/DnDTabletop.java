import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Represents the main entry point class.
 * <p></p>
 * Initializes the entire virtual tabletop application for this DnDTabletop.
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

/**
 * Represents tool types for the application.
 * <p></p>
 * Defines available editing modes for this ToolType.
 *
 * @author Adi
 */
enum ToolType {

    TOKEN,
    DRAWING,
    TEMPLATE;

}

/**
 * Represents drawing substates for the application.
 * <p></p>
 * Defines available drawing modes for this DrawingSubtool.
 *
 * @author Adi
 */
enum DrawingSubtool {

    PEN,
    ERASE_CLICK,
    ERASE_BRUSH;
}
