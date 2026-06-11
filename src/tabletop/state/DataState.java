package tabletop.state;

import tabletop.main.Constants;
import tabletop.model.*;

/**
 * Represents the global data state instance.
 * <p></p>
 * Stores all essential properties for this tabletop.state.DataState.
 *
 * @author Adi
 */
public class DataState implements java.io.Serializable {

    private double zoomLevel;
    private double panHorizontal;
    private double panVertical;
    private boolean gridSnapping;
    private boolean fullScreenPresentation;
    private String backgroundFilepath;

    private final java.util.Map<Integer, TokenModel> activeTokens;
    private final java.util.List<Integer> tokenOrdering;
    private final java.util.List<TemplateModel> activeTemplates;
    private final java.util.List<DrawingModel> canvasDrawings;
    private final java.util.List<java.util.List<DrawingModel>> drawingRedoStack;
    private final java.util.List<PinModel> mapPins;
    private final java.util.List<InitiativeModel> initiativeList;
    private final java.util.List<String> userNotes;

    private int nextTokenIdentifier;
    private int nextTemplateIdentifier;
    private int nextPinIdentifier;

    public DataState() {
        this.zoomLevel = 1.0;
        this.panHorizontal = 0.0;
        this.panVertical = 0.0;
        this.gridSnapping = true;
        this.fullScreenPresentation = false;
        this.backgroundFilepath = null;
        this.activeTokens = new java.util.LinkedHashMap<>();
        this.tokenOrdering = new java.util.ArrayList<>();
        this.activeTemplates = new java.util.ArrayList<>();
        this.canvasDrawings = new java.util.ArrayList<>();
        this.drawingRedoStack = new java.util.ArrayList<>();
        this.mapPins = new java.util.ArrayList<>();
        this.initiativeList = new java.util.ArrayList<>();
        this.userNotes = new java.util.ArrayList<>();
        this.nextTokenIdentifier = 1;
        this.nextTemplateIdentifier = 1;
        this.nextPinIdentifier = 1;
    }

    /**
     * Computes the scaled logical size.
     *
     * @return the absolute cell dimension
     */
    public double calculateCellDimension() {
        return Constants.BASE_CELL_SIZE * this.zoomLevel;
    }

    /**
     * Converts screen coordinates to logical map coordinates.
     *
     * @param screenHorizontal the horizontal screen position
     * @param screenVertical   the vertical screen position
     * @return the logical coordinate
     */
    public Coordinate convertScreenToLogical(int screenHorizontal, int screenVertical) {
        double dimension = this.calculateCellDimension();
        double logicalHorizontal = (screenHorizontal - this.panHorizontal) / dimension;
        double logicalVertical = (screenVertical - this.panVertical) / dimension;
        return new Coordinate(logicalHorizontal, logicalVertical);
    }

    /**
     * Converts logical coordinates to screen map coordinates.
     *
     * @param logicalHorizontal the horizontal logical position
     * @param logicalVertical   the vertical logical position
     * @return the screen coordinate
     */
    public Coordinate convertLogicalToScreen(double logicalHorizontal, double logicalVertical) {
        double dimension = this.calculateCellDimension();
        double screenHorizontal = (logicalHorizontal * dimension) + this.panHorizontal;
        double screenVertical = (logicalVertical * dimension) + this.panVertical;
        return new Coordinate(screenHorizontal, screenVertical);
    }

    public double getZoomLevel() {
        return this.zoomLevel;
    }

    public void setZoomLevel(double zoomLevel) {
        this.zoomLevel = zoomLevel;
    }

    public double getPanHorizontal() {
        return this.panHorizontal;
    }

    public void setPanHorizontal(double panHorizontal) {
        this.panHorizontal = panHorizontal;
    }

    public double getPanVertical() {
        return this.panVertical;
    }

    public void setPanVertical(double panVertical) {
        this.panVertical = panVertical;
    }

    public boolean isGridSnapping() {
        return this.gridSnapping;
    }

    public void setGridSnapping(boolean gridSnapping) {
        this.gridSnapping = gridSnapping;
    }

    public boolean isFullScreenPresentation() {
        return this.fullScreenPresentation;
    }

    public void setFullScreenPresentation(boolean fullScreenPresentation) {
        this.fullScreenPresentation = fullScreenPresentation;
    }

    public String getBackgroundFilepath() {
        return this.backgroundFilepath;
    }

    public void setBackgroundFilepath(String backgroundFilepath) {
        this.backgroundFilepath = backgroundFilepath;
    }

    public java.util.Map<Integer, TokenModel> getActiveTokens() {
        return this.activeTokens;
    }

    public java.util.List<Integer> getTokenOrdering() {
        return this.tokenOrdering;
    }

    public java.util.List<TemplateModel> getActiveTemplates() {
        return this.activeTemplates;
    }

    public java.util.List<DrawingModel> getCanvasDrawings() {
        return this.canvasDrawings;
    }

    public java.util.List<java.util.List<DrawingModel>> getDrawingRedoStack() {
        return this.drawingRedoStack;
    }

    public java.util.List<PinModel> getMapPins() {
        return this.mapPins;
    }

    public java.util.List<InitiativeModel> getInitiativeList() {
        return this.initiativeList;
    }

    public java.util.List<String> getUserNotes() {
        return this.userNotes;
    }

    public int getNextTokenIdentifier() {
        return this.nextTokenIdentifier;
    }

    public void setNextTokenIdentifier(int nextTokenIdentifier) {
        this.nextTokenIdentifier = nextTokenIdentifier;
    }

    public int getNextTemplateIdentifier() {
        return this.nextTemplateIdentifier;
    }

    public void setNextTemplateIdentifier(int nextTemplateIdentifier) {
        this.nextTemplateIdentifier = nextTemplateIdentifier;
    }

    public int getNextPinIdentifier() {
        return this.nextPinIdentifier;
    }

    public void setNextPinIdentifier(int nextPinIdentifier) {
        this.nextPinIdentifier = nextPinIdentifier;
    }

}
