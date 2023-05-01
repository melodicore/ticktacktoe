package me.datafox.ticktacktoe.frontend.ui.view;

import com.badlogic.gdx.Gdx;
import me.datafox.ticktacktoe.api.LobbyDto;
import me.datafox.ticktacktoe.frontend.Game;
import me.datafox.ticktacktoe.frontend.ui.element.GameTextButton;
import me.datafox.ticktacktoe.frontend.ui.screen.TitleScreen;
import me.datafox.ticktacktoe.frontend.utils.UiUtils;

/**
 * @author datafox
 */
public class MainMenuView extends View {
    private final GameTextButton create;
    private final GameTextButton join;
    private final GameTextButton view;
    private final GameTextButton leaderboard;
    private final GameTextButton settings;
    private final GameTextButton quit;

    public MainMenuView(TitleScreen parent) {
        super(parent);
        create = new GameTextButton("Create a lobby");
        join = new GameTextButton("Join a lobby");
        view = new GameTextButton("View games");
        leaderboard = new GameTextButton("Leaderboard");
        settings = new GameTextButton("Settings");
        quit = new GameTextButton("Quit");

        create.addListener(UiUtils.simpleChangeListener(this::createLobby));
        join.addListener(UiUtils.simpleChangeListener(() -> parent.setView(Views.Lobbies)));
        view.addListener(UiUtils.simpleChangeListener(() -> parent.setView(Views.Games)));
        leaderboard.addListener(UiUtils.simpleChangeListener(() -> parent.setView(Views.Leaderboard)));
        settings.addListener(UiUtils.simpleChangeListener(() -> parent.setView(Views.Settings)));
        quit.addListener(UiUtils.simpleChangeListener(Gdx.app::exit));

        add(create).row();
        add(join).row();
        add(view).row();
        add(leaderboard).row();
        add(settings).row();
        add(quit);
    }

    private void createLobby() {
        Game.lobby().setLobby(LobbyDto.builder().name(Game.player().getNickname() + "'s game").build());
        parent.setView(Views.Lobby);
        Game.lobby().create();
    }
}
