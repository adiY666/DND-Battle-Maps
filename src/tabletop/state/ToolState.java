package tabletop.state;

import tabletop.main.DrawingSubtool;
import tabletop.main.ToolType;

/**
 * Represents the active editing state instance.
 * <p></p>
 * Tracks the current active tool for this tabletop.state.ToolState.
 *
 * @author Adi
 */
public class ToolState {

    private ToolType currentTool;
    private DrawingSubtool drawingSubtool;
    private Integer selectedTokenIdentifier;
    private Integer selectedTemplateIdentifier;
    private Integer hoveredTemplateIdentifier;
    private Integer selectedPinIndex;
    private Integer selectedDrawingIndex;
    private Double hoverLogicalX;
    private Double hoverLogicalY;
    private boolean isHoverValid;

    public ToolState() {
        this.currentTool = ToolType.TOKEN;
        this.drawingSubtool = DrawingSubtool.PEN;
        this.selectedTokenIdentifier = null;
        this.selectedTemplateIdentifier = null;
        this.hoveredTemplateIdentifier = null;
        this.selectedPinIndex = null;
        this.selectedDrawingIndex = null;
    }

    public ToolType getCurrentTool() {
        return this.currentTool;
    }

    public void setCurrentTool(ToolType currentTool) {
        this.currentTool = currentTool;
    }

    public DrawingSubtool getDrawingSubtool() {
        return this.drawingSubtool;
    }

    public void setDrawingSubtool(DrawingSubtool drawingSubtool) {
        this.drawingSubtool = drawingSubtool;
    }

    public Integer getSelectedTokenIdentifier() {
        return this.selectedTokenIdentifier;
    }

    public void setSelectedTokenIdentifier(Integer selectedTokenIdentifier) {
        this.selectedTokenIdentifier = selectedTokenIdentifier;
    }

    public Integer getSelectedTemplateIdentifier() {
        return this.selectedTemplateIdentifier;
    }

    public void setSelectedTemplateIdentifier(Integer selectedTemplateIdentifier) {
        this.selectedTemplateIdentifier = selectedTemplateIdentifier;
    }

    public Integer getHoveredTemplateIdentifier() {
        return this.hoveredTemplateIdentifier;
    }

    public void setHoveredTemplateIdentifier(Integer hoveredTemplateIdentifier) {
        this.hoveredTemplateIdentifier = hoveredTemplateIdentifier;
    }

    public Integer getSelectedPinIndex() {
        return this.selectedPinIndex;
    }

    public void setSelectedPinIndex(Integer selectedPinIndex) {
        this.selectedPinIndex = selectedPinIndex;
    }

    public Integer getSelectedDrawingIndex() {
        return this.selectedDrawingIndex;
    }

    public void setSelectedDrawingIndex(Integer selectedDrawingIndex) {
        this.selectedDrawingIndex = selectedDrawingIndex;
    }

    public Double getHoverLogicalX() {
        return this.hoverLogicalX;
    }

    public void setHoverLogicalX(Double hoverLogicalX) {
        this.hoverLogicalX = hoverLogicalX;
    }

    public Double getHoverLogicalY() {
        return this.hoverLogicalY;
    }

    public void setHoverLogicalY(Double hoverLogicalY) {
        this.hoverLogicalY = hoverLogicalY;
    }

    public boolean isHoverValid() {
        return this.isHoverValid;
    }

    public void setHoverValid(boolean hoverValid) {
        this.isHoverValid = hoverValid;
    }

}
