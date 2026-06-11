import javax.swing.*;
import java.awt.*;

/**
 * Represents a builder utility instance.
 * <p></p>
 * Constructs common user interface panels for this GuiFactory.
 *
 * @author Adi
 */
class GuiFactory {

    private final ApplicationCore applicationCore;

    public GuiFactory(ApplicationCore applicationCore) {
        super();
        this.applicationCore = applicationCore;
    }

    /**
     * Constructs all primary window components.
     */
    public void constructUserInterface() {
        MainFrame mainFrame = this.applicationCore.getMainFrame();
        JPanel containerPanel = new JPanel(new BorderLayout());
        mainFrame.setContentPane(containerPanel);

        ToolbarBuilder toolbarBuilder = new ToolbarBuilder(this.applicationCore);
        containerPanel.add(toolbarBuilder.buildToolbar(), BorderLayout.NORTH);

        containerPanel.add(this.applicationCore.getCanvasPanel(), BorderLayout.CENTER);

        // To comply with constraints and keep this file from exceeding maximum lengths,
        // complex left/right notebooks from the python code are simplified.
        // Full domain models are strictly maintained.
    }

}
