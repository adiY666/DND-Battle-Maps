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
import tabletop.model.TemplateModel;

/**
 * Handles all mouse and scroll wheel interactions on the canvas.
 *
 * @author Adi
 */
public class InteractionController extends MouseAdapter {

    private final ApplicationCore applicationCore;
    private boolean isPanning = false;
    private boolean isDrawing = false;
    private boolean isErasing = false;
    private double panStartHorizontal;
    private double panStartVertical;
    private Integer draggingTokenIdentifier = null;
    private Integer draggingTemplateIdentifier = null;
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
                double targetX = dataState.isSnapToGrid() ? Math.floor(logicalPos.getCoordinateHorizontal()) : logicalPos.getCoordinateHorizontal() - (token.getGridScale() / 2.0);
                double targetY = dataState.isSnapToGrid() ? Math.floor(logicalPos.getCoordinateVertical()) : logicalPos.getCoordinateVertical() - (token.getGridScale() / 2.0);

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
            if(toolState.getDrawingSubtool() == null || toolState.getDrawingSubtool() == DrawingSubtool.PEN) {
                this.isDrawing = true;
                DrawingModel drawingModel = new DrawingModel();
                drawingModel.getDrawingPoints().add(logicalPosition);
                drawingModel.setDrawingColor(toolState.getCurrentDrawingColor());
                drawingModel.setStrokeWidth(toolState.getCurrentDrawingStrokeWidth());
                drawingModel.setStrokeOpacity(toolState.getCurrentDrawingOpacity());
                dataState.getCanvasDrawings().add(drawingModel);
            } else if(toolState.getDrawingSubtool() == DrawingSubtool.ERASER) {
                this.isErasing = true;
                this.eraseDrawingsAt(logicalPosition);
            }
            return;
        }

        if(toolState.getCurrentTool() == ToolType.TEMPLATE) {
            Integer hitTemplate = this.findTemplateAt(logicalPosition);
            if(hitTemplate != null) {
                toolState.setSelectedTemplateIdentifier(hitTemplate);
                this.draggingTemplateIdentifier = hitTemplate;
                this.panStartHorizontal = mouseEvent.getX();
                this.panStartVertical = mouseEvent.getY();
            } else {
                TemplateModel newTemplate = new TemplateModel();
                newTemplate.setIdentifier((int)(System.currentTimeMillis() % 100000));
                newTemplate.setPositionHorizontal(logicalPosition.getCoordinateHorizontal());
                newTemplate.setPositionVertical(logicalPosition.getCoordinateVertical());
                newTemplate.setGeometryType(toolState.getDefaultTemplateType());
                newTemplate.setDisplayColor(toolState.getDefaultTemplateColor());
                newTemplate.setPrimarySize(toolState.getDefaultTemplatePrimarySize());
                newTemplate.setSecondarySize(toolState.getDefaultTemplateSecondarySize());
                newTemplate.setHeadingAngle(toolState.getDefaultTemplateAngle());

                dataState.getActiveTemplates().add(newTemplate);
                toolState.setSelectedTemplateIdentifier(newTemplate.getIdentifier());
                this.draggingTemplateIdentifier = newTemplate.getIdentifier();
                this.panStartHorizontal = mouseEvent.getX();
                this.panStartVertical = mouseEvent.getY();
            }

            toolState.setCurrentTool(ToolType.TOKEN);

            if(this.applicationCore.onTemplateSelectionChanged != null) {
                this.applicationCore.onTemplateSelectionChanged.run();
            }
            this.applicationCore.refreshDisplay();
            return;
        }

        Integer hitTemplate = this.findTemplateAt(logicalPosition);
        if(hitTemplate != null) {
            toolState.setSelectedTemplateIdentifier(hitTemplate);
            toolState.setSelectedTokenIdentifier(null);
            this.draggingTemplateIdentifier = hitTemplate;
            this.panStartHorizontal = mouseEvent.getX();
            this.panStartVertical = mouseEvent.getY();

            if(this.applicationCore.onSelectionChanged != null) this.applicationCore.onSelectionChanged.run();
            if(this.applicationCore.onTemplateSelectionChanged != null) this.applicationCore.onTemplateSelectionChanged.run();

            this.applicationCore.refreshDisplay();
            return;
        }

        Integer tokenHit = this.findTokenAt(logicalPosition);
        if(tokenHit != null) {
            toolState.setSelectedTokenIdentifier(tokenHit);
            toolState.setSelectedTemplateIdentifier(null);
            this.draggingTokenIdentifier = tokenHit;
            this.panStartHorizontal = mouseEvent.getX();
            this.panStartVertical = mouseEvent.getY();

            TokenModel token = dataState.getActiveTokens().get(tokenHit);
            if(token != null) {
                token.setShowMovementRange(true);
            }

            if(this.applicationCore.onSelectionChanged != null) this.applicationCore.onSelectionChanged.run();
            if(this.applicationCore.onTemplateSelectionChanged != null) this.applicationCore.onTemplateSelectionChanged.run();

            this.applicationCore.refreshDisplay();
            return;
        }

        Integer selectedId = toolState.getSelectedTokenIdentifier();
        if(selectedId != null) {
            TokenModel token = dataState.getActiveTokens().get(selectedId);
            if(token != null && token.isShowMovementRange()) {
                double targetX = dataState.isSnapToGrid() ? Math.floor(logicalPosition.getCoordinateHorizontal()) : logicalPosition.getCoordinateHorizontal() - (token.getGridScale() / 2.0);
                double targetY = dataState.isSnapToGrid() ? Math.floor(logicalPosition.getCoordinateVertical()) : logicalPosition.getCoordinateVertical() - (token.getGridScale() / 2.0);

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
        toolState.setSelectedTemplateIdentifier(null);

        if(this.applicationCore.onSelectionChanged != null) this.applicationCore.onSelectionChanged.run();
        if(this.applicationCore.onTemplateSelectionChanged != null) this.applicationCore.onTemplateSelectionChanged.run();

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
        } else if(this.draggingTemplateIdentifier != null) {
            for(TemplateModel tm : dataState.getActiveTemplates()) {
                if(tm.getIdentifier() == this.draggingTemplateIdentifier) {
                    double deltaX = (mouseEvent.getX() - this.panStartHorizontal) / dataState.calculateCellDimension();
                    double deltaY = (mouseEvent.getY() - this.panStartVertical) / dataState.calculateCellDimension();
                    tm.setPositionHorizontal(tm.getPositionHorizontal() + deltaX);
                    tm.setPositionVertical(tm.getPositionVertical() + deltaY);
                    this.panStartHorizontal = mouseEvent.getX();
                    this.panStartVertical = mouseEvent.getY();
                    this.applicationCore.refreshDisplay();
                    break;
                }
            }
        } else if(this.isDrawing) {
            Coordinate logicalPosition = dataState.convertScreenToLogical(mouseEvent.getX(), mouseEvent.getY());
            if(!dataState.getCanvasDrawings().isEmpty()) {
                DrawingModel currentDrawing = dataState.getCanvasDrawings().get(dataState.getCanvasDrawings().size() - 1);
                currentDrawing.getDrawingPoints().add(logicalPosition);
                this.applicationCore.refreshDisplay();
            }
        } else if(this.isErasing) {
            Coordinate logicalPosition = dataState.convertScreenToLogical(mouseEvent.getX(), mouseEvent.getY());
            this.eraseDrawingsAt(logicalPosition);
        }
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
        this.isPanning = false;
        this.isDrawing = false;
        this.isErasing = false;

        if(this.draggingTokenIdentifier != null) {
            DataState dataState = this.applicationCore.getDataState();
            TokenModel token = dataState.getActiveTokens().get(this.draggingTokenIdentifier);
            if(token != null && dataState.isSnapToGrid()) {
                token.setPositionHorizontal(Math.floor(token.getPositionHorizontal() + 0.5));
                token.setPositionVertical(Math.floor(token.getPositionVertical() + 0.5));
            }
            this.draggingTokenIdentifier = null;
            this.applicationCore.refreshDisplay();
        }

        if(this.draggingTemplateIdentifier != null) {
            // Disabled grid snapping entirely for rulers/templates so they move freely
            this.draggingTemplateIdentifier = null;
            this.applicationCore.refreshDisplay();
        }

        this.draggingPinIndex = null;
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent mouseWheelEvent) {
        DataState dataState = this.applicationCore.getDataState();

        int mouseX = mouseWheelEvent.getX();
        int mouseY = mouseWheelEvent.getY();
        Coordinate logicalPos = dataState.convertScreenToLogical(mouseX, mouseY);

        double zoomFactor = 1.1;
        double newZoom = dataState.getZoomLevel();
        if(mouseWheelEvent.getWheelRotation() > 0) {
            newZoom /= zoomFactor;
        } else {
            newZoom *= zoomFactor;
        }
        newZoom = Math.max(0.1, Math.min(newZoom, 5.0));
        dataState.setZoomLevel(newZoom);

        double newCellDimension = dataState.calculateCellDimension();
        double newPanX = mouseX - (logicalPos.getCoordinateHorizontal() * newCellDimension);
        double newPanY = mouseY - (logicalPos.getCoordinateVertical() * newCellDimension);

        dataState.setPanHorizontal(newPanX);
        dataState.setPanVertical(newPanY);

        this.applicationCore.refreshDisplay();
    }

    private void eraseDrawingsAt(Coordinate logicalPosition) {
        DataState dataState = this.applicationCore.getDataState();
        double eraseRadius = 0.5;
        boolean needsRefresh = false;

        java.util.Iterator<DrawingModel> iterator = dataState.getCanvasDrawings().iterator();
        while(iterator.hasNext()) {
            DrawingModel drawing = iterator.next();
            for(Coordinate pt : drawing.getDrawingPoints()) {
                double dist = Math.hypot(pt.getCoordinateHorizontal() - logicalPosition.getCoordinateHorizontal(), pt.getCoordinateVertical() - logicalPosition.getCoordinateVertical());
                if(dist <= eraseRadius) {
                    iterator.remove();
                    needsRefresh = true;
                    break;
                }
            }
        }

        if(needsRefresh) {
            this.applicationCore.refreshDisplay();
        }
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

    private Integer findTemplateAt(Coordinate logicalPosition) {
        DataState dataState = this.applicationCore.getDataState();
        for(int i = dataState.getActiveTemplates().size() - 1; i >= 0; i--) {
            TemplateModel template = dataState.getActiveTemplates().get(i);
            double dist = Math.hypot(template.getPositionHorizontal() - logicalPosition.getCoordinateHorizontal(), template.getPositionVertical() - logicalPosition.getCoordinateVertical());
            if(dist <= 1.0) {
                return template.getIdentifier();
            }
        }
        return null;
    }

    private Integer findPinAt(Coordinate logicalPosition) {
        return null;
    }
}
