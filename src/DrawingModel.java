/**
 * Represents a freehand drawing instance.
 * <p></p>
 * Stores vector lines and styling for this DrawingModel.
 *
 * @author Adi
 */
class DrawingModel implements java.io.Serializable {

    private java.util.List<Coordinate> drawingPoints;
    private String drawingColor;
    private int strokeWidth;
    private double strokeOpacity;

    public DrawingModel() {
        this.drawingPoints = new java.util.ArrayList<>();
    }

    public java.util.List<Coordinate> getDrawingPoints() {
        return this.drawingPoints;
    }

    public void setDrawingPoints(java.util.List<Coordinate> drawingPoints) {
        this.drawingPoints = drawingPoints;
    }

    public String getDrawingColor() {
        return this.drawingColor;
    }

    public void setDrawingColor(String drawingColor) {
        this.drawingColor = drawingColor;
    }

    public int getStrokeWidth() {
        return this.strokeWidth;
    }

    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public double getStrokeOpacity() {
        return this.strokeOpacity;
    }

    public void setStrokeOpacity(double strokeOpacity) {
        this.strokeOpacity = strokeOpacity;
    }

}
