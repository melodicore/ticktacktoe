package me.datafox.ticktacktoe.frontend.drawable;

import com.badlogic.gdx.math.Vector2;
import space.earlygrey.shapedrawer.ShapeDrawer;

/**
 * @author datafox
 */
public abstract class SquareDrawable extends GameDrawable {
    public SquareDrawable() {
        super();
    }

    @Override
    public void drawShapes(ShapeDrawer shapeDrawer, float x, float y, float width, float height) {
        float min = Math.min(width, height);
        if(width > min) x += (width - min) / 2;
        if(height > min) y += (height - min) / 2;
        drawShapes(shapeDrawer, new Vector2(x, y), min);
    }

    public abstract void drawShapes(ShapeDrawer shapeDrawer, Vector2 pos, float size);
}
