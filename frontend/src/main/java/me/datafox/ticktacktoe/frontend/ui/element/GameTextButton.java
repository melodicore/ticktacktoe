package me.datafox.ticktacktoe.frontend.ui.element;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import me.datafox.ticktacktoe.frontend.Game;
import me.datafox.ticktacktoe.frontend.utils.Defaults;

/**
 * @author datafox
 */
public class GameTextButton extends TextButton {
    public GameTextButton(String text, BitmapFont font) {
        super(text, new TextButtonStyle(Defaults.defaultFilledFrame(),
                Defaults.darkerFilledFrame(), null, font));
        getStyle().over = Defaults.lighterFilledFrame();
        getStyle().disabled = Defaults.desaturatedFilledFrame();
        pad(Defaults.TABLE_SPACING, Defaults.TABLE_PADDING, Defaults.TABLE_SPACING, Defaults.TABLE_PADDING);
    }

    public GameTextButton(String text) {
        this(text, Game.ui().getFont());
    }
}
