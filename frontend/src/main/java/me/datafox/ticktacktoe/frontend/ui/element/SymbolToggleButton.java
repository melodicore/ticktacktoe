package me.datafox.ticktacktoe.frontend.ui.element;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import me.datafox.ticktacktoe.api.Symbols;
import me.datafox.ticktacktoe.frontend.Game;
import me.datafox.ticktacktoe.frontend.ui.ColorBuilder;
import me.datafox.ticktacktoe.frontend.utils.Defaults;
import me.datafox.ticktacktoe.frontend.utils.DrawingUtils;

import java.util.function.Supplier;

/**
 * @author datafox
 */
public class SymbolToggleButton extends Button {
    private final String symbol;
    private final Supplier<Color> color;
    public SymbolToggleButton(String symbol) {
        super(new SymbolButtonStyle());
        this.symbol = symbol;
        color = ColorBuilder.def().highlight().build();
    }

    @Override
    public float getMinWidth() {
        return Defaults.BUTTON_WIDTH;
    }

    @Override
    public float getMinHeight() {
        return Defaults.BUTTON_WIDTH;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        Vector2 center = new Vector2(this.getX(), this.getY());
        Vector2 offset = new Vector2(this.getWidth() / 2, this.getHeight() / 2);
        center.add(offset);
        if(isChecked()) {
            if(symbol.equals(Symbols.X))
                DrawingUtils.drawX(Game.ui().getDrawer(), center, Math.min(offset.x, offset.y) - Defaults.WORLD_BASE_WIDTH * 2, 0, color.get(), false);
            else if(symbol.equals(Symbols.O))
                DrawingUtils.drawO(Game.ui().getDrawer(), center, Math.min(offset.x, offset.y) - Defaults.WORLD_BASE_WIDTH * 2, color.get());
        }
    }

    private static class SymbolButtonStyle extends ButtonStyle {
        public SymbolButtonStyle() {
            Drawable upFrame = Defaults.defaultFilledFrame();
            Drawable downFrame = Defaults.darkerFilledFrame();
            Drawable overFrame = Defaults.lighterFilledFrame();
            Drawable disabledFrame = Defaults.desaturatedFilledFrame();
            up = upFrame;
            down = downFrame;
            over = overFrame;
            checked = upFrame;
            disabled = disabledFrame;
        }
    }
}