package tabletop.model;

/**
 * Represents a DM-only map pin for notes and room details.
 *
 * @author Adi
 */
public class PinModel {

    private double positionHorizontal;
    private double positionVertical;
    private String title = "New Map Note";
    private String description = "Enter your secret DM notes here...";
    private String pinColor = "Red";

    public PinModel(double positionHorizontal, double positionVertical) {
        super();
        this.positionHorizontal = positionHorizontal;
        this.positionVertical = positionVertical;
    }

    public double getPositionHorizontal() { return this.positionHorizontal; }
    public void setPositionHorizontal(double positionHorizontal) { this.positionHorizontal = positionHorizontal; }

    public double getPositionVertical() { return this.positionVertical; }
    public void setPositionVertical(double positionVertical) { this.positionVertical = positionVertical; }

    public String getTitle() { return this.title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return this.description; }
    public void setDescription(String description) { this.description = description; }

    public String getPinColor() { return this.pinColor; }
    public void setPinColor(String pinColor) { this.pinColor = pinColor; }
}
