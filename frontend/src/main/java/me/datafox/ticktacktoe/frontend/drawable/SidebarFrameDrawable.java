package me.datafox.ticktacktoe.frontend.drawable;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import me.datafox.ticktacktoe.frontend.ui.ColorBuilder;
import me.datafox.ticktacktoe.frontend.ui.NoiseBuilder;
import me.datafox.ticktacktoe.frontend.utils.DrawingUtils;
import space.earlygrey.shapedrawer.ShapeDrawer;

import java.util.function.Supplier;

/**
 * @author datafox
 */
public class SidebarFrameDrawable extends GameDrawable {
    protected final Supplier<Color> color1;
    protected final Supplier<Color> color2;
    private final Supplier<Color> fill;

    public SidebarFrameDrawable(Supplier<Color> color, Supplier<Color> fill) {
        super();
        color1 = ColorBuilder.of(color)
                .add(NoiseBuilder.get()
                        .lerp(-0.1f, 0.1f).build())
                .hue(NoiseBuilder.get()
                        .multiply(10).build()).build();
        color2 = ColorBuilder.of(color)
                .add(NoiseBuilder.get()
                        .lerp(-0.1f, 0.1f).build())
                .hue(NoiseBuilder.get()
                        .multiply(10).build()).build();
        this.fill = fill;
    }

    @Override
    public void drawShapes(ShapeDrawer shapeDrawer, float x, float y, float width, float height) {
        DrawingUtils.drawSidebarFrame(shapeDrawer, new Vector2(x, y), new Vector2(x + width, y + height),
                color1.get(), color2.get(), fill.get());
    }
}
