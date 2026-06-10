package tabletop.ui.core;

import tabletop.main.ApplicationCore;
import tabletop.model.*;
import tabletop.state.Coordinate;
import tabletop.state.DataState;
import tabletop.state.ToolState;
import tabletop.ui.theme.ColorPalette;
import tabletop.util.ColorUtility;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.*;
import java.io.File;
import java.io.IOException;

/**
 * Represents the primary rendering instance.
 *
 * @author Adi
 */
class RenderEngine {

    private final ApplicationCore applicationCore;
    private final ColorUtility colorUtility;

    public RenderEngine(ApplicationCore applicationCore) {
        this.applicationCore = applicationCore;
        this.colorUtility = new ColorUtility();
    }

    public void renderAll(Graphics renderGraphics, int componentWidth, int componentHeight) {
        Graphics2D vectorGraphics = (Graphics2D) renderGraphics;
        vectorGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        vectorGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        DataState dataState = this.applicationCore.getDataState();
        ToolState toolState = this.applicationCore.getToolState();
        double cellDimension = dataState.calculateCellDimension();

        this.drawBackground(vectorGraphics, dataState);
        this.drawGrid(vectorGraphics, dataState, cellDimension, componentWidth, componentHeight);

        // This handles the new range and hover functionality
        this.drawTokenRanges(vectorGraphics, dataState, toolState, cellDimension);

        this.drawTemplates(vectorGraphics, dataState, toolState, cellDimension);
        this.drawVectorDrawings(vectorGraphics, dataState);
        this.drawMapPins(vectorGraphics, dataState, toolState);
        this.drawTokens(vectorGraphics, dataState, toolState, cellDimension);
    }

    private void drawBackground(Graphics2D vectorGraphics, DataState dataState) {
        if(dataState.getBackgroundFilepath() == null) return;
        try {
            java.awt.image.BufferedImage backgroundImage = ImageIO.read(new File(dataState.getBackgroundFilepath()));
            int scaledWidth = (int) (backgroundImage.getWidth() * dataState.getZoomLevel());
            int scaledHeight = (int) (backgroundImage.getHeight() * dataState.getZoomLevel());
            vectorGraphics.drawImage(backgroundImage, (int) dataState.getPanHorizontal(), (int) dataState.getPanVertical(), scaledWidth, scaledHeight, null);
        } catch(IOException exception) {
            exception.printStackTrace();
        }
    }

    private void drawGrid(Graphics2D vectorGraphics, DataState dataState, double cellDimension, int componentWidth, int componentHeight) {
        vectorGraphics.setColor(new Color(200, 200, 200, 150));
        Stroke dashedStroke = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{4}, 0);
        vectorGraphics.setStroke(dashedStroke);

        int startColumn = Math.max(0, (int) Math.floor(-dataState.getPanHorizontal() / cellDimension));
        int startRow = Math.max(0, (int) Math.floor(-dataState.getPanVertical() / cellDimension));
        int endColumn = startColumn + (int) Math.ceil(componentWidth / cellDimension) + 1;
        int endRow = startRow + (int) Math.ceil(componentHeight / cellDimension) + 1;

        for(int column = startColumn; column < Math.min(endColumn, 200); column++) {
            int lineHorizontal = (int) (column * cellDimension + dataState.getPanHorizontal());
            vectorGraphics.drawLine(lineHorizontal, Math.max(0, (int) dataState.getPanVertical()), lineHorizontal, componentHeight);
        }

