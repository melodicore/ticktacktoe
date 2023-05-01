package me.datafox.ticktacktoe.frontend.drawable;

import com.badlogic.gdx.graphics.Color;
import space.earlygrey.shapedrawer.ShapeDrawer;

/**
 * @author datafox
 */
public class SelectionDrawable extends GameDrawable {
    @Override
    public void drawShapes(ShapeDrawer shapeDrawer, float x, float y, float width, float height) {
        shapeDrawer.filledRectangle(x, y, width, height, new Color(0.5f, 0.5f, 1f, 0.5f));
    }
}
