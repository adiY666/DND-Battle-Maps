package tabletop.controller;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import tabletop.main.ApplicationCore;
import tabletop.main.ToolType;
import tabletop.main.DrawingSubtool;
import tabletop.state.DataState;
import tabletop.state.ToolState;
import tabletop.state.Coordinate;
import tabletop.model.TokenModel;
import tabletop.model.DrawingModel;

/**
 * Handles all mouse and scroll wheel interactions on the canvas.
 *
 * @author Adi
 */
public class InteractionController extends MouseAdapter {

    private final ApplicationCore applicationCore;
    private boolean isPanning = false;
    private boolean isDrawing = false;
    private double panStartHorizontal;
    private double panStartVertical;
    private Integer draggingTokenIdentifier = null;
    private Integer draggingPinIndex = null;

    public InteractionController(ApplicationCore applicationCore) {
        super();
        this.applicationCore = applicationCore;
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        if(SwingUtilities.isLeftMouseButton(mouseEvent)) {
            this.handleLeftPress(mouseEvent);
        } else if(SwingUtilities.isRightMouseButton(mouseEvent)) {
            this.handleRightPress(mouseEvent);
        }
    }

    @Override
    public void mouseMoved(MouseEvent mouseEvent) {
        DataState dataState = this.applicationCore.getDataState();
        ToolState toolState = this.applicationCore.getToolState();
        Coordinate logicalPos = dataState.convertScreenToLogical(mouseEvent.getX(), mouseEvent.getY());

        Integer tokenId = toolState.getSelectedTokenIdentifier();
        if(tokenId != null) {
            TokenModel token = dataState.getActiveTokens().get(tokenId);
            if(token != null && token.isShowMovementRange()) {
                double targetX = dataState.isGridSnapping() ? Math.floor(logicalPos.getCoordinateHorizontal()) : logicalPos.getCoordinateHorizontal() - (token.getGridScale() / 2.0);
                double targetY = dataState.isGridSnapping() ? Math.floor(logicalPos.getCoordinateVertical()) : logicalPos.getCoordinateVertical() - (token.getGridScale() / 2.0);

                double dist = Math.hypot(targetX - token.getPositionHorizontal(), targetY - token.getPositionVertical());
                if(dist <= token.getMovementSpeed()) {
                    toolState.setHoverLogicalX(targetX);
                    toolState.setHoverLogicalY(targetY);
                    toolState.setHoverValid(true);
                } else {
                    toolState.setHoverValid(false);
                }
                this.applicationCore.refreshDisplay();
                return;
            }
        }

        if(toolState.isHoverValid()) {
            toolState.setHoverValid(false);
            this.applicationCore.refreshDisplay();
        }
    }

