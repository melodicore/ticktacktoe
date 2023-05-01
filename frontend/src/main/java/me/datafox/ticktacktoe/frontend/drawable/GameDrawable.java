package me.datafox.ticktacktoe.frontend.drawable;

import me.datafox.ticktacktoe.frontend.Game;
import space.earlygrey.shapedrawer.scene2d.ShapeDrawerDrawable;

/**
 * @author datafox
 */
public abstract class GameDrawable extends ShapeDrawerDrawable {
    public GameDrawable() {
        super(Game.ui().getDrawer());
    }
}
