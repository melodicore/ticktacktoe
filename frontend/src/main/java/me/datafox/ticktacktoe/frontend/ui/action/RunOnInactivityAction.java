package me.datafox.ticktacktoe.frontend.ui.action;

import com.badlogic.gdx.scenes.scene2d.Action;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author datafox
 */
public class RunOnInactivityAction<T> extends Action {
    private final float seconds;
    private final Supplier<T> trackedValue;
    private final Runnable action;
    private T lastValue;
    private float counter;
    private boolean ran;
    public RunOnInactivityAction(float seconds, Supplier<T> trackedValue, Runnable action) {
        this.seconds = seconds;
        this.trackedValue = trackedValue;
        this.action = action;
        lastValue = trackedValue.get();
        this.counter = 0;
        ran = false;
    }

    @Override
    public boolean act(float delta) {
        counter += delta;
        T currentValue = trackedValue.get();
        if(!Objects.equals(currentValue, lastValue)) {
            lastValue = currentValue;
            counter = 0;
            ran = false;
        }
        if(counter >= seconds && !ran) {
            ran = true;
            action.run();
        }
        return false;
    }
}
