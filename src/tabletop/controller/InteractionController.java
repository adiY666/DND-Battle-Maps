package tabletop.controller;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import javax.swing.SwingUtilities;

import tabletop.main.ApplicationCore;
import tabletop.main.ToolType;
import tabletop.model.TokenModel;
import tabletop.state.Coordinate;
import tabletop.state.DataState;
import tabletop.state.ToolState;

/**
 * Handles all mouse interactions (clicks, drags, scrolls) on the map canvas.
 *
 * @author Adi
 */
public class InteractionController extends MouseAdapter {

    private final ApplicationCore applicationCore;

    private int lastMouseX;
    private int lastMouseY;
    private boolean isDraggingToken = false;
    private boolean isPanning = false;

    public InteractionController(ApplicationCore applicationCore) {
        super();
        this.applicationCore = applicationCore;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        this.lastMouseX = e.getX();
        this.lastMouseY = e.getY();

        DataState dataState = this.applicationCore.getDataState();
        ToolState toolState = this.applicationCore.getToolState();
        Coordinate logicalClick = dataState.convertScreenToLogical(e.getX(), e.getY());

        // 1. Handle Map Panning (Middle Click)
        if (SwingUtilities.isMiddleMouseButton(e)) {
            this.isPanning = true;
            return;
        }

        // 2. Handle Pointing Arrow (Right Click)
        if (SwingUtilities.isRightMouseButton(e)) {
            toolState.setPointerStart(logicalClick);
            toolState.setPointerEnd(logicalClick);
            this.applicationCore.refreshDisplay();
            return;
        }

        // 3. Handle Token Tool Interactions (Left Click)
        if (SwingUtilities.isLeftMouseButton(e) && toolState.getCurrentTool() == ToolType.TOKEN) {

            // --- CLICK-TO-MOVE WITHIN RANGE LOGIC ---
            Integer selectedTokenId = toolState.getSelectedTokenIdentifier();
            if (selectedTokenId != null) {
                TokenModel selectedToken = dataState.getActiveTokens().get(selectedTokenId);

                if (selectedToken != null && selectedToken.isShowMovementRange()) {
                    double tokenCenterX = selectedToken.getPositionHorizontal() + (selectedToken.getGridScale() / 2.0);
                    double tokenCenterY = selectedToken.getPositionVertical() + (selectedToken.getGridScale() / 2.0);

                    double distance = Math.hypot(logicalClick.getCoordinateHorizontal() - tokenCenterX, logicalClick.getCoordinateVertical() - tokenCenterY);
                    double allowedRange = selectedToken.getMovementSpeed() + (selectedToken.getGridScale() / 2.0);

                    if (distance <= allowedRange) {
                        if (dataState.isSnapToGrid()) {
                            selectedToken.setPositionHorizontal(Math.floor(logicalClick.getCoordinateHorizontal()));
                            selectedToken.setPositionVertical(Math.floor(logicalClick.getCoordinateVertical()));
                        } else {
                            selectedToken.setPositionHorizontal(logicalClick.getCoordinateHorizontal() - (selectedToken.getGridScale() / 2.0));
                            selectedToken.setPositionVertical(logicalClick.getCoordinateVertical() - (selectedToken.getGridScale() / 2.0));
                        }
                        this.applicationCore.refreshDisplay();
                        return; // Stop processing so we don't select a different token by accident
                    }
                }
            }

            // --- STANDARD: SELECT OR DRAG TOKEN ---
            boolean tokenFound = false;

            // Loop backwards to select the token rendered on top if they are overlapping
            for (int i = dataState.getTokenOrdering().size() - 1; i >= 0; i--) {
                Integer tokenId = dataState.getTokenOrdering().get(i);
                TokenModel token = dataState.getActiveTokens().get(tokenId);

                if (token != null) {
                    double tx = token.getPositionHorizontal();
                    double ty = token.getPositionVertical();
                    double size = token.getGridScale();

                    // Check if the click falls within the token's bounding box
                    if (logicalClick.getCoordinateHorizontal() >= tx && logicalClick.getCoordinateHorizontal() <= tx + size &&
                            logicalClick.getCoordinateVertical() >= ty && logicalClick.getCoordinateVertical() <= ty + size) {

                        // Turn off range for the previously selected token if it is different
                        Integer prevSelectedId = toolState.getSelectedTokenIdentifier();
                        if (prevSelectedId != null && !prevSelectedId.equals(tokenId)) {
                            TokenModel prevToken = dataState.getActiveTokens().get(prevSelectedId);
                            if (prevToken != null) prevToken.setShowMovementRange(false);
                        }

                        // Automatically turn on the range for the newly selected token
                        token.setShowMovementRange(true);

                        toolState.setSelectedTokenIdentifier(tokenId);
                        this.isDraggingToken = true;
                        tokenFound = true;

                        if (this.applicationCore.onSelectionChanged != null) {
                            this.applicationCore.onSelectionChanged.run();
                        }
                        this.applicationCore.refreshDisplay();
                        break;
                    }
                }
            }

            // If we clicked on an empty space, deselect the current token
            if (!tokenFound) {
                // Turn off range before deselecting
                Integer prevSelectedId = toolState.getSelectedTokenIdentifier();
                if (prevSelectedId != null) {
                    TokenModel prevToken = dataState.getActiveTokens().get(prevSelectedId);
                    if (prevToken != null) prevToken.setShowMovementRange(false);
                }

                toolState.setSelectedTokenIdentifier(null);
                if (this.applicationCore.onSelectionChanged != null) {
                    this.applicationCore.onSelectionChanged.run();
                }

                // ---> FIX 1: Allow the user to drag the background to pan the map! <---
                this.isPanning = true;

                this.applicationCore.refreshDisplay();
            }
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        int dx = e.getX() - this.lastMouseX;
        int dy = e.getY() - this.lastMouseY;
        this.lastMouseX = e.getX();
        this.lastMouseY = e.getY();

        DataState dataState = this.applicationCore.getDataState();
        ToolState toolState = this.applicationCore.getToolState();
        Coordinate logicalClick = dataState.convertScreenToLogical(e.getX(), e.getY());

        // 1. Handle Map Panning
        if (this.isPanning) {
            dataState.setPanHorizontal(dataState.getPanHorizontal() + dx);
            dataState.setPanVertical(dataState.getPanVertical() + dy);
            this.applicationCore.refreshDisplay();
            return;
        }

        // 2. Handle Pointing Arrow
        if (SwingUtilities.isRightMouseButton(e)) {
            toolState.setPointerEnd(logicalClick);
            this.applicationCore.refreshDisplay();
            return;
        }

        // 3. Handle Token Dragging
        if (this.isDraggingToken && toolState.getCurrentTool() == ToolType.TOKEN) {
            Integer selectedId = toolState.getSelectedTokenIdentifier();
            if (selectedId != null) {
                TokenModel token = dataState.getActiveTokens().get(selectedId);
                if (token != null) {
                    double cellDim = dataState.calculateCellDimension();
                    double logicalDx = dx / cellDim;
                    double logicalDy = dy / cellDim;

                    token.setPositionHorizontal(token.getPositionHorizontal() + logicalDx);
                    token.setPositionVertical(token.getPositionVertical() + logicalDy);
                    this.applicationCore.refreshDisplay();
                }
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        this.isPanning = false;

        DataState dataState = this.applicationCore.getDataState();
        ToolState toolState = this.applicationCore.getToolState();

        // 1. Clear the Pointing Arrow
        if (SwingUtilities.isRightMouseButton(e)) {
            toolState.setPointerStart(null);
            toolState.setPointerEnd(null);
            this.applicationCore.refreshDisplay();
        }

        // 2. Handle Snap-to-Grid on Token Drop
        if (this.isDraggingToken) {
            this.isDraggingToken = false;

            if (dataState.isSnapToGrid()) {
                Integer selectedId = toolState.getSelectedTokenIdentifier();
                if (selectedId != null) {
                    TokenModel token = dataState.getActiveTokens().get(selectedId);
                    if (token != null) {
                        token.setPositionHorizontal(Math.round(token.getPositionHorizontal()));
                        token.setPositionVertical(Math.round(token.getPositionVertical()));
                        this.applicationCore.refreshDisplay();
                    }
                }
            }
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        DataState dataState = this.applicationCore.getDataState();

        // ---> FIX 2: Switched to standard getWheelRotation() for mouse compatibility <---
        double zoomChange = e.getWheelRotation() * -0.1;
        double newZoom = dataState.getZoomLevel() + zoomChange;

        if (newZoom < 0.1) newZoom = 0.1;
        if (newZoom > 5.0) newZoom = 5.0;

        dataState.setZoomLevel(newZoom);
        this.applicationCore.refreshDisplay();
    }
}
