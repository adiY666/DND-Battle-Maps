package tabletop.util;

/**
 * Represents a mathematics calculation instance.
 * <p></p>
 * Provides geometry algorithms for this tabletop.util.MathUtility.
 *
 * @author Adi
 */
public class MathUtility {

    public MathUtility() {
        super();
    }

    /**
     * Calculates the distance between two logical points.
     *
     * @param horizontalFirst  the first horizontal position
     * @param verticalFirst    the first vertical position
     * @param horizontalSecond the second horizontal position
     * @param verticalSecond   the second vertical position
     * @return the calculated distance
     */
    public double calculateDistance(double horizontalFirst, double verticalFirst, double horizontalSecond, double verticalSecond) {
        double differenceHorizontal = horizontalSecond - horizontalFirst;
        double differenceVertical = verticalSecond - verticalFirst;
        return Math.hypot(differenceHorizontal, differenceVertical);
    }

}
