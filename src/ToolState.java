/**
 * Represents the active editing state instance.
 * <p></p>
 * Tracks the current active tool for this ToolState.
 *
 * @author Adi
 */
class ToolState {

    private ToolType currentTool;
    private DrawingSubtool drawingSubtool;
    private Integer selectedTokenIdentifier;
    private Integer selectedTemplateIdentifier;
    private Integer hoveredTemplateIdentifier;
    private Integer selectedPinIndex;
    private Integer selectedDrawingIndex;

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

}
