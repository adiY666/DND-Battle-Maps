package tabletop.model;

import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a miniature token on the map.
 * Implements Serializable for the save/load system.
 *
 * @author Adi
 */
public class TokenModel implements Serializable {

    // Required for proper Java Serialization
    private static final long serialVersionUID = 1L;

    private int identifier;
    private String displayName;
    private double positionHorizontal;
    private double positionVertical;
    private String imageFilepath;

    // Marked as transient so the save system ignores it (since Image classes cannot be serialized)
    private transient BufferedImage originalImage;

    private int gridScale;
    private int movementSpeed;
    private boolean showMovementRange;

    // The new property to control Player Screen visibility
    private boolean visibleToPlayers;

    private List<EffectModel> activeEffects;

    public TokenModel(int identifier, String displayName, double positionHorizontal, double positionVertical, String imageFilepath) {
        this.identifier = identifier;
        this.displayName = displayName;
        this.positionHorizontal = positionHorizontal;
        this.positionVertical = positionVertical;
        this.imageFilepath = imageFilepath;

        this.gridScale = 1;         // Default to a 1x1 square
        this.movementSpeed = 6;     // Default to 30ft (6 squares)
        this.showMovementRange = false;
        this.visibleToPlayers = true; // Tokens default to being visible

        this.activeEffects = new ArrayList<>();
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

    public String getImageFilepath() {
        return this.imageFilepath;
    }

    public void setImageFilepath(String imageFilepath) {
        this.imageFilepath = imageFilepath;
    }

    public BufferedImage getOriginalImage() {
        return this.originalImage;
    }

    public void setOriginalImage(BufferedImage originalImage) {
        this.originalImage = originalImage;
    }

    public int getGridScale() {
        return this.gridScale;
    }

    public void setGridScale(int gridScale) {
        this.gridScale = gridScale;
    }

    public int getMovementSpeed() {
        return this.movementSpeed;
    }

    public void setMovementSpeed(int movementSpeed) {
        this.movementSpeed = movementSpeed;
    }

    public boolean isShowMovementRange() {
        return this.showMovementRange;
    }

    public void setShowMovementRange(boolean showMovementRange) {
        this.showMovementRange = showMovementRange;
    }

    public boolean isVisibleToPlayers() {
        return this.visibleToPlayers;
    }

    public void setVisibleToPlayers(boolean visibleToPlayers) {
        this.visibleToPlayers = visibleToPlayers;
    }

    public List<EffectModel> getActiveEffects() {
        return this.activeEffects;
    }

    public void setActiveEffects(List<EffectModel> activeEffects) {
        this.activeEffects = activeEffects;
    }
}
