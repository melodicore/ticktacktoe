package me.datafox.ticktacktoe.frontend.ui.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.utils.Align;
import me.datafox.ticktacktoe.api.GameDto;
import me.datafox.ticktacktoe.api.PlayerDto;
import me.datafox.ticktacktoe.api.Symbols;
import me.datafox.ticktacktoe.frontend.Game;
import me.datafox.ticktacktoe.frontend.ui.action.FadeOutAction;
import me.datafox.ticktacktoe.frontend.ui.action.RunAfterAction;
import me.datafox.ticktacktoe.frontend.ui.element.Board;
import me.datafox.ticktacktoe.frontend.ui.element.GameLabel;
import me.datafox.ticktacktoe.frontend.ui.element.GameTextButton;
import me.datafox.ticktacktoe.frontend.ui.element.Sidebar;
import me.datafox.ticktacktoe.frontend.utils.Defaults;
import me.datafox.ticktacktoe.frontend.utils.UiUtils;
import me.datafox.ticktacktoe.frontend.connection.ConnectionCallback;

import java.util.concurrent.TimeUnit;

/**
 * @author datafox
 */
public class GameScreen extends Screen {
    private final GameLabel message;
    private final Table innerTable;
    private final Table boardTable;
    private final Sidebar sidebar;
    private final GameLabel gameStats;
    private final GameTextButton abandon;
    private final GameTextButton abandonSure;
    private Board board;
    private boolean abandoning;

    public GameScreen() {
        super();
        message = new GameLabel("");
        innerTable = new Table();
        boardTable = new Table();
        sidebar = new Sidebar();
        gameStats = new GameLabel("");
        abandon = new GameTextButton("Abandon game");
        abandonSure = new GameTextButton("Are you sure?");

        setFillParent(true);

        defaults().fill().expandY();
        innerTable.defaults().fill().expandX();
        boardTable.defaults().fill();

        abandon.addListener(UiUtils.simpleChangeListener(this::abandonPrompt));
        abandonSure.addListener(UiUtils.simpleChangeListener(this::abandon));

        innerTable.add(boardTable).align(Align.center).expandY().row();
        innerTable.add(message).align(Align.bottom);

        add(innerTable).align(Align.center).expandX();
        add(sidebar).align(Align.right);

        Game.game().setCallback(ConnectionCallback
                .builder(GameDto.class)
                .completed(this::set)
                .failed(this::setMessage)
                .build());
    }

    private void abandonPrompt() {
        abandoning = true;
        abandonSure.addAction(new RunAfterAction(4, this::cancelAbandon));
        refreshSidebar();
    }

    private void cancelAbandon() {
        abandoning = false;
        refreshSidebar();
    }

    private void abandon() {
        Game.ui().getPane().setScreen(Screens.GameViewer);
        Game.game().abandon();
    }

    private void set(GameDto game) {
        Gdx.app.postRunnable(() -> {
            if(board == null) {
                board = new Board(game);
                boardTable.add(board).align(Align.center).size(new BoardWidthValue(), new BoardHeightValue());
            }
            else board.set(game);
            gameStats.setText(gameStats(game));
            if(game.isFinished()) {
                Game.ui().getPane().setScreen(Screens.GameViewer);
                Game.game().disconnect(game);
            }
            refreshSidebar();
        });
    }

    private String gameStats(GameDto game) {
        PlayerDto x = game.getPlayers().get(Symbols.X);
        PlayerDto o = game.getPlayers().get(Symbols.O);
        String stats = game.getName() + "\nStarted: " + UiUtils.getDate(game.getTimestamp()) + "\nPlayers:\n" + Symbols.X + ": " + x.getNickname();
        boolean oWinner = false;
        if(game.isFinished() && game.getCurrentPlayer() != null) {
            if(game.getCurrentPlayer().getUsername().equals(x.getUsername())) stats += " (winner)";
            else oWinner = true;
        }
        stats += "\n" + Symbols.O + ": " + o.getNickname();
        if(oWinner) stats += " (winner)";
        return stats;
    }

    private void refreshSidebar() {
        sidebar.clearChildren();
        sidebar.add(gameStats).row();
        sidebar.add(abandoning ? abandonSure : abandon).row();
    }

    public void setMessage(String text) {
        message.setColor(1,1,1,1);
        message.setText(text);
        Game.scheduler().schedule(() -> message.addAction(new FadeOutAction(2)), 2, TimeUnit.SECONDS);
    }

    private abstract class BoardSizeValue extends Value {
        protected Vector2 getSize() {
            int boardWidth = Game.game().getGame().getWidth();
            int boardHeight = Game.game().getGame().getHeight();
            float widthOffset = Defaults.TABLE_PADDING * 2 + Defaults.TABLE_SPACING * (boardWidth - 1);
            float heightOffset = Defaults.TABLE_PADDING * 2 + Defaults.TABLE_SPACING * (boardHeight - 1);
            float maxWidth = Game.ui().getWorldWidth() - Defaults.GAME_PADDING * 2 - sidebar.getWidth();
            float maxHeight = Game.ui().getWorldHeight() - Defaults.GAME_PADDING * 2;
            if((maxWidth - widthOffset) / boardWidth < (maxHeight - heightOffset) / boardHeight) {
                float side = (maxWidth - widthOffset) / boardWidth;
                float height = side * boardHeight + heightOffset;
                return new Vector2(maxWidth, height);
            }
            if((maxWidth - widthOffset) / boardWidth > (maxHeight - heightOffset) / boardHeight) {
                float side = (maxHeight - heightOffset) / boardHeight;
                float width = side * boardWidth + widthOffset;
                return new Vector2(width, maxHeight);
            }
            return new Vector2(maxWidth, maxHeight);
        }
    }

    private class BoardWidthValue extends BoardSizeValue {
        @Override
        public float get(Actor context) {
            return getSize().x;
        }
    }

    private class BoardHeightValue extends BoardSizeValue {
        @Override
        public float get(Actor context) {
            return getSize().y;
        }
    }
}
