package me.datafox.ticktacktoe.frontend.ui.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import me.datafox.ticktacktoe.api.LobbyDto;
import me.datafox.ticktacktoe.api.PlayerDto;
import me.datafox.ticktacktoe.api.Symbols;
import me.datafox.ticktacktoe.frontend.Game;
import me.datafox.ticktacktoe.frontend.ui.action.RunOnInactivityAction;
import me.datafox.ticktacktoe.frontend.ui.element.*;
import me.datafox.ticktacktoe.frontend.ui.screen.Screens;
import me.datafox.ticktacktoe.frontend.ui.screen.TitleScreen;
import me.datafox.ticktacktoe.frontend.utils.Defaults;
import me.datafox.ticktacktoe.frontend.utils.UiUtils;
import me.datafox.ticktacktoe.frontend.connection.ConnectionCallback;

/**
 * @author datafox
 */
public class LobbyView extends View {
    private final GameTextField name;
    private final Table left;
    private final Table right;
    private final GameLabel playersLabel;
    private final BaseTable players;
    private final GameLabel widthLabel;
    private final NumberSelector width;
    private final GameLabel heightLabel;
    private final NumberSelector height;
    private final GameLabel conditionLabel;
    private final NumberSelector condition;
    private final GameLabel fallModeLabel;
    private final SymbolToggleButton fallMode;
    private final GameTextButton start;
    private final GameTextButton exit;

    public LobbyView(TitleScreen parent) {
        super(parent);
        name = new GameTextField(Game.lobby().getLobby().getName(), GameTextField.freeformValidator());
        left = new Table();
        right = new Table();
        playersLabel = new GameLabel("Players:", Game.ui().getSmallFont());
        players = new BaseTable();
        widthLabel = new GameLabel("Width:", Game.ui().getSmallFont());
        width = new NumberSelector(3, 10, 3);
        heightLabel = new GameLabel("Height:", Game.ui().getSmallFont());
        height = new NumberSelector(3, 10, 3);
        conditionLabel = new GameLabel("Win condition:", Game.ui().getSmallFont());
        condition = new NumberSelector(3, 3, 3);
        fallModeLabel = new GameLabel("Fall mode:", Game.ui().getSmallFont());
        fallMode = new SymbolToggleButton(Symbols.X);
        start = new GameTextButton("Start game");
        exit = new GameTextButton("Exit lobby");

        widthLabel.setAlignment(Align.right);
        heightLabel.setAlignment(Align.right);
        conditionLabel.setAlignment(Align.right);
        fallModeLabel.setAlignment(Align.right);

        name.addAction(new RunOnInactivityAction<>(1, name::getText, this::setName));;

        width.addListener(this::setBoardWidth);
        height.addListener(this::setBoardHeight);
        condition.addListener(this::setWinCondition);
        fallMode.addListener(UiUtils.simpleChangeListener(this::setFallMode));
        start.addListener(UiUtils.simpleChangeListener(this::start));
        exit.addListener(UiUtils.simpleChangeListener(this::back));

        left.align(Align.topLeft);
        right.align(Align.topLeft);
        left.defaults().fill().expandX().space(Defaults.TABLE_SPACING).align(Align.topLeft);
        right.defaults().fill().expandX().space(Defaults.TABLE_SPACING).align(Align.topLeft);

        left.add(playersLabel).row();
        left.add(players).row();

        right.add(widthLabel).align(Align.right);
        right.add(width).row();
        right.add(heightLabel).align(Align.right);
        right.add(height).row();
        right.add(conditionLabel).align(Align.right);
        right.add(condition).row();
        right.add(fallModeLabel).align(Align.right);
        right.add(fallMode).fill(false, true).expand(false, false);

        add(name).colspan(2).row();
        add(left);
        add(right).row();
        add(start);
        add(exit);

        Game.lobby().setCallback(ConnectionCallback
                .builder(LobbyDto.class)
                .completed(this::set)
                .failed(parent::setMessage)
                .build());
    }

    private void start() {
        Game.lobby().start();
    }

    private void back() {
        Game.con().disconnect();
        parent.setView(Views.MainMenu);
    }

    public void set(LobbyDto lobby) {
        Gdx.app.postRunnable(() -> {
            if(lobby == null) return;
            if(lobby.isStarted()) {
                Game.ui().getPane().setScreen(Screens.Game);
                Game.lobby().unsubscribe();
                Game.game().start(lobby.getId());
                return;
            }
            players.clearChildren();

            width.setValue(lobby.getWidth());
            height.setValue(lobby.getHeight());
            condition.setValue(lobby.getWinCondition());
            fallMode.setChecked(lobby.isFallMode());
            start.setDisabled(lobby.getPlayerCount() != lobby.getPlayers().size());

            boolean host = lobby.getHost().getUsername().equals(Game.player().getUsername());
            if(!host) name.getField().setText(lobby.getName());
            name.getField().setDisabled(!host);
            width.setDisabled(!host);
            height.setDisabled(!host);
            condition.setDisabled(!host);
            fallMode.setDisabled(!host);
            start.setDisabled(!host);
            lobby.getPlayers().forEach(this::createPlayer);
        });
    }

    private void setBoardWidth(int width) {
        Game.lobby().setWidth(width);
        condition.setMax(Math.min(width, height.getValue()));
    }

    private void setBoardHeight(int height) {
        Game.lobby().setHeight(height);
        condition.setMax(Math.min(width.getValue(), height));
    }

    private void setWinCondition(int condition) {
        Game.lobby().setCondition(condition);
    }

    private void setFallMode() {
        Game.lobby().setFallMode(fallMode.isChecked());
    }

    private void createPlayer(String symbol, PlayerDto player) {
        GameLabel label = new GameLabel(player.getNickname());
        SymbolButton button = new SymbolButton(symbol);
        if(player.getUsername().equals(Game.player().getUsername()) &&
                player.getUsername().equals(Game.lobby().getLobby().getHost().getUsername())) {
            button.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    String s = "X";
                    if(symbol.equals("X")) s = "O";
                    Game.lobby().setSymbol(s);
                }
            });
        } else button.setDisabled(true);

        if(!players.getChildren().isEmpty()) players.row();
        players.add(label);
        players.add(button);
    }

    private void setName() {
        if(Game.lobby().getLobby().getHost() == null) return;
        if(!Game.lobby().getLobby().getHost().getUsername().equals(Game.player().getUsername())) return;
        if(name.getText().isBlank()) name.getField().setText(Game.player().getNickname() + "'s game");
        Game.lobby().setName(name.getText());
    }
}
