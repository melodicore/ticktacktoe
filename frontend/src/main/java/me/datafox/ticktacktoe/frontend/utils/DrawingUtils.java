package me.datafox.ticktacktoe.frontend.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import space.earlygrey.shapedrawer.ShapeDrawer;

/**
 * @author datafox
 */
public class DrawingUtils {
    public static float getLineWidth(float size) {
        return size / 4;
    }

    public static void drawSymbol(ShapeDrawer shapeDrawer, String symbol, Vector2 center, float radius, float angle, Color color, boolean adjust) {
        switch(symbol) {
            case "X" -> {
                drawX(shapeDrawer, center, radius, angle, color, adjust);
            }
            case "O" -> {
                drawO(shapeDrawer, center, radius, color);
            }
        }
    }

    public static void drawX(ShapeDrawer shapeDrawer, Vector2 center, float radius, float angle, Color color, boolean adjust) {
        if(adjust) radius = radius / Defaults.SQRT2;
        float size = radius * 2;
        float lineWidth = getLineWidth(size);
        float halfWidth = lineWidth / 2;
        float offset = radius - halfWidth;
        float coffset = halfWidth / Defaults.SQRT2;
        Vector2 v1 = new Vector2(center.x - offset, center.y + offset);
        Vector2 v2 = new Vector2(center.x - offset, center.y - offset);
        Vector2 v3 = new Vector2(center.x + offset, center.y - offset);
        Vector2 v4 = new Vector2(center.x + offset, center.y + offset);
        Vector2 cv1 = new Vector2(center.x - coffset, center.y + coffset);
        Vector2 cv2 = new Vector2(center.x - coffset, center.y - coffset);
        Vector2 cv3 = new Vector2(center.x + coffset, center.y - coffset);
        Vector2 cv4 = new Vector2(center.x + coffset, center.y + coffset);
        Vector2 ev1 = new Vector2(center.x - offset - coffset, center.y + offset - coffset);
        Vector2 ev5 = new Vector2(center.x - offset + coffset, center.y + offset + coffset);
        Vector2 ev2 = new Vector2(center.x - offset - coffset, center.y - offset + coffset);
        Vector2 ev6 = new Vector2(center.x - offset + coffset, center.y - offset - coffset);
        Vector2 ev3 = new Vector2(center.x + offset + coffset, center.y - offset + coffset);
        Vector2 ev7 = new Vector2(center.x + offset - coffset, center.y - offset - coffset);
        Vector2 ev4 = new Vector2(center.x + offset + coffset, center.y + offset - coffset);
        Vector2 ev8 = new Vector2(center.x + offset - coffset, center.y + offset + coffset);
        Vector2 ecv1 = new Vector2(center.x - halfWidth * Defaults.SQRT2, center.y);
        Vector2 ecv2 = new Vector2(center.x, center.y - halfWidth * Defaults.SQRT2);
        Vector2 ecv3 = new Vector2(center.x + halfWidth * Defaults.SQRT2, center.y);
        Vector2 ecv4 = new Vector2(center.x, center.y + halfWidth * Defaults.SQRT2);
        angle = angle % (Defaults.PI * 2);
        if(angle != 0) {
            v1.rotateAroundRad(center, angle);
            v2.rotateAroundRad(center, angle);
            v3.rotateAroundRad(center, angle);
            v4.rotateAroundRad(center, angle);
            cv1.rotateAroundRad(center, angle);
            cv2.rotateAroundRad(center, angle);
            cv3.rotateAroundRad(center, angle);
            cv4.rotateAroundRad(center, angle);
            ev1.rotateAroundRad(center, angle);
            ev2.rotateAroundRad(center, angle);
            ev3.rotateAroundRad(center, angle);
            ev4.rotateAroundRad(center, angle);
            ev5.rotateAroundRad(center, angle);
            ev6.rotateAroundRad(center, angle);
            ev7.rotateAroundRad(center, angle);
            ev8.rotateAroundRad(center, angle);
            ecv1.rotateAroundRad(center, angle);
            ecv2.rotateAroundRad(center, angle);
            ecv3.rotateAroundRad(center, angle);
            ecv4.rotateAroundRad(center, angle);
        }
        Color darker = color.cpy().mul(0.75f, 0.75f, 0.75f, 1);
        shapeDrawer.setColor(color);
        shapeDrawer.setDefaultLineWidth(lineWidth);
        //Draw lines
        shapeDrawer.line(v1, cv1, color, darker);
        shapeDrawer.line(cv3, v3, darker, color);
        shapeDrawer.line(v2, cv2);
        shapeDrawer.line(cv2, cv4);
        shapeDrawer.line(cv4, v4);
        //Draw round ends
        shapeDrawer.sector(v1.x, v1.y, halfWidth, Defaults.PI * 0.25f + angle, Defaults.PI);
        shapeDrawer.sector(v2.x, v2.y, halfWidth, Defaults.PI * 0.75f + angle, Defaults.PI);
        shapeDrawer.sector(v3.x, v3.y, halfWidth, Defaults.PI * 1.25f + angle, Defaults.PI);
        shapeDrawer.sector(v4.x, v4.y, halfWidth, Defaults.PI * 1.75f + angle, Defaults.PI);
        //Draw edges
        shapeDrawer.setColor(Color.BLACK);
        shapeDrawer.setDefaultLineWidth(1.5f);
        shapeDrawer.arc(v1.x, v1.y, halfWidth, Defaults.PI * 0.25f + angle, Defaults.PI);
        shapeDrawer.line(ev1, ecv1);
        shapeDrawer.line(ecv2, ev7);

        shapeDrawer.arc(v3.x, v3.y, halfWidth, Defaults.PI * 1.25f + angle, Defaults.PI);
        shapeDrawer.line(ev3, ecv3);
        shapeDrawer.line(ecv4, ev5);

        shapeDrawer.arc(v2.x, v2.y, halfWidth, Defaults.PI * 0.75f + angle, Defaults.PI);
        shapeDrawer.line(ev2, ev8);

        shapeDrawer.arc(v4.x, v4.y, halfWidth, Defaults.PI * 1.75f + angle, Defaults.PI);
        shapeDrawer.line(ev4, ev6);

    }

