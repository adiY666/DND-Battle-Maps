package tabletop.model;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a miniature token on the tabletop map.
 *
 * @author Adi
 */
public class TokenModel implements java.io.Serializable {

    private int identifier;
    private String displayName;
    private double positionHorizontal;
    private double positionVertical;
    private double gridScale = 1.0;
    private int movementSpeed = 6;
    private boolean showMovementRange = false;

    private transient BufferedImage originalImage;

    // Fixed: Renamed to match your exact expected getter/setter terminology
    private String imageFilepath;

    private List<EffectModel> activeEffects = new ArrayList<>();

    public TokenModel(int identifier, String displayName, double positionHorizontal, double positionVertical, String imageFilepath) {
        super();
        this.identifier = identifier;
        this.displayName = displayName;
        this.positionHorizontal = positionHorizontal;
        this.positionVertical = positionVertical;
        this.imageFilepath = imageFilepath;
    }

    public int getIdentifier() { return this.identifier; }
    public void setIdentifier(int identifier) { this.identifier = identifier; }

    public String getDisplayName() { return this.displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public double getPositionHorizontal() { return this.positionHorizontal; }
    public void setPositionHorizontal(double positionHorizontal) { this.positionHorizontal = positionHorizontal; }

    public double getPositionVertical() { return this.positionVertical; }
    public void setPositionVertical(double positionVertical) { this.positionVertical = positionVertical; }

    public double getGridScale() { return this.gridScale; }
    public void setGridScale(double gridScale) { this.gridScale = gridScale; }

    public int getMovementSpeed() { return this.movementSpeed; }
    public void setMovementSpeed(int movementSpeed) { this.movementSpeed = movementSpeed; }

    public boolean isShowMovementRange() { return this.showMovementRange; }
    public void setShowMovementRange(boolean showMovementRange) { this.showMovementRange = showMovementRange; }

    public BufferedImage getOriginalImage() { return this.originalImage; }
    public void setOriginalImage(BufferedImage originalImage) { this.originalImage = originalImage; }

    // Fixed: Getters and setters properly use "Filepath"
    public String getImageFilepath() { return this.imageFilepath; }
    public void setImageFilepath(String imageFilepath) { this.imageFilepath = imageFilepath; }

    public List<EffectModel> getActiveEffects() { return this.activeEffects; }
    public void setActiveEffects(List<EffectModel> activeEffects) { this.activeEffects = activeEffects; }
}