        for(int row = startRow; row < Math.min(endRow, 200); row++) {
            int lineVertical = (int) (row * cellDimension + dataState.getPanVertical());
            vectorGraphics.drawLine(Math.max(0, (int) dataState.getPanHorizontal()), lineVertical, componentWidth, lineVertical);
        }
    }

    private void drawTokenRanges(Graphics2D vectorGraphics, DataState dataState, ToolState toolState, double cellDimension) {
        Integer selectedId = toolState.getSelectedTokenIdentifier();
        if(selectedId != null) {
            TokenModel token = dataState.getActiveTokens().get(selectedId);

            if(token != null && token.isShowMovementRange()) {
                double centerX = token.getPositionHorizontal() + (token.getGridScale() / 2.0);
                double centerY = token.getPositionVertical() + (token.getGridScale() / 2.0);
                Coordinate screenCenter = dataState.convertLogicalToScreen(centerX, centerY);

                int radiusPx = (int) (token.getMovementSpeed() * cellDimension);

                // Range Fill
                vectorGraphics.setColor(ColorPalette.RANGE_FILL);
                vectorGraphics.fillOval((int) screenCenter.getCoordinateHorizontal() - radiusPx, (int) screenCenter.getCoordinateVertical() - radiusPx, radiusPx * 2, radiusPx * 2);

                // Range Border
                vectorGraphics.setColor(ColorPalette.RANGE_BORDER);
                vectorGraphics.setStroke(new BasicStroke(Math.max(1, (int) (2 * dataState.getZoomLevel()))));
                vectorGraphics.drawOval((int) screenCenter.getCoordinateHorizontal() - radiusPx, (int) screenCenter.getCoordinateVertical() - radiusPx, radiusPx * 2, radiusPx * 2);

                // Dynamic Hover Indicator
                if(toolState.isHoverValid() && toolState.getHoverLogicalX() != null && toolState.getHoverLogicalY() != null) {
                    double hoverCenterX = toolState.getHoverLogicalX() + (token.getGridScale() / 2.0);
                    double hoverCenterY = toolState.getHoverLogicalY() + (token.getGridScale() / 2.0);
                    Coordinate screenHover = dataState.convertLogicalToScreen(hoverCenterX, hoverCenterY);

                    int hoverRadiusPx = (int) ((token.getGridScale() * cellDimension) / 2.0);

                    Stroke dashed = new BasicStroke(Math.max(2, (int) (4 * dataState.getZoomLevel())), BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{4, 4}, 0);
                    vectorGraphics.setStroke(dashed);
                    vectorGraphics.setColor(ColorPalette.HOVER_BORDER);
                    vectorGraphics.drawOval((int) screenHover.getCoordinateHorizontal() - hoverRadiusPx, (int) screenHover.getCoordinateVertical() - hoverRadiusPx, hoverRadiusPx * 2, hoverRadiusPx * 2);
                }
            }
        }
    }

    private void drawTemplates(Graphics2D vectorGraphics, DataState dataState, ToolState toolState, double cellDimension) {
        for(TemplateModel templateModel : dataState.getActiveTemplates()) {
            Coordinate screenPosition = dataState.convertLogicalToScreen(templateModel.getPositionHorizontal(), templateModel.getPositionVertical());
            Color baseColor = this.colorUtility.retrieveColor(templateModel.getDisplayColor());
            Color fillColor = new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), 90);

            Color outlineColor = new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), 200);
            if(java.util.Objects.equals(toolState.getSelectedTemplateIdentifier(), templateModel.getIdentifier())) {
                outlineColor = Color.YELLOW; // Highlight if selected
            }

            if("circle".equals(templateModel.getGeometryType())) {
                double radiusPixels = (templateModel.getPrimarySize() / 5.0) * cellDimension;
                vectorGraphics.setColor(fillColor);
                vectorGraphics.fill(new Ellipse2D.Double(screenPosition.getCoordinateHorizontal() - radiusPixels, screenPosition.getCoordinateVertical() - radiusPixels, radiusPixels * 2, radiusPixels * 2));
                vectorGraphics.setColor(outlineColor);
                vectorGraphics.setStroke(new BasicStroke(3));
                vectorGraphics.draw(new Ellipse2D.Double(screenPosition.getCoordinateHorizontal() - radiusPixels, screenPosition.getCoordinateVertical() - radiusPixels, radiusPixels * 2, radiusPixels * 2));

            } else if("square".equals(templateModel.getGeometryType())) {
                double widthPixels = (templateModel.getPrimarySize() / 5.0) * cellDimension;
                double heightPixels = (templateModel.getSecondarySize() / 5.0) * cellDimension;

                AffineTransform originalTransform = vectorGraphics.getTransform();
                vectorGraphics.translate(screenPosition.getCoordinateHorizontal(), screenPosition.getCoordinateVertical());
                vectorGraphics.rotate(Math.toRadians(templateModel.getHeadingAngle()));

                Rectangle2D.Double centeredRect = new Rectangle2D.Double(-widthPixels / 2.0, -heightPixels / 2.0, widthPixels, heightPixels);
                vectorGraphics.setColor(fillColor);
                vectorGraphics.fill(centeredRect);
                vectorGraphics.setColor(outlineColor);
                vectorGraphics.setStroke(new BasicStroke(3));
                vectorGraphics.draw(centeredRect);

                vectorGraphics.setTransform(originalTransform);

            } else if("cone".equals(templateModel.getGeometryType())) {
                double lengthPixels = (templateModel.getPrimarySize() / 5.0) * cellDimension;
                Arc2D.Double arc = new Arc2D.Double(screenPosition.getCoordinateHorizontal() - lengthPixels, screenPosition.getCoordinateVertical() - lengthPixels, lengthPixels * 2, lengthPixels * 2, -(templateModel.getHeadingAngle() - templateModel.getSecondarySize() / 2.0), -templateModel.getSecondarySize(), Arc2D.PIE);
                vectorGraphics.setColor(fillColor);
                vectorGraphics.fill(arc);
                vectorGraphics.setColor(outlineColor);
                vectorGraphics.setStroke(new BasicStroke(3));
                vectorGraphics.draw(arc);

            } else if("line".equals(templateModel.getGeometryType())) {
                double lengthPixels = (templateModel.getPrimarySize() / 5.0) * cellDimension;
                double widthPixels = (templateModel.getSecondarySize() / 5.0) * cellDimension;
                AffineTransform originalTransform = vectorGraphics.getTransform();
                vectorGraphics.translate(screenPosition.getCoordinateHorizontal(), screenPosition.getCoordinateVertical());
                vectorGraphics.rotate(Math.toRadians(templateModel.getHeadingAngle()));
                Rectangle2D.Double rectangle = new Rectangle2D.Double(0, -widthPixels / 2.0, lengthPixels, widthPixels);
                vectorGraphics.setColor(fillColor);
                vectorGraphics.fill(rectangle);
                vectorGraphics.setColor(outlineColor);
                vectorGraphics.setStroke(new BasicStroke(3));
                vectorGraphics.draw(rectangle);
                vectorGraphics.setTransform(originalTransform);
            }

            // Draw a tiny anchor point at the origin so the user knows where to click to select/drag it
            vectorGraphics.setColor(outlineColor);
            vectorGraphics.fillOval((int) screenPosition.getCoordinateHorizontal() - 4, (int) screenPosition.getCoordinateVertical() - 4, 8, 8);
        }
    }

    private void drawVectorDrawings(Graphics2D vectorGraphics, DataState dataState) {
        for(DrawingModel drawingModel : dataState.getCanvasDrawings()) {
            if(drawingModel.getDrawingPoints().size() < 2) continue;

            Color baseColor = this.colorUtility.retrieveColor(drawingModel.getDrawingColor());
            Color renderingColor = new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), (int) (drawingModel.getStrokeOpacity() * 255));
            vectorGraphics.setColor(renderingColor);
            vectorGraphics.setStroke(new BasicStroke(Math.max(1, (int) (drawingModel.getStrokeWidth() * dataState.getZoomLevel())), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            Path2D.Double path = new Path2D.Double();
            Coordinate firstCoordinate = dataState.convertLogicalToScreen(drawingModel.getDrawingPoints().get(0).getCoordinateHorizontal(), drawingModel.getDrawingPoints().get(0).getCoordinateVertical());
            path.moveTo(firstCoordinate.getCoordinateHorizontal(), firstCoordinate.getCoordinateVertical());

            for(int index = 1; index < drawingModel.getDrawingPoints().size(); index++) {
                Coordinate pointCoordinate = dataState.convertLogicalToScreen(drawingModel.getDrawingPoints().get(index).getCoordinateHorizontal(), drawingModel.getDrawingPoints().get(index).getCoordinateVertical());
                path.lineTo(pointCoordinate.getCoordinateHorizontal(), pointCoordinate.getCoordinateVertical());
            }
            vectorGraphics.draw(path);
        }
    }

    private void drawMapPins(Graphics2D vectorGraphics, DataState dataState, ToolState toolState) {
        for(int index = 0; index < dataState.getMapPins().size(); index++) {
            PinModel pinModel = dataState.getMapPins().get(index);
            Coordinate screenPosition = dataState.convertLogicalToScreen(pinModel.getPositionHorizontal(), pinModel.getPositionVertical());

            if(java.util.Objects.equals(toolState.getSelectedPinIndex(), index)) {
                vectorGraphics.setColor(Color.YELLOW);
                vectorGraphics.setStroke(new BasicStroke(3));
                vectorGraphics.drawOval((int) screenPosition.getCoordinateHorizontal() - 12, (int) screenPosition.getCoordinateVertical() - 28, 24, 32);
            }

            vectorGraphics.setColor(this.colorUtility.retrieveColor(pinModel.getDisplayColor()));
            Path2D.Double pinBody = new Path2D.Double();
            pinBody.moveTo(screenPosition.getCoordinateHorizontal(), screenPosition.getCoordinateVertical());
            pinBody.lineTo(screenPosition.getCoordinateHorizontal() - 8, screenPosition.getCoordinateVertical() - 16);
            pinBody.lineTo(screenPosition.getCoordinateHorizontal() + 8, screenPosition.getCoordinateVertical() - 16);
            pinBody.closePath();
            vectorGraphics.fill(pinBody);
            vectorGraphics.fillOval((int) screenPosition.getCoordinateHorizontal() - 8, (int) screenPosition.getCoordinateVertical() - 24, 16, 16);

            vectorGraphics.setColor(Color.BLACK);
            vectorGraphics.setStroke(new BasicStroke(1));
            vectorGraphics.draw(pinBody);
            vectorGraphics.drawOval((int) screenPosition.getCoordinateHorizontal() - 8, (int) screenPosition.getCoordinateVertical() - 24, 16, 16);

            vectorGraphics.setFont(new Font("Arial", Font.BOLD, 12));
            vectorGraphics.drawString(pinModel.getDisplayName(), (float) screenPosition.getCoordinateHorizontal(), (float) screenPosition.getCoordinateVertical() - 30);
        }
    }

    private void drawTokens(Graphics2D vectorGraphics, DataState dataState, ToolState toolState, double cellDimension) {
        for(Integer tokenIdentifier : dataState.getTokenOrdering()) {
            TokenModel tokenModel = dataState.getActiveTokens().get(tokenIdentifier);
            if(tokenModel == null) continue;

            Coordinate screenPosition = dataState.convertLogicalToScreen(tokenModel.getPositionHorizontal(), tokenModel.getPositionVertical());
            double tokenPixelSize = cellDimension * tokenModel.getGridScale();

            if(tokenModel.getOriginalImage() != null) {
                int renderingWidth = (int) (tokenPixelSize - (4 * dataState.getZoomLevel()));

                if(renderingWidth > 5) {
                    double centerHorizontal = screenPosition.getCoordinateHorizontal() + tokenPixelSize / 2.0;
                    double centerVertical = screenPosition.getCoordinateVertical() + tokenPixelSize / 2.0;

                    AffineTransform originalTransform = vectorGraphics.getTransform();
                    vectorGraphics.translate(centerHorizontal, centerVertical);
                    vectorGraphics.rotate(Math.toRadians(tokenModel.getRotationAngle()));
                    vectorGraphics.drawImage(tokenModel.getOriginalImage(), -renderingWidth / 2, -renderingWidth / 2, renderingWidth, renderingWidth, null);
                    vectorGraphics.setTransform(originalTransform);

                    if(java.util.Objects.equals(toolState.getSelectedTokenIdentifier(), tokenIdentifier)) {
                        vectorGraphics.setColor(Color.YELLOW);
                        vectorGraphics.setStroke(new BasicStroke(Math.max(2, (int) (4 * dataState.getZoomLevel()))));
                        vectorGraphics.drawRect((int) screenPosition.getCoordinateHorizontal(), (int) screenPosition.getCoordinateVertical(), (int) tokenPixelSize, (int) tokenPixelSize);
                    }

                    for(int index = 0; index < tokenModel.getActiveEffects().size(); index++) {
                        EffectModel effectModel = tokenModel.getActiveEffects().get(index);
                        double effectRadius = (renderingWidth / 2.0) + (index * 5 * dataState.getZoomLevel()) + (2 * dataState.getZoomLevel());
                        vectorGraphics.setColor(this.colorUtility.retrieveColor(effectModel.getDisplayColor()));
                        vectorGraphics.setStroke(new BasicStroke(Math.max(1, (int) (3 * dataState.getZoomLevel()))));
                        vectorGraphics.drawOval((int) (centerHorizontal - effectRadius), (int) (centerVertical - effectRadius), (int) (effectRadius * 2), (int) (effectRadius * 2));
                    }
                }
            }
        }
    }
}
