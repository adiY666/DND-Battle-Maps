package tabletop.ui.core;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import javax.imageio.ImageIO;

import tabletop.main.ApplicationCore;
import tabletop.state.Coordinate;
import tabletop.state.DataState;
import tabletop.state.ToolState;
import tabletop.model.DrawingModel;
import tabletop.model.EffectModel;
import tabletop.model.TemplateModel;
import tabletop.model.TokenModel;
import tabletop.model.PinModel;
import tabletop.util.ColorUtility;
import tabletop.ui.theme.ColorPalette;
import tabletop.ui.theme.RenderConstants;

/**
 * Handles all graphical rendering for the tabletop canvas.
 *
 * @author Adi
 */
public class RenderEngine {

    private final ApplicationCore applicationCore;
    private final ColorUtility colorUtility;

    public RenderEngine(ApplicationCore applicationCore) {
        super();
        this.applicationCore = applicationCore;
        this.colorUtility = new ColorUtility();
    }

    public void renderAll(Graphics renderGraphics, int panelWidth, int panelHeight, boolean isPlayerScreen) {
        Graphics2D vectorGraphics = (Graphics2D) renderGraphics;
        vectorGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        vectorGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        DataState dataState = this.applicationCore.getDataState();
        ToolState toolState = this.applicationCore.getToolState();
        double cellDimension = dataState.calculateCellDimension();

        // 1. Draw Background Image
        if(dataState.getBackgroundFilepath() != null && !dataState.getBackgroundFilepath().isEmpty()) {
            try {
                BufferedImage bgImage = ImageIO.read(new File(dataState.getBackgroundFilepath()));
                if(bgImage != null) {
                    double drawWidth = bgImage.getWidth() * dataState.getZoomLevel();
                    double drawHeight = bgImage.getHeight() * dataState.getZoomLevel();
                    vectorGraphics.drawImage(bgImage, (int) dataState.getPanHorizontal(), (int) dataState.getPanVertical(), (int) drawWidth, (int) drawHeight, null);
                }
            } catch(Exception e) {
                // Ignore background errors
            }
        }

        // 2. Draw Grid
        vectorGraphics.setColor(RenderConstants.COLOR_GRID_LINES);
        vectorGraphics.setStroke(new BasicStroke(RenderConstants.STROKE_THIN));
        for(int i = 0; i < RenderConstants.MAX_GRID_LINES; i++) {
            int linePos = (int) (dataState.getPanHorizontal() + (i * cellDimension));
            if(linePos > panelWidth) break;
            if(linePos > 0) vectorGraphics.drawLine(linePos, 0, linePos, panelHeight);
        }
        for(int i = 0; i < RenderConstants.MAX_GRID_LINES; i++) {
            int linePos = (int) (dataState.getPanVertical() + (i * cellDimension));
            if(linePos > panelHeight) break;
            if(linePos > 0) vectorGraphics.drawLine(0, linePos, panelWidth, linePos);
        }

        // 3. Draw Drawings
        this.drawCanvasDrawings(vectorGraphics, dataState);

        // 4. Draw Rulers/Templates
        this.drawTemplates(vectorGraphics, dataState, toolState, cellDimension);

        // 5. Draw Tokens
        for(Integer tokenId : dataState.getTokenOrdering()) {
            TokenModel token = dataState.getActiveTokens().get(tokenId);
            if(token == null) continue;

            Coordinate screenPos = dataState.convertLogicalToScreen(token.getPositionHorizontal(), token.getPositionVertical());
            double tokenPixelSize = token.getGridScale() * cellDimension;

            // Draw Movement Range Aura (if active)
            if(token.isShowMovementRange()) {
                double rangePixels = (token.getMovementSpeed() + (token.getGridScale() / 2.0)) * cellDimension;
                double centerX = screenPos.getCoordinateHorizontal() + (tokenPixelSize / 2.0);
                double centerY = screenPos.getCoordinateVertical() + (tokenPixelSize / 2.0);

                vectorGraphics.setColor(ColorPalette.RANGE_FILL);
                vectorGraphics.fill(new Ellipse2D.Double(centerX - rangePixels, centerY - rangePixels, rangePixels * 2, rangePixels * 2));

                vectorGraphics.setColor(ColorPalette.RANGE_BORDER);
                vectorGraphics.setStroke(new BasicStroke(RenderConstants.STROKE_MEDIUM, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, RenderConstants.DASH_PATTERN, 0));
                vectorGraphics.draw(new Ellipse2D.Double(centerX - rangePixels, centerY - rangePixels, rangePixels * 2, rangePixels * 2));
                vectorGraphics.setStroke(new BasicStroke(RenderConstants.STROKE_THIN));
            }

            // Draw Token Image
            if(token.getOriginalImage() != null) {
                vectorGraphics.drawImage(token.getOriginalImage(), (int) screenPos.getCoordinateHorizontal(), (int) screenPos.getCoordinateVertical(), (int) tokenPixelSize, (int) tokenPixelSize, null);
            } else {
                vectorGraphics.setColor(RenderConstants.COLOR_TOKEN_DEFAULT);
                vectorGraphics.fillOval((int) screenPos.getCoordinateHorizontal(), (int) screenPos.getCoordinateVertical(), (int) tokenPixelSize, (int) tokenPixelSize);
            }

            // Draw Selected Highlight
            if(java.util.Objects.equals(toolState.getSelectedTokenIdentifier(), token.getIdentifier())) {
                vectorGraphics.setColor(RenderConstants.COLOR_HIGHLIGHT);
                vectorGraphics.setStroke(new BasicStroke(RenderConstants.STROKE_THICK));
                vectorGraphics.drawRect((int) screenPos.getCoordinateHorizontal(), (int) screenPos.getCoordinateVertical(), (int) tokenPixelSize, (int) tokenPixelSize);
            }

            // Draw Token Name
            vectorGraphics.setFont(new Font("SansSerif", Font.BOLD, 12));
            FontMetrics metrics = vectorGraphics.getFontMetrics();
            int textWidth = metrics.stringWidth(token.getDisplayName());
            int textX = (int) screenPos.getCoordinateHorizontal() + (int) (tokenPixelSize / 2) - (textWidth / 2);
            int textY = (int) screenPos.getCoordinateVertical() - RenderConstants.TEXT_OFFSET_Y;

            vectorGraphics.setColor(ColorPalette.HOVER_BORDER);
            vectorGraphics.fillRect(textX - (RenderConstants.TEXT_PADDING_X / 2), textY - metrics.getAscent() - (RenderConstants.TEXT_PADDING_Y / 2), textWidth + RenderConstants.TEXT_PADDING_X, metrics.getHeight() + RenderConstants.TEXT_PADDING_Y);
            vectorGraphics.setColor(ColorPalette.TEXT_LIGHT);
            vectorGraphics.drawString(token.getDisplayName(), textX, textY);

            // Draw Status Effects
            int effectYOffset = (int) screenPos.getCoordinateVertical() + (int) tokenPixelSize + RenderConstants.EFFECT_START_Y;
            for(EffectModel effect : token.getActiveEffects()) {
                String effText = effect.getEffectName() + " (" + effect.getRemainingTurns() + ")";
                int effWidth = metrics.stringWidth(effText);
                int effX = (int) screenPos.getCoordinateHorizontal() + (int) (tokenPixelSize / 2) - (effWidth / 2);

                Color effColor = this.colorUtility.retrieveColor(effect.getEffectColor());
                vectorGraphics.setColor(new Color(effColor.getRed(), effColor.getGreen(), effColor.getBlue(), RenderConstants.TEMPLATE_OUTLINE_ALPHA));
                vectorGraphics.fillRect(effX - (RenderConstants.TEXT_PADDING_X / 2), effectYOffset - metrics.getAscent() - (RenderConstants.TEXT_PADDING_Y / 2), effWidth + RenderConstants.TEXT_PADDING_X, metrics.getHeight() + RenderConstants.TEXT_PADDING_Y);

                vectorGraphics.setColor(ColorPalette.TEXT_LIGHT);
                vectorGraphics.drawString(effText, effX, effectYOffset);
                effectYOffset += RenderConstants.EFFECT_LINE_HEIGHT;
            }
        }

        // 6. Draw Map Pins
        this.drawPins(vectorGraphics, dataState, toolState, cellDimension, isPlayerScreen);

        // 7. Draw the Pointing Arrow (Top Layer)
        this.drawPointer(vectorGraphics, dataState, toolState);
    }

