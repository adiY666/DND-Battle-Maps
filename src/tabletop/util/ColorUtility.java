package tabletop.util;

import java.awt.*;

/**
 * Represents a color mapping utility instance.
 * <p></p>
 * Translates display names to color objects for this tabletop.util.ColorUtility.
 *
 * @author Adi
 */
public class ColorUtility {

    private static final java.util.Map<String, Color> COLOR_DICTIONARY = new java.util.HashMap<>();

    static {
        COLOR_DICTIONARY.put("Red", Color.RED);
        COLOR_DICTIONARY.put("Green", Color.GREEN);
        COLOR_DICTIONARY.put("Blue", Color.BLUE);
        COLOR_DICTIONARY.put("Yellow", Color.YELLOW);
        COLOR_DICTIONARY.put("Purple", new Color(128, 0, 128));
        COLOR_DICTIONARY.put("Orange", new Color(255, 165, 0));
        COLOR_DICTIONARY.put("Black", Color.BLACK);
        COLOR_DICTIONARY.put("White", Color.WHITE);
        COLOR_DICTIONARY.put("Cyan", Color.CYAN);
        COLOR_DICTIONARY.put("Magenta", Color.MAGENTA);
    }

    public ColorUtility() {
        super();
    }

    /**
     * Retrieves a mapped color.
     *
     * @param colorName the display name of the color
     * @return the associated color object
     */
    public Color retrieveColor(String colorName) {
        if (colorName == null) throw new IllegalArgumentException("Color name cannot be null");
        if (COLOR_DICTIONARY.containsKey(colorName)) return COLOR_DICTIONARY.get(colorName);
        return Color.BLACK;
    }

}
