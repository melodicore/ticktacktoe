package me.datafox.ticktacktoe.frontend.ui.element;

import me.datafox.ticktacktoe.api.GameDto;
import me.datafox.ticktacktoe.frontend.Game;
import me.datafox.ticktacktoe.frontend.ui.ColorBuilder;
import me.datafox.ticktacktoe.frontend.utils.ColorUtils;
import me.datafox.ticktacktoe.frontend.utils.UiUtils;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author datafox
 */
public class Board extends BaseTable {
    public Board(GameDto game) {
        set(game);
    }

    public void set(GameDto game) {
        clearChildren();
        Map<String,String> reverse = game
                .getPlayers()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(e ->
                        e.getValue().getUsername(),
                        Map.Entry::getKey));
        for(int y=0;y<game.getHeight();y++) {
            for(int x=0;x<game.getWidth();x++) {
                String symbol = game.getBoard()[x + y * game.getWidth()];
                if(game.isFinished()) add(createButton(symbol, null,
                        game, false, x, y));
                else add(createButton(symbol, reverse.get(Game.player().getUsername()),
                        game,
                        Game.player().getUsername().equals(game.getCurrentPlayer().getUsername()),
                        x, y));
            }
            if(y != game.getHeight() - 1) row();
        }
    }

    private BoardButton createButton(String symbol, String checkedSymbol, GameDto game, boolean turn, int x, int y) {
        if(symbol == null) {
            BoardButton button = new BoardButton(checkedSymbol, false, turn);
            if(checkedSymbol != null) button.addListener(UiUtils.simpleChangeListener(new Runnable() {
                @Override
                public void run() {
                    Game.game().addMove(checkedSymbol, x, y);
                }
            }));
            return button;
        }
        return new BoardButton(symbol, ColorBuilder.of(ColorUtils.toGdx(game.getPlayers().get(symbol).getColor())).build(), true, false);
    }
}
