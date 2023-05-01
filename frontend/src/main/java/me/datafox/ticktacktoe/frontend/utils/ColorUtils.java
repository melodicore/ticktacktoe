package me.datafox.ticktacktoe.frontend.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import me.datafox.ticktacktoe.api.ColorDto;
import me.datafox.ticktacktoe.frontend.Game;

import java.util.function.Supplier;

/**
 * @author datafox
 */
public class ColorUtils {
    public static Supplier<Color> of(Color color) {
        return () -> color;
    }

    public static Supplier<Color> random() {
        Color color = Color.WHITE.cpy();
        float h = Game.rng().nextFloat(360);
        float s = Game.rng().nextFloat();
        float v = (1 - s);
        color.fromHsv(h, s, v);
        color = toAccentColor(color);
        return of(color);
    }

    public static java.awt.Color toAwt(Color color) {
        if(color == null) return null;
        return new java.awt.Color(color.r, color.g, color.b, color.a);
    }

    public static Color toGdx(java.awt.Color color) {
        if(color == null) return null;
        return new Color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
    }

    public static ColorDto toDto(Color color) {
        if(color == null) return null;
        return ColorDto.builder().r(color.r).g(color.g).b(color.b).build();
    }

    public static Color toGdx(ColorDto color) {
        if(color == null) return null;
        return new Color(color.getR(), color.getG(), color.getB(), 1);
    }

    public static Color toAccentColor(Color color) {
        if(color == null) return null;
        float[] hsv = color.toHsv(new float[3]);
        hsv[1] = MathUtils.lerp(0, 0.6f, hsv[1]);
        hsv[2] = MathUtils.lerp(0.2f, 0.8f, hsv[2]);
        return color.cpy().fromHsv(hsv);
    }

    public static Color toRealColor(Color color) {
        if(color == null) return null;
        float[] hsv = color.toHsv(new float[3]);
        hsv[1] = hsv[1] / 0.6f;
        hsv[2] = (hsv[2] - 0.2f) / 0.6f;
        return color.cpy().fromHsv(hsv);
    }
}
