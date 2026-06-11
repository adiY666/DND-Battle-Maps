/**
 * Represents a map bookmark instance.
 * <p></p>
 * Stores location markers for this PinModel.
 *
 * @author Adi
 */
class PinModel implements java.io.Serializable {

    private int identifier;
    private String displayName;
    private String displayColor;
    private double positionHorizontal;
    private double positionVertical;

    /**
     * Instantiates a new map pin.
     *
     * @param identifier         the unique integer
     * @param displayName        the descriptive string
     * @param positionHorizontal the x location
     * @param positionVertical   the y location
     * @param displayColor       the string color
     */
    public PinModel(int identifier, String displayName, double positionHorizontal, double positionVertical, String displayColor) {
        this.identifier = identifier;
        this.displayName = displayName;
        this.positionHorizontal = positionHorizontal;
        this.positionVertical = positionVertical;
        this.displayColor = displayColor;
    }

    public int getIdentifier() {
        return this.identifier;
    }

    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayColor() {
        return this.displayColor;
    }

    public void setDisplayColor(String displayColor) {
        this.displayColor = displayColor;
    }

    public double getPositionHorizontal() {
        return this.positionHorizontal;
    }

    public void setPositionHorizontal(double positionHorizontal) {
        this.positionHorizontal = positionHorizontal;
    }

    public double getPositionVertical() {
        return this.positionVertical;
    }

    public void setPositionVertical(double positionVertical) {
        this.positionVertical = positionVertical;
    }

}
