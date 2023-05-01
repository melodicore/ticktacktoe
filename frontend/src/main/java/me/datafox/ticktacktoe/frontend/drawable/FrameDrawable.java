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
public class FrameDrawable extends GameDrawable {
    protected final Supplier<Color> color1;
    protected final Supplier<Color> color2;
    protected final Supplier<Color> color3;
    protected final Supplier<Color> color4;

    public FrameDrawable(Supplier<Color> color) {
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
        color3 = ColorBuilder.of(color)
                .add(NoiseBuilder.get()
                        .lerp(-0.1f, 0.1f).build())
                .hue(NoiseBuilder.get()
                        .multiply(10).build()).build();
        color4 = ColorBuilder.of(color)
                .add(NoiseBuilder.get()
                        .lerp(-0.1f, 0.1f).build())
                .hue(NoiseBuilder.get()
                        .multiply(10).build()).build();
    }

    @Override
    public void drawShapes(ShapeDrawer shapeDrawer, float x, float y, float width, float height) {
        DrawingUtils.drawFrame(shapeDrawer, new Vector2(x, y), new Vector2(x + width, y + height),
                color1.get(), color2.get(), color3.get(), color4.get());
    }
}
