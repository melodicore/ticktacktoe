package me.datafox.ticktacktoe.frontend.ui.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import me.datafox.ticktacktoe.api.LobbyDto;
import me.datafox.ticktacktoe.frontend.Game;
import me.datafox.ticktacktoe.frontend.ui.element.BaseTable;
import me.datafox.ticktacktoe.frontend.ui.element.GameLabel;
import me.datafox.ticktacktoe.frontend.ui.element.GameTextButton;
import me.datafox.ticktacktoe.frontend.ui.screen.TitleScreen;
import me.datafox.ticktacktoe.frontend.utils.Defaults;
import me.datafox.ticktacktoe.frontend.utils.UiUtils;
import me.datafox.ticktacktoe.frontend.connection.ConnectionCallback;

import java.util.Arrays;

/**
 * @author datafox
 */
public class LobbiesView extends View {
    private final GameTextButton refresh;
    private final GameTextButton previous;
    private final GameTextButton next;
    private final GameTextButton back;
    private final Table lobbies;
    private int page;

    public LobbiesView(TitleScreen parent) {
        super(parent);
        refresh = new GameTextButton("Refresh list", Game.ui().getSmallFont());
        previous = new GameTextButton("Previous page", Game.ui().getSmallFont());
        next = new GameTextButton("Next page", Game.ui().getSmallFont());
        back = new GameTextButton("Back to menu", Game.ui().getSmallFont());
        lobbies = new Table();

        refresh.addListener(UiUtils.simpleChangeListener(this::refresh));
        previous.addListener(UiUtils.simpleChangeListener(this::previous));
        next.addListener(UiUtils.simpleChangeListener(this::next));
        back.addListener(UiUtils.simpleChangeListener(this::back));

        lobbies.defaults().fill().expand().space(Defaults.TABLE_SPACING);

        add(refresh).colspan(3).row();
        add(lobbies).colspan(3).row();
        add(previous);
        add(next);
        add(back);
        refresh();
    }

    private void refresh() {
        page = 0;
        previous.setDisabled(true);
        refreshPage();
    }

    private void previous() {
        page = Math.max(0, page - 1);
        refreshPage();
    }

    private void next() {
        page += 1;
        refreshPage();
    }

    private void back() {
        parent.setView(Views.MainMenu);
    }

    private void refreshPage() {
        Game.con().get("/lobby/get", page, ConnectionCallback
                .builder(LobbyDto[].class)
                .completed(this::refreshList)
                .failed(parent::setMessage)
                .build());
    }

    private void disabler(boolean disableNext) {
        previous.setDisabled(page == 0);
        next.setDisabled(disableNext);
    }

    private void refreshList(LobbyDto[] arr) {
        Gdx.app.postRunnable(() -> {
            lobbies.clearChildren();
            disabler(arr.length <= 6);
            Arrays.stream(Arrays.copyOf(arr, Math.min(6, arr.length))).forEach(this::createLobbyPanel);
        });
    }

    private void createLobbyPanel(LobbyDto lobby) {
        GameLabel name = new GameLabel(lobby.getName(), Game.ui().getSmallFont());
        GameLabel players = new GameLabel("Players: " + lobby.getPlayers().size() + "/" + lobby.getPlayerCount(), Game.ui().getSmallFont());
        GameTextButton join = new GameTextButton("Join");
        BaseTable table = new BaseTable();
        VerticalGroup group = new VerticalGroup();

        join.addListener(UiUtils.simpleChangeListener(() -> {
            Game.lobby().setLobby(lobby);
            parent.setView(Views.Lobby);
            Game.lobby().join(lobby.getId());
        }));

        group.addActor(name);
        group.addActor(players);

        table.add(group);
        table.add(join);

        if(lobbies.hasChildren() && lobbies.getChildren().size % 2 == 0) lobbies.row();
        lobbies.add(table);
    }
}
