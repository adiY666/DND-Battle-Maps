import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 * Represents the main input event listener instance.
 * <p></p>
 * Handles raw mouse gestures for this InteractionController.
 *
 * @author Adi
 */
class InteractionController extends MouseAdapter {

    private final ApplicationCore applicationCore;
    private final MathUtility mathUtility;

    private Integer draggingTokenIdentifier;
    private Integer draggingTemplateIdentifier;
    private Integer draggingPinIndex;
    private boolean isPanning;
    private boolean isDrawing;
    private int panStartHorizontal;
    private int panStartVertical;

    public InteractionController(ApplicationCore applicationCore) {
        super();
        this.applicationCore = applicationCore;
        this.mathUtility = new MathUtility();
        this.draggingTokenIdentifier = null;
        this.draggingTemplateIdentifier = null;
        this.draggingPinIndex = null;
        this.isPanning = false;
        this.isDrawing = false;
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        if (SwingUtilities.isLeftMouseButton(mouseEvent)) this.handleLeftPress(mouseEvent);
        if (SwingUtilities.isMiddleMouseButton(mouseEvent)) this.handleMiddlePress(mouseEvent);
        if (SwingUtilities.isRightMouseButton(mouseEvent)) this.handleRightPress(mouseEvent);
    }

    @Override
    public void mouseDragged(MouseEvent mouseEvent) {
        if (SwingUtilities.isLeftMouseButton(mouseEvent)) this.handleLeftDrag(mouseEvent);
        if (SwingUtilities.isMiddleMouseButton(mouseEvent)) this.handleMiddleDrag(mouseEvent);
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
        if (SwingUtilities.isLeftMouseButton(mouseEvent)) this.handleLeftRelease(mouseEvent);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent mouseWheelEvent) {
        double zoomFactor = mouseWheelEvent.getPreciseWheelRotation() < 0 ? 1.1 : 0.9;
        DataState dataState = this.applicationCore.getDataState();
        double logicalHorizontal = (mouseWheelEvent.getX() - dataState.getPanHorizontal()) / dataState.getZoomLevel();
        double logicalVertical = (mouseWheelEvent.getY() - dataState.getPanVertical()) / dataState.getZoomLevel();

        double newZoom = Math.max(0.2, Math.min(dataState.getZoomLevel() * zoomFactor, 5.0));
        dataState.setZoomLevel(newZoom);
        dataState.setPanHorizontal(mouseWheelEvent.getX() - (logicalHorizontal * newZoom));
        dataState.setPanVertical(mouseWheelEvent.getY() - (logicalVertical * newZoom));

        this.applicationCore.refreshDisplay();
    }

    private void handleLeftPress(MouseEvent mouseEvent) {
        DataState dataState = this.applicationCore.getDataState();
        ToolState toolState = this.applicationCore.getToolState();
        Coordinate logicalPosition = dataState.convertScreenToLogical(mouseEvent.getX(), mouseEvent.getY());

        Integer pinHit = this.findPinAt(logicalPosition);
        if (pinHit != null) {
            toolState.setSelectedPinIndex(pinHit);
            this.draggingPinIndex = pinHit;
            this.applicationCore.refreshDisplay();
            return;
        }

        if (toolState.getCurrentTool() == ToolType.DRAWING) {
            if (toolState.getDrawingSubtool() == DrawingSubtool.PEN) {
                this.isDrawing = true;
                DrawingModel drawingModel = new DrawingModel();
                drawingModel.getDrawingPoints().add(logicalPosition);
                drawingModel.setDrawingColor("Black");
                drawingModel.setStrokeWidth(5);
                drawingModel.setStrokeOpacity(1.0);
                dataState.getCanvasDrawings().add(drawingModel);
            }
            return;
        }

        Integer tokenHit = this.findTokenAt(logicalPosition);
        if (tokenHit != null) {
            toolState.setSelectedTokenIdentifier(tokenHit);
            this.draggingTokenIdentifier = tokenHit;
            this.applicationCore.refreshDisplay();
            return;
        }

        toolState.setSelectedTokenIdentifier(null);
        this.isPanning = true;
        this.panStartHorizontal = mouseEvent.getX();
        this.panStartVertical = mouseEvent.getY();
        this.applicationCore.refreshDisplay();
    }

    private void handleLeftDrag(MouseEvent mouseEvent) {
        DataState dataState = this.applicationCore.getDataState();
        if (this.draggingPinIndex != null) {
            Coordinate logicalPosition = dataState.convertScreenToLogical(mouseEvent.getX(), mouseEvent.getY());
            dataState.getMapPins().get(this.draggingPinIndex).setPositionHorizontal(logicalPosition.getCoordinateHorizontal());
            dataState.getMapPins().get(this.draggingPinIndex).setPositionVertical(logicalPosition.getCoordinateVertical());
            this.applicationCore.refreshDisplay();
            return;
        }

        if (this.isDrawing && !dataState.getCanvasDrawings().isEmpty()) {
            Coordinate logicalPosition = dataState.convertScreenToLogical(mouseEvent.getX(), mouseEvent.getY());
            dataState.getCanvasDrawings().get(dataState.getCanvasDrawings().size() - 1).getDrawingPoints().add(logicalPosition);
            this.applicationCore.refreshDisplay();
            return;
        }

        if (this.draggingTokenIdentifier != null) {
            TokenModel tokenModel = dataState.getActiveTokens().get(this.draggingTokenIdentifier);
            if (tokenModel != null) {
                double horizontalDelta = (mouseEvent.getX() - this.panStartHorizontal) / dataState.calculateCellDimension();
                double verticalDelta = (mouseEvent.getY() - this.panStartVertical) / dataState.calculateCellDimension();
                tokenModel.setPositionHorizontal(tokenModel.getPositionHorizontal() + horizontalDelta);
                tokenModel.setPositionVertical(tokenModel.getPositionVertical() + verticalDelta);
                this.panStartHorizontal = mouseEvent.getX();
                this.panStartVertical = mouseEvent.getY();
                this.applicationCore.refreshDisplay();
            }
        } else if (this.isPanning) {
            this.executePan(mouseEvent);
        }
    }

