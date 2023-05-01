package me.datafox.ticktacktoe.frontend.ui.element;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import lombok.Getter;
import me.datafox.ticktacktoe.frontend.utils.Defaults;

/**
 * @author datafox
 */
public class Sidebar extends Table {
    @Getter
    private final Drawable defaultBackground;

    public Sidebar() {
        defaultBackground = Defaults.sidebarFilledFrame();
        background(defaultBackground);
        pad(Defaults.TABLE_PADDING);
        align(Align.topLeft);
        defaults().fill().expandX().space(Defaults.TABLE_SPACING).align(Align.topLeft);
    }
}
