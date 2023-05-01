package me.datafox.ticktacktoe.frontend.connection;

import lombok.Getter;
import me.datafox.ticktacktoe.api.GameDto;
import me.datafox.ticktacktoe.api.MoveDto;
import me.datafox.ticktacktoe.frontend.Game;

/**
 * @author datafox
 */
public class GameHandler implements ConnectionCallback<GameDto> {
    private ConnectionCallback<GameDto> callback;
    @Getter private GameDto game;

    public GameHandler() {
    }

    public void start(String gameId) {
        Game.con().get("/game/" + gameId + "/get", ConnectionCallback
                .builder(GameDto.class)
                .completed(this::join)
                .failed(this::failed)
                .build());
    }

    public void join(GameDto game) {
        completed(game);
        if(!game.isFinished()) Game.con().subscribe("/out/game/" + game.getId(), this);
    }

    public void abandon() {
        if(game == null) return;
        Game.con().send(getEndpoint("abandon"), ConnectionCallback
                .builder(GameDto.class)
                .completed(this::disconnect)
                .failed(this::failed)
                .build());
    }

    public void disconnect(GameDto gameDto) {
        completed(game);
        callback = null;
        Game.con().disconnect();
    }

    public void remove(GameDto game, EmptyCallback callback) {
        Game.con().post("/game/remove", game, callback);
    }

    public void setCallback(ConnectionCallback<GameDto> callback) {
        this.callback = callback;
    }

    public void addMove(String symbol, int x, int y) {
        String current = game.getCurrentPlayer().getUsername();
        if(!current.equals(Game.player().getUsername()) || !game.getPlayers().get(symbol).getUsername().equals(current)) return;
        MoveDto move = MoveDto
                .builder()
                .symbol(symbol)
                .x(x)
                .y(y)
                .build();
        Game.con().send(getEndpoint("move"), move);
    }

    private String getEndpoint(String endpoint) {
        return "/in/game/" + game.getId() + "/" + endpoint;
    }

    @Override
    public Class<GameDto> getType() {
        return GameDto.class;
    }

    @Override
    public void completed(GameDto game) {
        this.game = game;
        if(callback != null) callback.completed(game);
    }

    @Override
    public void failed(String reason) {
        callback.failed(reason);
    }
}