    private void handleLeftPress(MouseEvent mouseEvent) {
        DataState dataState = this.applicationCore.getDataState();
        ToolState toolState = this.applicationCore.getToolState();
        Coordinate logicalPosition = dataState.convertScreenToLogical(mouseEvent.getX(), mouseEvent.getY());

        Integer pinHit = this.findPinAt(logicalPosition);
        if(pinHit != null) {
            toolState.setSelectedPinIndex(pinHit);
            this.draggingPinIndex = pinHit;
            this.applicationCore.refreshDisplay();
            return;
        }

        if(toolState.getCurrentTool() == ToolType.DRAWING) {
            if(toolState.getDrawingSubtool() == DrawingSubtool.PEN) {
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
        if(tokenHit != null) {
            toolState.setSelectedTokenIdentifier(tokenHit);
            this.draggingTokenIdentifier = tokenHit;
            this.panStartHorizontal = mouseEvent.getX();
            this.panStartVertical = mouseEvent.getY();

            TokenModel token = dataState.getActiveTokens().get(tokenHit);
            if(token != null) {
                token.setShowMovementRange(true);
            }

            if(this.applicationCore.onSelectionChanged != null) {
                this.applicationCore.onSelectionChanged.run();
            }

            this.applicationCore.refreshDisplay();
            return;
        }

        Integer selectedId = toolState.getSelectedTokenIdentifier();
        if(selectedId != null) {
            TokenModel token = dataState.getActiveTokens().get(selectedId);
            if(token != null && token.isShowMovementRange()) {
                double targetX = dataState.isGridSnapping() ? Math.floor(logicalPosition.getCoordinateHorizontal()) : logicalPosition.getCoordinateHorizontal() - (token.getGridScale() / 2.0);
                double targetY = dataState.isGridSnapping() ? Math.floor(logicalPosition.getCoordinateVertical()) : logicalPosition.getCoordinateVertical() - (token.getGridScale() / 2.0);

                double dist = Math.hypot(targetX - token.getPositionHorizontal(), targetY - token.getPositionVertical());
                if(dist <= token.getMovementSpeed()) {
                    token.setPositionHorizontal(targetX);
                    token.setPositionVertical(targetY);
                    this.applicationCore.refreshDisplay();
                    return;
                }
            }

            if(token != null) {
                token.setShowMovementRange(false);
            }
        }

        toolState.setSelectedTokenIdentifier(null);

        if(this.applicationCore.onSelectionChanged != null) {
            this.applicationCore.onSelectionChanged.run();
        }

        this.isPanning = true;
        this.panStartHorizontal = mouseEvent.getX();
        this.panStartVertical = mouseEvent.getY();
        this.applicationCore.refreshDisplay();
    }

    private void handleRightPress(MouseEvent mouseEvent) {
        DataState dataState = this.applicationCore.getDataState();
        ToolState toolState = this.applicationCore.getToolState();
        Coordinate logicalPosition = dataState.convertScreenToLogical(mouseEvent.getX(), mouseEvent.getY());
        Integer tokenHit = this.findTokenAt(logicalPosition);

        if(tokenHit != null) {
            toolState.setSelectedTokenIdentifier(tokenHit);

            if(this.applicationCore.onSelectionChanged != null) {
                this.applicationCore.onSelectionChanged.run();
            }

            this.applicationCore.refreshDisplay();
            JPopupMenu contextMenu = new JPopupMenu();
            contextMenu.show(this.applicationCore.getCanvasPanel(), mouseEvent.getX(), mouseEvent.getY());
        }
    }

    @Override
    public void mouseDragged(MouseEvent mouseEvent) {
        DataState dataState = this.applicationCore.getDataState();

        if(this.isPanning) {
            double deltaX = mouseEvent.getX() - this.panStartHorizontal;
            double deltaY = mouseEvent.getY() - this.panStartVertical;
            dataState.setPanHorizontal(dataState.getPanHorizontal() + deltaX);
            dataState.setPanVertical(dataState.getPanVertical() + deltaY);
            this.panStartHorizontal = mouseEvent.getX();
            this.panStartVertical = mouseEvent.getY();
            this.applicationCore.refreshDisplay();
        } else if(this.draggingTokenIdentifier != null) {
            TokenModel token = dataState.getActiveTokens().get(this.draggingTokenIdentifier);
            if(token != null) {
                double deltaX = (mouseEvent.getX() - this.panStartHorizontal) / dataState.calculateCellDimension();
                double deltaY = (mouseEvent.getY() - this.panStartVertical) / dataState.calculateCellDimension();
                token.setPositionHorizontal(token.getPositionHorizontal() + deltaX);
                token.setPositionVertical(token.getPositionVertical() + deltaY);
                this.panStartHorizontal = mouseEvent.getX();
                this.panStartVertical = mouseEvent.getY();
                this.applicationCore.refreshDisplay();
            }
        } else if(this.isDrawing) {
            Coordinate logicalPosition = dataState.convertScreenToLogical(mouseEvent.getX(), mouseEvent.getY());
            if(!dataState.getCanvasDrawings().isEmpty()) {
                DrawingModel currentDrawing = dataState.getCanvasDrawings().get(dataState.getCanvasDrawings().size() - 1);
                currentDrawing.getDrawingPoints().add(logicalPosition);
                this.applicationCore.refreshDisplay();
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
        this.isPanning = false;
        this.isDrawing = false;

        if(this.draggingTokenIdentifier != null) {
            DataState dataState = this.applicationCore.getDataState();
            TokenModel token = dataState.getActiveTokens().get(this.draggingTokenIdentifier);
            if(token != null && dataState.isGridSnapping()) {
                token.setPositionHorizontal(Math.floor(token.getPositionHorizontal() + 0.5));
                token.setPositionVertical(Math.floor(token.getPositionVertical() + 0.5));
            }
            this.draggingTokenIdentifier = null;
            this.applicationCore.refreshDisplay();
        }

        this.draggingPinIndex = null;
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent mouseWheelEvent) {
        DataState dataState = this.applicationCore.getDataState();
        double zoomFactor = 1.1;
        if(mouseWheelEvent.getWheelRotation() > 0) {
            dataState.setZoomLevel(dataState.getZoomLevel() / zoomFactor);
        } else {
            dataState.setZoomLevel(dataState.getZoomLevel() * zoomFactor);
        }
        this.applicationCore.refreshDisplay();
    }

    private Integer findTokenAt(Coordinate logicalPosition) {
        DataState dataState = this.applicationCore.getDataState();
        for(int i = dataState.getTokenOrdering().size() - 1; i >= 0; i--) {
            Integer tokenId = dataState.getTokenOrdering().get(i);
            TokenModel token = dataState.getActiveTokens().get(tokenId);
            if(token != null) {
                double size = token.getGridScale();
                if(logicalPosition.getCoordinateHorizontal() >= token.getPositionHorizontal() && logicalPosition.getCoordinateHorizontal() <= token.getPositionHorizontal() + size && logicalPosition.getCoordinateVertical() >= token.getPositionVertical() && logicalPosition.getCoordinateVertical() <= token.getPositionVertical() + size) {
                    return tokenId;
                }
            }
        }
        return null;
    }

    private Integer findPinAt(Coordinate logicalPosition) {
        // Placeholder for pin hit logic
        return null;
    }
}
