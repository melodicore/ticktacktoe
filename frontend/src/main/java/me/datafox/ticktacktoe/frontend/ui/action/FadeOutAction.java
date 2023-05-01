package me.datafox.ticktacktoe.frontend.ui.action;

import com.badlogic.gdx.scenes.scene2d.Action;

/**
 * @author datafox
 */
public class FadeOutAction extends Action {
    public FadeOutAction(float seconds) {
    }

    @Override
    public boolean act(float delta) {
        getActor().getColor().a -= delta;
        if(getActor().getColor().a < 0) {
            getActor().getColor().a = 0;
            return true;
        }
        return false;
    }
}
