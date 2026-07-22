package tabletop.model;

/**
 * Represents a ruler/template placed on the map.
 *
 * @author Adi
 */
public class TemplateModel implements java.io.Serializable {
    private int identifier;
    private String geometryType = "circle";
    private String displayColor = "Yellow";
    private double positionHorizontal;
    private double positionVertical;
    private double primarySize = 15.0;
    private double secondarySize = 15.0;
    private double headingAngle = 0.0;

    public int getIdentifier() { return this.identifier; }
    public void setIdentifier(int identifier) { this.identifier = identifier; }

    public String getGeometryType() { return this.geometryType; }
    public void setGeometryType(String geometryType) { this.geometryType = geometryType; }

    public String getDisplayColor() { return this.displayColor; }
    public void setDisplayColor(String displayColor) { this.displayColor = displayColor; }

    public double getPositionHorizontal() { return this.positionHorizontal; }
    public void setPositionHorizontal(double positionHorizontal) { this.positionHorizontal = positionHorizontal; }

    public double getPositionVertical() { return this.positionVertical; }
    public void setPositionVertical(double positionVertical) { this.positionVertical = positionVertical; }

    public double getPrimarySize() { return this.primarySize; }
    public void setPrimarySize(double primarySize) { this.primarySize = primarySize; }

    public double getSecondarySize() { return this.secondarySize; }
    public void setSecondarySize(double secondarySize) { this.secondarySize = secondarySize; }

    public double getHeadingAngle() { return this.headingAngle; }
    public void setHeadingAngle(double headingAngle) { this.headingAngle = headingAngle; }
}
