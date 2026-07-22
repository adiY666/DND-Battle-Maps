package tabletop.ui.theme;

import java.awt.Color;

/**
 * Constants and magic numbers used exclusively for rendering the map canvas.
 *
 * @author Adi
 */
public class RenderConstants {

    public static final int MAX_GRID_LINES = 200;
    public static final double GRID_FEET_PER_SQUARE = 5.0;

    // Stroke Thickness
    public static final float STROKE_THIN = 1.0f;
    public static final float STROKE_PIN_OUTLINE = 1.5f; // New for pins
    public static final float STROKE_MEDIUM = 2.0f;
    public static final float STROKE_THICK = 3.0f;
    public static final float STROKE_POINTER = 6.0f;
    public static final float[] DASH_PATTERN = {10.0f};

    // Text & UI Offsets
    public static final int TEXT_OFFSET_Y = 5;
    public static final int TEXT_PADDING_X = 4;
    public static final int TEXT_PADDING_Y = 2;
    public static final int EFFECT_START_Y = 15;
    public static final int EFFECT_LINE_HEIGHT = 18;
    public static final int TEMPLATE_FILL_ALPHA = 90;
    public static final int TEMPLATE_OUTLINE_ALPHA = 200;

    // Geometry Sizes
    public static final int TEMPLATE_ANCHOR_RADIUS = 4;
    public static final int POINTER_MIN_DRAG_DIST = 5;
    public static final int POINTER_ARROW_SIZE = 25;
    public static final double POINTER_ARROW_ANGLE = Math.PI / 7.0;
    public static final int POINTER_ANCHOR_RADIUS = 8;

    // --- NEW: Map Pin Geometry & Text ---
    public static final int PIN_RADIUS = 12;
    public static final int PIN_DIAMETER = 24;
    public static final int PIN_HIGHLIGHT_OFFSET = 4;
    public static final int PIN_HIGHLIGHT_RADIUS = 16;
    public static final int PIN_HIGHLIGHT_DIAMETER = 32;
    public static final int PIN_TITLE_OFFSET_Y = 35;
    public static final int PIN_TITLE_FONT_SIZE = 14;

    // Specific Map Rendering Colors (Not covered by global UI Theme)
    public static final Color COLOR_GRID_LINES = new Color(255, 255, 255, 60);
    public static final Color COLOR_TOKEN_DEFAULT = Color.GRAY;
    public static final Color COLOR_HIGHLIGHT = Color.YELLOW;
    public static final Color COLOR_POINTER = new Color(255, 60, 60, 220);

}
