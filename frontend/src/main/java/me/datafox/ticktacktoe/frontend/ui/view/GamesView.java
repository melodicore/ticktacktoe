package me.datafox.ticktacktoe.frontend.ui.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import me.datafox.ticktacktoe.api.GameDto;
import me.datafox.ticktacktoe.api.PlayerDto;
import me.datafox.ticktacktoe.api.Symbols;
import me.datafox.ticktacktoe.frontend.Game;
import me.datafox.ticktacktoe.frontend.ui.element.BaseTable;
import me.datafox.ticktacktoe.frontend.ui.element.GameLabel;
import me.datafox.ticktacktoe.frontend.ui.element.GameTextButton;
import me.datafox.ticktacktoe.frontend.ui.element.SymbolToggleButton;
import me.datafox.ticktacktoe.frontend.ui.screen.Screens;
import me.datafox.ticktacktoe.frontend.ui.screen.TitleScreen;
import me.datafox.ticktacktoe.frontend.utils.Defaults;
import me.datafox.ticktacktoe.frontend.utils.UiUtils;
import me.datafox.ticktacktoe.frontend.connection.ConnectionCallback;

import java.util.Arrays;

/**
 * @author datafox
 */
public class GamesView extends View {
    private final Table topTable;
    private final GameTextButton refresh;
    private final GameLabel onlyFinishedLabel;
    private final SymbolToggleButton onlyFinished;
    private final GameLabel noFinishedLabel;
    private final SymbolToggleButton noFinished;
    private final GameTextButton previous;
    private final GameTextButton next;
    private final GameTextButton back;
    private final Table games;
    private int page;

    public GamesView(TitleScreen parent) {
        super(parent);
        topTable = new Table();
        refresh = new GameTextButton("Refresh list", Game.ui().getSmallFont());
        onlyFinishedLabel = new GameLabel("Show finished only: ", Game.ui().getSmallFont());
        onlyFinished = new SymbolToggleButton(Symbols.X);
        noFinishedLabel = new GameLabel("Show ongoing only: ", Game.ui().getSmallFont());
        noFinished = new SymbolToggleButton(Symbols.X);

        previous = new GameTextButton("Previous page", Game.ui().getSmallFont());
        next = new GameTextButton("Next page", Game.ui().getSmallFont());
        back = new GameTextButton("Back to menu", Game.ui().getSmallFont());
        games = new Table();

        topTable.defaults().space(Defaults.TABLE_SPACING);
        noFinishedLabel.setAlignment(Align.right);

        refresh.addListener(UiUtils.simpleChangeListener(this::refresh));
        onlyFinished.addListener(UiUtils.simpleChangeListener(this::onlyFinishedToggle));
        noFinished.addListener(UiUtils.simpleChangeListener(this::noFinishedToggle));

        previous.addListener(UiUtils.simpleChangeListener(this::previous));
        next.addListener(UiUtils.simpleChangeListener(this::next));
        back.addListener(UiUtils.simpleChangeListener(this::back));

        games.defaults().fill().expand().space(Defaults.TABLE_SPACING);

        topTable.add(refresh).fill().expand();
        topTable.add(onlyFinishedLabel);
        topTable.add(onlyFinished);
        topTable.add(noFinishedLabel);
        topTable.add(noFinished).row();

        add(topTable).colspan(3).row();
        add(games).colspan(3).row();
        add(previous);
        add(next);
        add(back);
        refresh();
    }

    private void onlyFinishedToggle() {
        if(noFinished.isChecked()) onlyFinished.setChecked(false);
        noFinished.setDisabled(onlyFinished.isChecked());
        refresh();
    }

    private void noFinishedToggle() {
        if(onlyFinished.isChecked()) noFinished.setChecked(false);
        onlyFinished.setDisabled(noFinished.isChecked());
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
        int mode = 0;
        if(onlyFinished.isChecked()) mode = 1;
        else if(noFinished.isChecked()) mode = 2;
        Game.con().get("/game/get", page, "mode=" + mode, ConnectionCallback
                .builder(GameDto[].class)
                .completed(this::refreshList)
                .failed(parent::setMessage)
                .build());
    }

    private void disabler(boolean disableNext) {
        previous.setDisabled(page == 0);
        next.setDisabled(disableNext);
    }

    private void refreshList(GameDto[] arr) {
        Gdx.app.postRunnable(() -> {
            games.clearChildren();
            disabler(arr.length <= 6);
            Arrays.stream(Arrays.copyOf(arr, Math.min(6, arr.length))).forEach(this::createGamePanel);
        });
    }

    private void createGamePanel(GameDto game) {
        GameLabel name = new GameLabel(game.getName() + " (" + UiUtils.getDate(game.getTimestamp()) + ")", Game.ui().getSmallFont());
        PlayerDto x = game.getPlayers().get(Symbols.X);
        PlayerDto o = game.getPlayers().get(Symbols.O);
        String playersLabel = Symbols.X + ": " + x.getNickname();
        boolean oWinner = false;
        if(game.isFinished() && game.getCurrentPlayer() != null) {
            if(game.getCurrentPlayer().getUsername().equals(x.getUsername())) playersLabel += " (winner)";
            else oWinner = true;
        }
        playersLabel += "\n" + Symbols.O + ": " + o.getNickname();
        if(oWinner) playersLabel += " (winner)";
        GameLabel players = new GameLabel(playersLabel, Game.ui().getSmallFont());
        GameTextButton view = new GameTextButton(game.isFinished() ? "View" : "Watch");
        BaseTable table = new BaseTable();

        view.addListener(UiUtils.simpleChangeListener(() -> {
            if(game.isFinished()) {
                Game.game().join(game);
                Game.ui().getPane().setScreen(Screens.GameViewer);
            } else {
                Game.ui().getPane().setScreen(Screens.Game);
                Game.game().join(game);
            }
        }));

        table.add(name).colspan(2).align(Align.center).row();
        table.add(players);
        table.add(view);

        if(games.hasChildren() && games.getChildren().size % 2 == 0) games.row();
        games.add(table);
    }
}
