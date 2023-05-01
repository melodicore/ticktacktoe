package me.datafox.ticktacktoe.frontend.drawable;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import me.datafox.ticktacktoe.frontend.utils.Defaults;
import me.datafox.ticktacktoe.frontend.utils.DrawingUtils;
import space.earlygrey.shapedrawer.ShapeDrawer;

import java.util.function.Supplier;

/**
 * @author datafox
 */
public class SymbolDrawable extends SquareDrawable {
    private final String symbol;
    private final Supplier<Color> color;

    public SymbolDrawable(String symbol, Supplier<Color> color) {
        super();
        this.symbol = symbol;
        this.color = color;
    }

    @Override
    public float getMinWidth() {
        return Defaults.WORLD_BASE_WIDTH * 4;
    }

    @Override
    public float getMinHeight() {
        return Defaults.WORLD_BASE_WIDTH * 4;
    }

    @Override
    public void drawShapes(ShapeDrawer shapeDrawer, Vector2 pos, float size) {
        float halfSize = size / 2;
        pos.add(halfSize, halfSize);
        DrawingUtils.drawSymbol(shapeDrawer, symbol, pos, halfSize - Defaults.WORLD_BASE_WIDTH, 0, color.get(), false);
    }
}
