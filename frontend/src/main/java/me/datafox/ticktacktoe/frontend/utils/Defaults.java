package me.datafox.ticktacktoe.frontend.utils;

import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import me.datafox.ticktacktoe.frontend.drawable.FilledFrameDrawable;
import me.datafox.ticktacktoe.frontend.drawable.SidebarFrameDrawable;
import me.datafox.ticktacktoe.frontend.ui.ColorBuilder;

/**
 * @author datafox
 */
public class Defaults {
    public static final float WORLD_MIN_WIDTH = 600f;
    public static final float WORLD_MIN_HEIGHT = 600f;
    public static final float WORLD_BASE_WIDTH = 4f;
    public static final float WORLD_HALF_WIDTH = WORLD_BASE_WIDTH / 2;
    public static final float TABLE_SPACING = WORLD_BASE_WIDTH * 2;
    public static final float TABLE_PADDING = WORLD_BASE_WIDTH * 3;
    public static final float PI = (float) Math.PI;
    public static final float SQRT2 = (float) Math.sqrt(2);
    public static final float BUTTON_WIDTH = 40f;
    public static final float GAME_PADDING = 100f;

    public static Drawable defaultFilledFrame() {
        return new FilledFrameDrawable(ColorBuilder.def().highlight().build(),
                ColorBuilder.def().antiHighlight().darken().transparent(0.9f).desaturate().build());
    }

    public static Drawable darkerFilledFrame() {
        return new FilledFrameDrawable(ColorBuilder.def().highlight().darken().build(),
                ColorBuilder.def().antiHighlight().darken().darken().transparent(0.9f).desaturate().build());
    }

    public static Drawable lighterFilledFrame() {
        return new FilledFrameDrawable(ColorBuilder.def().highlight().lighten().build(),
                ColorBuilder.def().antiHighlight().transparent(0.9f).desaturate().build());
    }

    public static Drawable desaturatedFilledFrame() {
        return new FilledFrameDrawable(ColorBuilder.def().highlight().desaturate(0.6f).build(),
                ColorBuilder.def().antiHighlight().darken().transparent(0.9f).desaturate(0.9f).build());
    }

    public static Drawable sidebarFilledFrame() {
        return new SidebarFrameDrawable(ColorBuilder.def().highlight().build(),
                ColorBuilder.def().antiHighlight().darken().transparent(0.9f).desaturate().build());
    }
}