    public static void drawO(ShapeDrawer shapeDrawer, Vector2 center, float radius, Color color) {
        float size = radius * 2;
        float lineWidth = getLineWidth(size);
        float halfWidth = lineWidth / 2;
        radius -= halfWidth;
        shapeDrawer.setColor(color);
        shapeDrawer.setDefaultLineWidth(lineWidth);
        shapeDrawer.circle(center.x, center.y, radius);
        //Draw edges
        shapeDrawer.setColor(Color.BLACK);
        shapeDrawer.setDefaultLineWidth(1.5f);
        shapeDrawer.circle(center.x, center.y, radius - halfWidth);
        shapeDrawer.circle(center.x, center.y, radius + halfWidth);
    }

    public static void drawFrame(ShapeDrawer shapeDrawer, Vector2 botLeft, Vector2 topRight,
                                 Color color1, Color color2, Color color3, Color color4) {
        Vector2 v1 = new Vector2(topRight.x - Defaults.WORLD_HALF_WIDTH, topRight.y - Defaults.WORLD_HALF_WIDTH);
        Vector2 v2 = new Vector2(botLeft.x + Defaults.WORLD_HALF_WIDTH, topRight.y - Defaults.WORLD_HALF_WIDTH);
        Vector2 v3 = new Vector2(botLeft.x + Defaults.WORLD_HALF_WIDTH, botLeft.y + Defaults.WORLD_HALF_WIDTH);
        Vector2 v4 = new Vector2(topRight.x - Defaults.WORLD_HALF_WIDTH, botLeft.y + Defaults.WORLD_HALF_WIDTH);
        shapeDrawer.setColor(color1);
        shapeDrawer.setDefaultLineWidth(Defaults.WORLD_BASE_WIDTH);
        //Draw lines
        shapeDrawer.line(v1, v2, color1, color2);
        shapeDrawer.line(v2, v3, color2, color3);
        shapeDrawer.line(v3, v4, color3, color4);
        shapeDrawer.line(v4, v1, color4, color1);
        //Draw round corners
        shapeDrawer.sector(v1.x, v1.y, Defaults.WORLD_HALF_WIDTH, 0, Defaults.PI * 0.5f);
        shapeDrawer.sector(v2.x, v2.y, Defaults.WORLD_HALF_WIDTH, Defaults.PI * 0.5f, Defaults.PI * 0.5f, color2, color2);
        shapeDrawer.sector(v3.x, v3.y, Defaults.WORLD_HALF_WIDTH, Defaults.PI, Defaults.PI * 0.5f, color3, color3);
        shapeDrawer.sector(v4.x, v4.y, Defaults.WORLD_HALF_WIDTH, Defaults.PI * 1.5f, Defaults.PI * 0.5f, color4, color4);
    }

    public static void drawFilledFrame(ShapeDrawer shapeDrawer, Vector2 botLeft, Vector2 topRight,
                                       Color color1, Color color2, Color color3, Color color4, Color fill) {
        drawFrame(shapeDrawer, botLeft, topRight, color1, color2, color3, color4);
        topRight.sub(Defaults.WORLD_BASE_WIDTH, Defaults.WORLD_BASE_WIDTH);
        botLeft.add(Defaults.WORLD_BASE_WIDTH, Defaults.WORLD_BASE_WIDTH);
        shapeDrawer.filledRectangle(topRight.x, topRight.y, botLeft.x - topRight.x, botLeft.y - topRight.y, fill);
    }

    public static void drawSidebarFrame(ShapeDrawer shapeDrawer, Vector2 botLeft, Vector2 topRight, Color color1, Color color2, Color fill) {
        shapeDrawer.setColor(color1);
        shapeDrawer.setDefaultLineWidth(Defaults.WORLD_BASE_WIDTH);
        shapeDrawer.line(botLeft.x + Defaults.WORLD_HALF_WIDTH, topRight.y, botLeft.x + Defaults.WORLD_HALF_WIDTH, botLeft.y, color1, color2);
        botLeft.add(Defaults.WORLD_BASE_WIDTH, 0);
        shapeDrawer.filledRectangle(botLeft.x, topRight.y, topRight.x - botLeft.x + 10, botLeft.y - topRight.y, fill);
    }
}
