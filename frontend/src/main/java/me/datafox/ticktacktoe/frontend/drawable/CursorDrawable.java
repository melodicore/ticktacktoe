package me.datafox.ticktacktoe.frontend.drawable;

import com.badlogic.gdx.graphics.Color;
import me.datafox.ticktacktoe.frontend.Game;
import space.earlygrey.shapedrawer.ShapeDrawer;

/**
 * @author datafox
 */
public class CursorDrawable extends GameDrawable {
    @Override
    public void drawShapes(ShapeDrawer shapeDrawer, float x, float y, float width, float height) {
        shapeDrawer.line(x, y, x, y + height, Color.WHITE, 1 / Game.ui().getScale());
    }
}
