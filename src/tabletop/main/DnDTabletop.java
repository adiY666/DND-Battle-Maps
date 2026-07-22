package tabletop.main;

import javax.swing.UIManager; // Add this import!

public class DnDTabletop {

    public static void main(String[] args) {

        // --- ADD THIS BLOCK TO FIX THE WHITE BUTTONS ---
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        // -----------------------------------------------

        // Keep your existing startup code below...
        ApplicationCore applicationCore = new ApplicationCore();
        applicationCore.initializeApplication();
    }
}
