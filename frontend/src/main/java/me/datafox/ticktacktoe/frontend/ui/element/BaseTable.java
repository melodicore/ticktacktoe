package me.datafox.ticktacktoe.frontend.ui.element;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import lombok.Getter;
import me.datafox.ticktacktoe.frontend.utils.Defaults;

/**
 * @author datafox
 */
public class BaseTable extends Table {
    @Getter private final Drawable defaultBackground;

    public BaseTable() {
        defaultBackground = Defaults.defaultFilledFrame();
        background(defaultBackground);
        pad(Defaults.TABLE_PADDING);
        defaults().fill().expand().space(Defaults.TABLE_SPACING);
    }
}
