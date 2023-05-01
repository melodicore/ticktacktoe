package me.datafox.ticktacktoe.frontend.ui.action;

import com.badlogic.gdx.scenes.scene2d.Action;

/**
 * @author datafox
 */
public class RunAfterAction extends Action {
    private final float seconds;
    private final Runnable action;
    private float counter;
    private boolean ran;
    public RunAfterAction(float seconds, Runnable action) {
        this.seconds = seconds;
        this.action = action;
        this.counter = 0;
        ran = false;
    }

    @Override
    public boolean act(float delta) {
        counter += delta;
        if(counter >= seconds && !ran) {
            ran = true;
            action.run();
            return true;
        }
        return false;
    }
}
