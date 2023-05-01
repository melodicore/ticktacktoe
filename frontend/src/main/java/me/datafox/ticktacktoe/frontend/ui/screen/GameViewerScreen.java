package me.datafox.ticktacktoe.frontend.ui.screen;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.utils.Align;
import me.datafox.ticktacktoe.api.GameDto;
import me.datafox.ticktacktoe.api.PlayerDto;
import me.datafox.ticktacktoe.api.Symbols;
import me.datafox.ticktacktoe.frontend.Game;
import me.datafox.ticktacktoe.frontend.ui.element.*;
import me.datafox.ticktacktoe.frontend.ui.view.Views;
import me.datafox.ticktacktoe.frontend.utils.Defaults;
import me.datafox.ticktacktoe.frontend.utils.UiUtils;

/**
 * @author datafox
 */
public class GameViewerScreen extends Screen {
    private final GameLabel message;
    private final Table innerTable;
    private final Table boardTable;
    private final Sidebar sidebar;
    private final GameDto game;
    private final ViewerBoard board;
    private final GameLabel gameStats;
    private final GameLabel moveSelectorLabel;
    private final NumberSelector moveSelector;
    private final GameTextButton exit;

    public GameViewerScreen() {
        super();
        message = new GameLabel("");
        innerTable = new Table();
        boardTable = new Table();
        sidebar = new Sidebar();
        game = Game.game().getGame();
        board = new ViewerBoard(game);
        gameStats = new GameLabel(gameStats());
        moveSelectorLabel = new GameLabel("Move:");
        moveSelector = new NumberSelector(0, game.getMoves().size(), game.getMoves().size());
        exit = new GameTextButton("Back to main menu");

        setFillParent(true);

        defaults().fill().expandY();
        innerTable.defaults().fill().expandX();
        boardTable.defaults().fill();

        moveSelector.addListener(this::setMove);
        exit.addListener(UiUtils.simpleChangeListener(this::exit));
        moveSelectorLabel.setAlignment(Align.right);

        boardTable.add(board).align(Align.center).size(new BoardWidthValue(), new BoardHeightValue());

        sidebar.add(gameStats).colspan(3).row();
        sidebar.add(moveSelectorLabel).expand(false, false);
        sidebar.add(moveSelector).expand(false, false);
        sidebar.add().expandX().row();
        sidebar.add(exit).colspan(3).row();

        innerTable.add(boardTable).align(Align.center).expandY().row();
        innerTable.add(message).align(Align.bottom);

        add(innerTable).align(Align.center).expandX();
        add(sidebar).align(Align.topRight);
    }

    private String gameStats() {
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

    private void setMove(int move) {
        board.set(game, move);
    }

    private void exit() {
        Game.ui().getPane().setScreen(Screens.Title);
        ((TitleScreen) Game.ui().getPane().getChildren().get(1)).setView(Views.MainMenu);
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
