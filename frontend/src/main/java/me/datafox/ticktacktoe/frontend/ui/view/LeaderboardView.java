package me.datafox.ticktacktoe.frontend.ui.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import me.datafox.ticktacktoe.api.PlayerRankDto;
import me.datafox.ticktacktoe.frontend.Game;
import me.datafox.ticktacktoe.frontend.connection.ConnectionCallback;
import me.datafox.ticktacktoe.frontend.ui.element.BaseTable;
import me.datafox.ticktacktoe.frontend.ui.element.GameLabel;
import me.datafox.ticktacktoe.frontend.ui.element.GameTextButton;
import me.datafox.ticktacktoe.frontend.ui.screen.TitleScreen;
import me.datafox.ticktacktoe.frontend.utils.Defaults;
import me.datafox.ticktacktoe.frontend.utils.UiUtils;

import java.util.Arrays;

/**
 * @author datafox
 */
public class LeaderboardView extends View {
    private final GameTextButton refresh;
    private final GameTextButton previous;
    private final GameTextButton next;
    private final GameTextButton back;
    private final Table leaderboard;
    private int page;

    public LeaderboardView(TitleScreen parent) {
        super(parent);
        refresh = new GameTextButton("Refresh list", Game.ui().getSmallFont());
        previous = new GameTextButton("Previous page", Game.ui().getSmallFont());
        next = new GameTextButton("Next page", Game.ui().getSmallFont());
        back = new GameTextButton("Back to menu", Game.ui().getSmallFont());
        leaderboard = new Table();

        refresh.addListener(UiUtils.simpleChangeListener(this::refresh));
        previous.addListener(UiUtils.simpleChangeListener(this::previous));
        next.addListener(UiUtils.simpleChangeListener(this::next));
        back.addListener(UiUtils.simpleChangeListener(this::back));

        leaderboard.defaults().fill().expand().space(Defaults.TABLE_SPACING);

        add(refresh).colspan(3).row();
        add(leaderboard).colspan(3).row();
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
        Game.con().get("/leaderboard", page, ConnectionCallback
                .builder(PlayerRankDto[].class)
                .completed(this::refreshList)
                .failed(parent::setMessage)
                .build());
    }

    private void disabler(boolean disableNext) {
        previous.setDisabled(page == 0);
        next.setDisabled(disableNext);
    }

    private void refreshList(PlayerRankDto[] arr) {
        Gdx.app.postRunnable(() -> {
            leaderboard.clearChildren();
            disabler(arr.length <= 6);
            Arrays.stream(Arrays.copyOf(arr, Math.min(6, arr.length))).forEach(this::createRankPanel);
        });
    }

    private void createRankPanel(PlayerRankDto rank) {
        GameLabel name = new GameLabel("#" + rank.getRank() + ": " + rank.getPlayer().getNickname());
        GameLabel stats = new GameLabel("Ratio: " + UiUtils.formatFloat(rank.getRatio()) + ", Wins: " + rank.getWins() + "\nLosses: " + rank.getLosses() + ", Draws: " + rank.getDraws(), Game.ui().getSmallFont());
        BaseTable table = new BaseTable();
        table.add(name).row();
        table.add(stats);

        if(leaderboard.hasChildren() && leaderboard.getChildren().size % 2 == 0) leaderboard.row();
        leaderboard.add(table);
    }
}
