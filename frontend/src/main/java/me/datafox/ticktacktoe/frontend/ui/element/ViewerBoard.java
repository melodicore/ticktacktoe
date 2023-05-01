package me.datafox.ticktacktoe.frontend.ui.element;

import me.datafox.ticktacktoe.api.GameDto;
import me.datafox.ticktacktoe.api.MoveDto;
import me.datafox.ticktacktoe.frontend.ui.ColorBuilder;
import me.datafox.ticktacktoe.frontend.utils.ColorUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author datafox
 */
public class ViewerBoard extends BaseTable {
    public ViewerBoard(GameDto game) {
        set(game, game.getMoves().size());
    }

    public void set(GameDto game, int move) {
        clearChildren();
        move = Math.min(move, game.getMoves().size());
        Map<Integer,String> symbols = new HashMap<>();
        for(int i=0;i<move;i++) {
            MoveDto m = game.getMoves().get(i);
            symbols.put(m.getX() + m.getY() * game.getWidth(), m.getSymbol());
        }
        for(int y=0;y<game.getHeight();y++) {
            for(int x=0;x<game.getWidth();x++) {
                String symbol = symbols.get(x + y * game.getWidth());
                add(createButton(symbol, game, x, y));
            }
            if(y != game.getHeight() - 1) row();
        }
    }

    private BoardButton createButton(String symbol, GameDto game, int x, int y) {
        if(symbol == null) {
            return new BoardButton(null, false, false);
        }
        return new BoardButton(symbol, ColorBuilder.of(ColorUtils.toGdx(game.getPlayers().get(symbol).getColor())).build(), true, false);
    }
}
