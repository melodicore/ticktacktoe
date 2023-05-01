package me.datafox.ticktacktoe.frontend.ui.element;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import me.datafox.ticktacktoe.frontend.Game;

/**
 * @author datafox
 */
public class GameLabel extends Label {
    public GameLabel(String text) {
        super(text, new LabelStyle(Game.ui().getFont(), Color.WHITE));
    }

    public GameLabel(String text, BitmapFont font) {
        super(text, new LabelStyle(font, Color.WHITE));
    }
}
