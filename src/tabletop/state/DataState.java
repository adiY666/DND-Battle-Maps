package tabletop.state;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tabletop.model.DrawingModel;
import tabletop.model.TemplateModel;
import tabletop.model.TokenModel;
import tabletop.model.PinModel;
import tabletop.model.InitiativeModel; // Added missing import

/**
 * Represents the core data model for the tabletop canvas.
 *
 * @author Adi
 */
public class DataState {

    private double zoomLevel = 1.0;
    private double panHorizontal = 0.0;
    private double panVertical = 0.0;
    private String backgroundFilepath;
    private int nextTokenIdentifier = 1;
    private boolean snapToGrid = true;

    private final Map<Integer, TokenModel> activeTokens = new HashMap<>();
    private final List<Integer> tokenOrdering = new ArrayList<>();
    private final List<DrawingModel> canvasDrawings = new ArrayList<>();
    private final List<TemplateModel> activeTemplates = new ArrayList<>();
    private final List<PinModel> mapPins = new ArrayList<>();

    // Restored Lists with correct types
    private final List<String> userNotes = new ArrayList<>();
    private final List<InitiativeModel> initiativeList = new ArrayList<>(); // Fixed generic type

    public DataState() {
        super();
    }

    public double getZoomLevel() { return this.zoomLevel; }
    public void setZoomLevel(double zoomLevel) { this.zoomLevel = zoomLevel; }

    public double getPanHorizontal() { return this.panHorizontal; }
    public void setPanHorizontal(double panHorizontal) { this.panHorizontal = panHorizontal; }

    public double getPanVertical() { return this.panVertical; }
    public void setPanVertical(double panVertical) { this.panVertical = panVertical; }

    public String getBackgroundFilepath() { return this.backgroundFilepath; }
    public void setBackgroundFilepath(String backgroundFilepath) { this.backgroundFilepath = backgroundFilepath; }

    public int getNextTokenIdentifier() { return this.nextTokenIdentifier; }
    public void setNextTokenIdentifier(int nextTokenIdentifier) { this.nextTokenIdentifier = nextTokenIdentifier; }

    public boolean isSnapToGrid() { return this.snapToGrid; }
    public void setSnapToGrid(boolean snapToGrid) { this.snapToGrid = snapToGrid; }

    public Map<Integer, TokenModel> getActiveTokens() { return this.activeTokens; }
    public List<Integer> getTokenOrdering() { return this.tokenOrdering; }
    public List<DrawingModel> getCanvasDrawings() { return this.canvasDrawings; }
    public List<TemplateModel> getActiveTemplates() { return this.activeTemplates; }
    public List<PinModel> getMapPins() { return this.mapPins; }

    // Restored Getters
    public List<String> getUserNotes() { return this.userNotes; }
    public List<InitiativeModel> getInitiativeList() { return this.initiativeList; } // Fixed return type

    public double calculateCellDimension() {
        return 50.0 * this.zoomLevel;
    }

    public Coordinate convertScreenToLogical(double screenX, double screenY) {
        double cellDimension = this.calculateCellDimension();
        double logicalX = (screenX - this.panHorizontal) / cellDimension;
        double logicalY = (screenY - this.panVertical) / cellDimension;
        return new Coordinate(logicalX, logicalY);
    }

    public Coordinate convertLogicalToScreen(double logicalX, double logicalY) {
        double cellDimension = this.calculateCellDimension();
        double screenX = (logicalX * cellDimension) + this.panHorizontal;
        double screenY = (logicalY * cellDimension) + this.panVertical;
        return new Coordinate(screenX, screenY);
    }

}
