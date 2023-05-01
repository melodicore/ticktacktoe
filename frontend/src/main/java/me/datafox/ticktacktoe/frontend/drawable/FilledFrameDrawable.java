package me.datafox.ticktacktoe.frontend.drawable;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import me.datafox.ticktacktoe.frontend.utils.DrawingUtils;
import space.earlygrey.shapedrawer.ShapeDrawer;

import java.util.function.Supplier;

/**
 * @author datafox
 */
public class FilledFrameDrawable extends FrameDrawable {
    private final Supplier<Color> fill;
    public FilledFrameDrawable(Supplier<Color> color, Supplier<Color> fill) {
        super(color);
        this.fill = fill;
    }

    @Override
    public void drawShapes(ShapeDrawer shapeDrawer, float x, float y, float width, float height) {
        DrawingUtils.drawFilledFrame(shapeDrawer, new Vector2(x, y), new Vector2(x + width, y + height),
                color1.get(), color2.get(), color3.get(), color4.get(), fill.get());
    }
}
