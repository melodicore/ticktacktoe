package me.datafox.ticktacktoe.frontend.ui.element;

import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import me.datafox.ticktacktoe.frontend.ui.screen.*;

/**
 * @author datafox
 */
public class MainPane extends Stack {
    private final BackgroundPane background;

    public MainPane() {
        super();
        this.background = new BackgroundPane(1f);
        setFillParent(true);
        add(background);
    }

    public void setScreen(Screens screen) {
        clearChildren();
        add(background);
        switch(screen) {
            case Title -> add(new TitleScreen());
            case Game -> add(new GameScreen());
            case GameViewer -> add(new GameViewerScreen());
        }
    }
}
