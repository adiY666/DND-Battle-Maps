package tabletop.main;

/**
 * Represents the configuration constants instance.
 * <p></p>
 * Stores all global magic numbers and defaults for this tabletop.main.Constants.
 *
 * @author Adi
 */
public class Constants {

    public static final int BASE_CELL_SIZE = 60;
    public static final int MAXIMUM_WINDOW_WIDTH = 1100;
    public static final int MAXIMUM_WINDOW_HEIGHT = 750;
    public static final java.util.List<String> AVAILABLE_COLORS = java.util.List.of(
            "Red", "Green", "Blue", "Yellow", "Purple", "Orange", "Black", "White", "Cyan", "Magenta"
    );

    public Constants() {
        super();
    }

}
