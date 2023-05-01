package me.datafox.ticktacktoe.frontend.utils;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author datafox
 */
public class UiUtils {
    public static ChangeListener simpleChangeListener(Runnable run) {
        return new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                run.run();
            }
        };
    }

    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");

    public static String getDate(long millis) {
        return simpleDateFormat.format(new Date(millis));
    }

    private static final DecimalFormat accurate = new DecimalFormat("#.###", DecimalFormatSymbols.getInstance());

    public static String formatFloat(float number) {
        if(Float.isInfinite(number) || Float.isNaN(number)) return "inf";
        return accurate.format(number);
    }
}
