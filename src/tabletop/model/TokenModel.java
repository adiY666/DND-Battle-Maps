package tabletop.model;

/**
 * Represents a game miniature instance.
 * <p></p>
 * Encapsulates token properties and image path for this tabletop.model.TokenModel.
 *
 * @author Adi
 */
public class TokenModel implements java.io.Serializable {

    private int identifier;
    private int movementSpeed;
    private int gridScale;
    private int rotationAngle;
    private String displayName;
    private String imageFilepath;
    private double positionHorizontal;
    private double positionVertical;
    private boolean rangeVisible;
    private java.util.List<EffectModel> activeEffects;
    private boolean showMovementRange;

    private transient java.awt.image.BufferedImage originalImage;

    /**
     * Instantiates a new token.
     *
     * @param identifier         the unique numeric id
     * @param displayName        the character name
     * @param positionHorizontal the x location
     * @param positionVertical   the y location
     * @param imageFilepath      the disk location
     */
    public TokenModel(int identifier, String displayName, double positionHorizontal, double positionVertical, String imageFilepath) {
        this.identifier = identifier;
        this.displayName = displayName;
        this.positionHorizontal = positionHorizontal;
        this.positionVertical = positionVertical;
        this.imageFilepath = imageFilepath;
        this.movementSpeed = 6;
        this.gridScale = 1;
        this.rotationAngle = 0;
        this.rangeVisible = false;
        this.activeEffects = new java.util.ArrayList<>();
    }

    public int getIdentifier() {
        return this.identifier;
    }

    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    public int getMovementSpeed() {
        return this.movementSpeed;
    }

    public void setMovementSpeed(int movementSpeed) {
        this.movementSpeed = movementSpeed;
    }

    public int getGridScale() {
        return this.gridScale;
    }

    public void setGridScale(int gridScale) {
        this.gridScale = gridScale;
    }

    public int getRotationAngle() {
        return this.rotationAngle;
    }

    public void setRotationAngle(int rotationAngle) {
        this.rotationAngle = rotationAngle;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getImageFilepath() {
        return this.imageFilepath;
    }

    public void setImageFilepath(String imageFilepath) {
        this.imageFilepath = imageFilepath;
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

    public boolean isRangeVisible() {
        return this.rangeVisible;
    }

    public void setRangeVisible(boolean rangeVisible) {
        this.rangeVisible = rangeVisible;
    }

    public java.util.List<EffectModel> getActiveEffects() {
        return this.activeEffects;
    }

    public void setActiveEffects(java.util.List<EffectModel> activeEffects) {
        this.activeEffects = activeEffects;
    }

    public java.awt.image.BufferedImage getOriginalImage() {
        return this.originalImage;
    }

    public void setOriginalImage(java.awt.image.BufferedImage originalImage) {
        this.originalImage = originalImage;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || this.getClass() != object.getClass()) return false;
        TokenModel that = (TokenModel) object;
        return this.identifier == that.identifier;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(this.identifier);
    }

    @Override
    public String toString() {
        return "tabletop.model.TokenModel{name='" + this.displayName + "'}";
    }

    public boolean isShowMovementRange() {
        return this.showMovementRange;
    }

    public void setShowMovementRange(boolean showMovementRange) {
        this.showMovementRange = showMovementRange;
    }

}