    private void drawCanvasDrawings(Graphics2D vectorGraphics, DataState dataState) {
        for(DrawingModel drawing : dataState.getCanvasDrawings()) {
            List<Coordinate> points = drawing.getDrawingPoints();
            if(points.isEmpty()) continue;

            Color baseColor = this.colorUtility.retrieveColor(drawing.getDrawingColor());
            Color strokeColor = new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), (int) (drawing.getStrokeOpacity() * 255));

            vectorGraphics.setColor(strokeColor);
            vectorGraphics.setStroke(new BasicStroke((float) drawing.getStrokeWidth(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            Path2D.Double path = new Path2D.Double();
            Coordinate firstPoint = dataState.convertLogicalToScreen(points.get(0).getCoordinateHorizontal(), points.get(0).getCoordinateVertical());
            path.moveTo(firstPoint.getCoordinateHorizontal(), firstPoint.getCoordinateVertical());

            for(int i = 1; i < points.size(); i++) {
                Coordinate pt = dataState.convertLogicalToScreen(points.get(i).getCoordinateHorizontal(), points.get(i).getCoordinateVertical());
                path.lineTo(pt.getCoordinateHorizontal(), pt.getCoordinateVertical());
            }

            vectorGraphics.draw(path);
        }
    }

    private void drawTemplates(Graphics2D vectorGraphics, DataState dataState, ToolState toolState, double cellDimension) {
        for(TemplateModel templateModel : dataState.getActiveTemplates()) {
            Coordinate screenPosition = dataState.convertLogicalToScreen(templateModel.getPositionHorizontal(), templateModel.getPositionVertical());
            Color baseColor = this.colorUtility.retrieveColor(templateModel.getDisplayColor());
            Color fillColor = new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), RenderConstants.TEMPLATE_FILL_ALPHA);
            Color outlineColor = new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), RenderConstants.TEMPLATE_OUTLINE_ALPHA);

            if(java.util.Objects.equals(toolState.getSelectedTemplateIdentifier(), templateModel.getIdentifier())) {
                outlineColor = RenderConstants.COLOR_HIGHLIGHT;
            }

            if("circle".equals(templateModel.getGeometryType())) {
                double radiusPixels = (templateModel.getPrimarySize() / RenderConstants.GRID_FEET_PER_SQUARE) * cellDimension;
                vectorGraphics.setColor(fillColor);
                vectorGraphics.fill(new Ellipse2D.Double(screenPosition.getCoordinateHorizontal() - radiusPixels, screenPosition.getCoordinateVertical() - radiusPixels, radiusPixels * 2.0, radiusPixels * 2.0));
                vectorGraphics.setColor(outlineColor);
                vectorGraphics.setStroke(new BasicStroke(RenderConstants.STROKE_THICK));
                vectorGraphics.draw(new Ellipse2D.Double(screenPosition.getCoordinateHorizontal() - radiusPixels, screenPosition.getCoordinateVertical() - radiusPixels, radiusPixels * 2.0, radiusPixels * 2.0));

            } else if("square".equals(templateModel.getGeometryType())) {
                double widthPixels = (templateModel.getPrimarySize() / RenderConstants.GRID_FEET_PER_SQUARE) * cellDimension;
                double heightPixels = (templateModel.getSecondarySize() / RenderConstants.GRID_FEET_PER_SQUARE) * cellDimension;

                AffineTransform originalTransform = vectorGraphics.getTransform();
                vectorGraphics.translate(screenPosition.getCoordinateHorizontal(), screenPosition.getCoordinateVertical());
                vectorGraphics.rotate(Math.toRadians(templateModel.getHeadingAngle()));

                Rectangle2D.Double centeredRect = new Rectangle2D.Double(-widthPixels / 2.0, -heightPixels / 2.0, widthPixels, heightPixels);
                vectorGraphics.setColor(fillColor);
                vectorGraphics.fill(centeredRect);
                vectorGraphics.setColor(outlineColor);
                vectorGraphics.setStroke(new BasicStroke(RenderConstants.STROKE_THICK));
                vectorGraphics.draw(centeredRect);

                vectorGraphics.setTransform(originalTransform);

            } else if("cone".equals(templateModel.getGeometryType())) {
                double lengthPixels = (templateModel.getPrimarySize() / RenderConstants.GRID_FEET_PER_SQUARE) * cellDimension;
                Arc2D.Double arc = new Arc2D.Double(screenPosition.getCoordinateHorizontal() - lengthPixels, screenPosition.getCoordinateVertical() - lengthPixels, lengthPixels * 2.0, lengthPixels * 2.0, -(templateModel.getHeadingAngle() - templateModel.getSecondarySize() / 2.0), -templateModel.getSecondarySize(), Arc2D.PIE);
                vectorGraphics.setColor(fillColor);
                vectorGraphics.fill(arc);
                vectorGraphics.setColor(outlineColor);
                vectorGraphics.setStroke(new BasicStroke(RenderConstants.STROKE_THICK));
                vectorGraphics.draw(arc);

            } else if("line".equals(templateModel.getGeometryType())) {
                double lengthPixels = (templateModel.getPrimarySize() / RenderConstants.GRID_FEET_PER_SQUARE) * cellDimension;
                double widthPixels = (templateModel.getSecondarySize() / RenderConstants.GRID_FEET_PER_SQUARE) * cellDimension;

                AffineTransform originalTransform = vectorGraphics.getTransform();
                vectorGraphics.translate(screenPosition.getCoordinateHorizontal(), screenPosition.getCoordinateVertical());
                vectorGraphics.rotate(Math.toRadians(templateModel.getHeadingAngle()));

                Rectangle2D.Double rectangle = new Rectangle2D.Double(0, -widthPixels / 2.0, lengthPixels, widthPixels);
                vectorGraphics.setColor(fillColor);
                vectorGraphics.fill(rectangle);
                vectorGraphics.setColor(outlineColor);
                vectorGraphics.setStroke(new BasicStroke(RenderConstants.STROKE_THICK));
                vectorGraphics.draw(rectangle);

                vectorGraphics.setTransform(originalTransform);
            }

            vectorGraphics.setColor(outlineColor);
            vectorGraphics.fillOval(
                    (int) screenPosition.getCoordinateHorizontal() - RenderConstants.TEMPLATE_ANCHOR_RADIUS,
                    (int) screenPosition.getCoordinateVertical() - RenderConstants.TEMPLATE_ANCHOR_RADIUS,
                    RenderConstants.TEMPLATE_ANCHOR_RADIUS * 2,
                    RenderConstants.TEMPLATE_ANCHOR_RADIUS * 2
            );
        }
    }

    private void drawPins(Graphics2D vectorGraphics, DataState dataState, ToolState toolState, double cellDimension, boolean isPlayerScreen) {
        List<PinModel> pins = dataState.getMapPins();
        for(int i = 0; i < pins.size(); i++) {
            PinModel pin = pins.get(i);

            // --- NEW VISIBILITY LOGIC ---
            // If we are drawing on the Player's screen, ONLY draw it if the global
            // share toggle is ON and this specific pin is marked as shared.
            if (isPlayerScreen && (!toolState.isShowSharedPinsOnPlayerScreen() || !pin.isShared())) {
                continue; // Skip rendering this pin!
            }

            Coordinate screenPos = dataState.convertLogicalToScreen(pin.getPositionHorizontal(), pin.getPositionVertical());
            int pinX = (int) screenPos.getCoordinateHorizontal();
            int pinY = (int) screenPos.getCoordinateVertical();

            Color pinColor = this.colorUtility.retrieveColor(pin.getPinColor());

            vectorGraphics.setColor(pinColor);
            vectorGraphics.fillOval(pinX - RenderConstants.PIN_RADIUS, pinY - RenderConstants.PIN_DIAMETER, RenderConstants.PIN_DIAMETER, RenderConstants.PIN_DIAMETER);
            vectorGraphics.fillPolygon(
                    new int[]{pinX - RenderConstants.PIN_RADIUS, pinX + RenderConstants.PIN_RADIUS, pinX},
                    new int[]{pinY - RenderConstants.PIN_RADIUS, pinY - RenderConstants.PIN_RADIUS, pinY},
                    3);

            vectorGraphics.setColor(Color.BLACK);
            vectorGraphics.setStroke(new BasicStroke(RenderConstants.STROKE_PIN_OUTLINE));
            vectorGraphics.drawOval(pinX - RenderConstants.PIN_RADIUS, pinY - RenderConstants.PIN_DIAMETER, RenderConstants.PIN_DIAMETER, RenderConstants.PIN_DIAMETER);
            vectorGraphics.drawPolygon(
                    new int[]{pinX - RenderConstants.PIN_RADIUS, pinX + RenderConstants.PIN_RADIUS, pinX},
                    new int[]{pinY - RenderConstants.PIN_RADIUS, pinY - RenderConstants.PIN_RADIUS, pinY},
                    3);

            if(java.util.Objects.equals(toolState.getSelectedPinIndex(), i)) {
                vectorGraphics.setColor(RenderConstants.COLOR_HIGHLIGHT);
                vectorGraphics.setStroke(new BasicStroke(RenderConstants.STROKE_THICK));
                vectorGraphics.drawOval(pinX - RenderConstants.PIN_HIGHLIGHT_RADIUS, pinY - (RenderConstants.PIN_DIAMETER + RenderConstants.PIN_HIGHLIGHT_OFFSET), RenderConstants.PIN_HIGHLIGHT_DIAMETER, RenderConstants.PIN_HIGHLIGHT_DIAMETER);

                vectorGraphics.setFont(new Font("SansSerif", Font.BOLD, RenderConstants.PIN_TITLE_FONT_SIZE));
                FontMetrics metrics = vectorGraphics.getFontMetrics();
                int textWidth = metrics.stringWidth(pin.getTitle());
                int textX = pinX - (textWidth / 2);
                int textY = pinY - RenderConstants.PIN_TITLE_OFFSET_Y;

                vectorGraphics.setColor(ColorPalette.HOVER_BORDER);
                vectorGraphics.fillRect(textX - (RenderConstants.TEXT_PADDING_X), textY - metrics.getAscent() - (RenderConstants.TEXT_PADDING_Y), textWidth + (RenderConstants.TEXT_PADDING_X * 2), metrics.getHeight() + (RenderConstants.TEXT_PADDING_Y * 2));
                vectorGraphics.setColor(ColorPalette.TEXT_LIGHT);
                vectorGraphics.drawString(pin.getTitle(), textX, textY);
            }
        }
    }

    private void drawPointer(Graphics2D vectorGraphics, DataState dataState, ToolState toolState) {
        Coordinate start = toolState.getPointerStart();
        Coordinate end = toolState.getPointerEnd();

        if (start != null && end != null) {
            Coordinate screenStart = dataState.convertLogicalToScreen(start.getCoordinateHorizontal(), start.getCoordinateVertical());
            Coordinate screenEnd = dataState.convertLogicalToScreen(end.getCoordinateHorizontal(), end.getCoordinateVertical());

            double dist = Math.hypot(screenStart.getCoordinateHorizontal() - screenEnd.getCoordinateHorizontal(), screenStart.getCoordinateVertical() - screenEnd.getCoordinateVertical());

            if(dist > RenderConstants.POINTER_MIN_DRAG_DIST) {
                vectorGraphics.setColor(RenderConstants.COLOR_POINTER);
                vectorGraphics.setStroke(new BasicStroke(RenderConstants.STROKE_POINTER, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                int sx = (int) screenStart.getCoordinateHorizontal();
                int sy = (int) screenStart.getCoordinateVertical();
                int ex = (int) screenEnd.getCoordinateHorizontal();
                int ey = (int) screenEnd.getCoordinateVertical();

                vectorGraphics.drawLine(sx, sy, ex, ey);

                double angle = Math.atan2(ey - sy, ex - sx);
                int x1 = (int) (ex - RenderConstants.POINTER_ARROW_SIZE * Math.cos(angle - RenderConstants.POINTER_ARROW_ANGLE));
                int y1 = (int) (ey - RenderConstants.POINTER_ARROW_SIZE * Math.sin(angle - RenderConstants.POINTER_ARROW_ANGLE));
                int x2 = (int) (ex - RenderConstants.POINTER_ARROW_SIZE * Math.cos(angle + RenderConstants.POINTER_ARROW_ANGLE));
                int y2 = (int) (ey - RenderConstants.POINTER_ARROW_SIZE * Math.sin(angle + RenderConstants.POINTER_ARROW_ANGLE));

                vectorGraphics.fillPolygon(new int[]{ex, x1, x2}, new int[]{ey, y1, y2}, 3);

                vectorGraphics.fillOval(
                        sx - RenderConstants.POINTER_ANCHOR_RADIUS,
                        sy - RenderConstants.POINTER_ANCHOR_RADIUS,
                        RenderConstants.POINTER_ANCHOR_RADIUS * 2,
                        RenderConstants.POINTER_ANCHOR_RADIUS * 2
                );
            }
        }
    }
}
