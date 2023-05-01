package me.datafox.ticktacktoe.frontend.ui.screen;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.utils.Align;
import me.datafox.ticktacktoe.frontend.Game;
import me.datafox.ticktacktoe.frontend.ui.action.FadeOutAction;
import me.datafox.ticktacktoe.frontend.ui.element.GameLabel;
import me.datafox.ticktacktoe.frontend.ui.element.TitleLabel;
import me.datafox.ticktacktoe.frontend.ui.view.*;

import java.util.concurrent.TimeUnit;

/**
 * @author datafox
 */
public class TitleScreen extends Screen {
    private final TitleLabel title;
    private final Table viewContainer;
    private final GameLabel message;

    public TitleScreen() {
        super();
        title = new TitleLabel("Tick Tack Toe!");
        viewContainer = new Table();
        message = new GameLabel("");

        setFillParent(true);
        defaults().align(Align.center).expand();
        add(title).prefHeight(new Value() {
            @Override
            public float get(Actor context) {
                return Game.ui().getWorldHeight() / 8;
            }
        }).row();
        add(viewContainer).row();
        add(message).prefHeight(new Value() {
            @Override
            public float get(Actor context) {
                return Game.ui().getWorldHeight() / 8;
            }
        });

        setView(Views.Connect);
    }

    public void setView(Views view) {
        viewContainer.clearChildren();
        switch(view) {
            case Connect -> viewContainer.add(new ConnectView(this));
            case Login -> viewContainer.add(new LoginView(this));
            case Register -> viewContainer.add(new RegisterView(this));
            case MainMenu -> viewContainer.add(new MainMenuView(this));
            case Lobby -> viewContainer.add(new LobbyView(this));
            case Lobbies -> viewContainer.add(new LobbiesView(this));
            case Games -> viewContainer.add(new GamesView(this));
            case Leaderboard -> viewContainer.add(new LeaderboardView(this));
            case Settings -> viewContainer.add(new SettingsView(this));
        }
    }

    public void setMessage(String text) {
        message.setColor(1,1,1,1);
        message.setText(text);
        System.out.println(text);
        Game.scheduler().schedule(() -> message.addAction(new FadeOutAction(2)), 2, TimeUnit.SECONDS);
    }
}
