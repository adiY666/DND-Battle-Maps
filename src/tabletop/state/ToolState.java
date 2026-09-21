package tabletop.state;

import tabletop.main.DrawingSubtool;
import tabletop.main.ToolType;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents the active editing state instance.
 * Tracks the current active tool for the tabletop.
 *
 * @author Adi
 */
public class ToolState {

    private ToolType currentTool;
    private DrawingSubtool drawingSubtool;

    // --- NEW: A Set to hold multiple selected tokens ---
    private Set<Integer> selectedTokenIdentifiers;

    private Integer selectedTemplateIdentifier;
    private Integer hoveredTemplateIdentifier;
    private Integer selectedPinIndex;
    private Integer selectedDrawingIndex;

    private Double hoverLogicalX;
    private Double hoverLogicalY;
    private boolean isHoverValid;

    private String currentDrawingColor = "Black";
    private int currentDrawingStrokeWidth = 5;
    private double currentDrawingOpacity = 1.0;

    private String defaultTemplateType = "circle";
    private String defaultTemplateColor = "Yellow";
    private double defaultTemplatePrimarySize = 15.0;
    private double defaultTemplateSecondarySize = 15.0;
    private double defaultTemplateAngle = 0.0;

    private boolean playerScreenBlackout = false;
    private Coordinate pointerStart = null;
    private Coordinate pointerEnd = null;
    private boolean showSharedPinsOnPlayerScreen = false;

    public ToolState() {
        super();
        this.currentTool = ToolType.TOKEN;
        this.drawingSubtool = DrawingSubtool.PEN;

        // Initialize the empty selection set
        this.selectedTokenIdentifiers = new HashSet<>();

        this.selectedTemplateIdentifier = null;
        this.hoveredTemplateIdentifier = null;
        this.selectedPinIndex = null;
        this.selectedDrawingIndex = null;
    }

    public ToolType getCurrentTool() { return this.currentTool; }
    public void setCurrentTool(ToolType currentTool) { this.currentTool = currentTool; }

    public DrawingSubtool getDrawingSubtool() { return this.drawingSubtool; }
    public void setDrawingSubtool(DrawingSubtool drawingSubtool) { this.drawingSubtool = drawingSubtool; }

    // --- NEW: Getter for the selection Set ---
    public Set<Integer> getSelectedTokenIdentifiers() { return this.selectedTokenIdentifiers; }

    public Integer getSelectedTemplateIdentifier() { return this.selectedTemplateIdentifier; }
    public void setSelectedTemplateIdentifier(Integer selectedTemplateIdentifier) { this.selectedTemplateIdentifier = selectedTemplateIdentifier; }

    public Integer getHoveredTemplateIdentifier() { return this.hoveredTemplateIdentifier; }
    public void setHoveredTemplateIdentifier(Integer hoveredTemplateIdentifier) { this.hoveredTemplateIdentifier = hoveredTemplateIdentifier; }

    public Integer getSelectedPinIndex() { return this.selectedPinIndex; }
    public void setSelectedPinIndex(Integer selectedPinIndex) { this.selectedPinIndex = selectedPinIndex; }

    public Integer getSelectedDrawingIndex() { return this.selectedDrawingIndex; }
    public void setSelectedDrawingIndex(Integer selectedDrawingIndex) { this.selectedDrawingIndex = selectedDrawingIndex; }

    public Double getHoverLogicalX() { return this.hoverLogicalX; }
    public void setHoverLogicalX(Double hoverLogicalX) { this.hoverLogicalX = hoverLogicalX; }

    public Double getHoverLogicalY() { return this.hoverLogicalY; }
    public void setHoverLogicalY(Double hoverLogicalY) { this.hoverLogicalY = hoverLogicalY; }

    public boolean isHoverValid() { return this.isHoverValid; }
    public void setHoverValid(boolean hoverValid) { this.isHoverValid = hoverValid; }

    public String getCurrentDrawingColor() { return this.currentDrawingColor; }
    public void setCurrentDrawingColor(String currentDrawingColor) { this.currentDrawingColor = currentDrawingColor; }

    public int getCurrentDrawingStrokeWidth() { return this.currentDrawingStrokeWidth; }
    public void setCurrentDrawingStrokeWidth(int currentDrawingStrokeWidth) { this.currentDrawingStrokeWidth = currentDrawingStrokeWidth; }

    public double getCurrentDrawingOpacity() { return this.currentDrawingOpacity; }
    public void setCurrentDrawingOpacity(double currentDrawingOpacity) { this.currentDrawingOpacity = currentDrawingOpacity; }

    public String getDefaultTemplateType() { return this.defaultTemplateType; }
    public void setDefaultTemplateType(String defaultTemplateType) { this.defaultTemplateType = defaultTemplateType; }

    public String getDefaultTemplateColor() { return this.defaultTemplateColor; }
    public void setDefaultTemplateColor(String defaultTemplateColor) { this.defaultTemplateColor = defaultTemplateColor; }

    public double getDefaultTemplatePrimarySize() { return this.defaultTemplatePrimarySize; }
    public void setDefaultTemplatePrimarySize(double defaultTemplatePrimarySize) { this.defaultTemplatePrimarySize = defaultTemplatePrimarySize; }

    public double getDefaultTemplateSecondarySize() { return this.defaultTemplateSecondarySize; }
    public void setDefaultTemplateSecondarySize(double defaultTemplateSecondarySize) { this.defaultTemplateSecondarySize = defaultTemplateSecondarySize; }

    public double getDefaultTemplateAngle() { return this.defaultTemplateAngle; }
    public void setDefaultTemplateAngle(double defaultTemplateAngle) { this.defaultTemplateAngle = defaultTemplateAngle; }

    public boolean isPlayerScreenBlackout() { return this.playerScreenBlackout; }
    public void setPlayerScreenBlackout(boolean playerScreenBlackout) { this.playerScreenBlackout = playerScreenBlackout; }

    public Coordinate getPointerStart() { return this.pointerStart; }
    public void setPointerStart(Coordinate pointerStart) { this.pointerStart = pointerStart; }

    public Coordinate getPointerEnd() { return this.pointerEnd; }
    public void setPointerEnd(Coordinate pointerEnd) { this.pointerEnd = pointerEnd; }

    public boolean isShowSharedPinsOnPlayerScreen() { return this.showSharedPinsOnPlayerScreen; }
    public void setShowSharedPinsOnPlayerScreen(boolean showSharedPinsOnPlayerScreen) { this.showSharedPinsOnPlayerScreen = showSharedPinsOnPlayerScreen; }
}
