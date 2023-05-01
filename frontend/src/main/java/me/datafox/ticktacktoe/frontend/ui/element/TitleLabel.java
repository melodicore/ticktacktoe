package me.datafox.ticktacktoe.frontend.ui.element;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import me.datafox.ticktacktoe.frontend.Game;
import me.datafox.ticktacktoe.frontend.ui.ColorBuilder;

import java.util.function.Supplier;

/**
 * @author datafox
 */
public class TitleLabel extends Label {
    public TitleLabel(String text) {
        super(text.toUpperCase(), new LabelStyle(Game.ui().getTitleFont(), Color.WHITE));
        Supplier<Color> color = ColorBuilder.def().highlight(0.3f, 0.3f, 0.5f).build();
        addAction(new Action() {
            @Override
            public boolean act(float delta) {
                getStyle().fontColor = color.get();
                return false;
            }
        });
    }
}