    private void handleLeftRelease(MouseEvent mouseEvent) {
        this.isDrawing = false;
        this.draggingPinIndex = null;
        this.draggingTemplateIdentifier = null;
        this.isPanning = false;

        DataState dataState = this.applicationCore.getDataState();
        if (this.draggingTokenIdentifier != null) {
            TokenModel tokenModel = dataState.getActiveTokens().get(this.draggingTokenIdentifier);
            Coordinate logicalPosition = dataState.convertScreenToLogical(mouseEvent.getX(), mouseEvent.getY());
            if (dataState.isGridSnapping()) {
                tokenModel.setPositionHorizontal(Math.floor(logicalPosition.getCoordinateHorizontal() - tokenModel.getGridScale() / 2.0 + 0.5));
                tokenModel.setPositionVertical(Math.floor(logicalPosition.getCoordinateVertical() - tokenModel.getGridScale() / 2.0 + 0.5));
            } else {
                tokenModel.setPositionHorizontal(logicalPosition.getCoordinateHorizontal() - tokenModel.getGridScale() / 2.0);
                tokenModel.setPositionVertical(logicalPosition.getCoordinateVertical() - tokenModel.getGridScale() / 2.0);
            }
            this.draggingTokenIdentifier = null;
            this.applicationCore.refreshDisplay();
        }
    }

    private void handleMiddlePress(MouseEvent mouseEvent) {
        this.isPanning = true;
        this.panStartHorizontal = mouseEvent.getX();
        this.panStartVertical = mouseEvent.getY();
    }

    private void handleMiddleDrag(MouseEvent mouseEvent) {
        if (this.isPanning) this.executePan(mouseEvent);
    }

    private void handleRightPress(MouseEvent mouseEvent) {
        DataState dataState = this.applicationCore.getDataState();
        ToolState toolState = this.applicationCore.getToolState();
        Coordinate logicalPosition = dataState.convertScreenToLogical(mouseEvent.getX(), mouseEvent.getY());
        Integer tokenHit = this.findTokenAt(logicalPosition);

        if (tokenHit != null) {
            toolState.setSelectedTokenIdentifier(tokenHit);
            this.applicationCore.refreshDisplay();
            JPopupMenu contextMenu = new JPopupMenu();
            // In a full implementation, add menu items for tokens here.
            contextMenu.show(this.applicationCore.getCanvasPanel(), mouseEvent.getX(), mouseEvent.getY());
        }
    }

    private void executePan(MouseEvent mouseEvent) {
        DataState dataState = this.applicationCore.getDataState();
        dataState.setPanHorizontal(dataState.getPanHorizontal() + (mouseEvent.getX() - this.panStartHorizontal));
        dataState.setPanVertical(dataState.getPanVertical() + (mouseEvent.getY() - this.panStartVertical));
        this.panStartHorizontal = mouseEvent.getX();
        this.panStartVertical = mouseEvent.getY();
        this.applicationCore.refreshDisplay();
    }

    private Integer findTokenAt(Coordinate logicalPosition) {
        DataState dataState = this.applicationCore.getDataState();
        for (int index = dataState.getTokenOrdering().size() - 1; index >= 0; index--) {
            int tokenIdentifier = dataState.getTokenOrdering().get(index);
            TokenModel tokenModel = dataState.getActiveTokens().get(tokenIdentifier);
            if (tokenModel != null && logicalPosition.getCoordinateHorizontal() >= tokenModel.getPositionHorizontal() && logicalPosition.getCoordinateHorizontal() < tokenModel.getPositionHorizontal() + tokenModel.getGridScale() && logicalPosition.getCoordinateVertical() >= tokenModel.getPositionVertical() && logicalPosition.getCoordinateVertical() < tokenModel.getPositionVertical() + tokenModel.getGridScale()) {
                return tokenIdentifier;
            }
        }
        return null;
    }

    private Integer findPinAt(Coordinate logicalPosition) {
        DataState dataState = this.applicationCore.getDataState();
        for (int index = dataState.getMapPins().size() - 1; index >= 0; index--) {
            PinModel pinModel = dataState.getMapPins().get(index);
            if (this.mathUtility.calculateDistance(pinModel.getPositionHorizontal(), pinModel.getPositionVertical(), logicalPosition.getCoordinateHorizontal(), logicalPosition.getCoordinateVertical()) <= 0.5)
                return index;
        }
        return null;
    }

}
