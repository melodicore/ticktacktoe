package me.datafox.ticktacktoe.frontend.connection;

import lombok.Getter;
import lombok.Setter;
import me.datafox.ticktacktoe.api.LobbyDto;
import me.datafox.ticktacktoe.frontend.Game;

/**
 * @author datafox
 */
public class LobbyHandler implements ConnectionCallback<LobbyDto> {
    private ConnectionCallback<LobbyDto> callback;
    @Setter @Getter private LobbyDto lobby;
    private boolean connected;

    public LobbyHandler() {
        connected = false;
    }

    public void create() {
        Game.con().get("/lobby/create", ConnectionCallback
                .builder(LobbyDto.class)
                .completed(this::subscribe)
                .failed(this::failed)
                .build());
    }

    public void subscribe(LobbyDto lobby) {
        completed(lobby);
        Game.con().subscribe("/out/lobby/" + lobby.getId(), this);
        connected = true;
    }

    public void unsubscribe() {
        Game.con().unsubscribe("/out/lobby/" + lobby.getId());
        connected = false;
    }

    public void setCallback(ConnectionCallback<LobbyDto> callback) {
        this.callback = callback;
    }

    public void join(String lobbyId) {
        Game.con().get("/lobby/" + lobbyId + "/join", ConnectionCallback
                .builder(LobbyDto.class)
                .completed(this::subscribe)
                .failed(this::failed)
                .build());
    }

    public void setSymbol(String symbol) {
        if(!isHost()) return;
        Game.con().send(getEndpoint("symbol"), symbol);
    }

    public void setName(String name) {
        if(!isHost()) return;
        Game.con().send(getEndpoint("name"), name);
    }

    public void setWidth(int width) {
        if(!isHost()) return;
        Game.con().send(getEndpoint("width"), width);
    }

    public void setHeight(int height) {
        if(!isHost()) return;
        Game.con().send(getEndpoint("height"), height);
    }

    public void setCondition(int condition) {
        if(!isHost()) return;
        Game.con().send(getEndpoint("condition"), condition);
    }

    public void setFallMode(boolean fallMode) {
        if(!isHost()) return;
        Game.con().send(getEndpoint("fall"), fallMode);
    }

    public void start() {
        if(!isHost()) return;
        Game.con().send(getEndpoint("start"), new Object());
    }

    private String getEndpoint(String endpoint) {
        return "/in/lobby/" + lobby.getId() + "/" + endpoint;
    }

    private boolean isHost() {
        return Game.player().getUsername().equals(lobby.getHost().getUsername());
    }

    @Override
    public Class<LobbyDto> getType() {
        return LobbyDto.class;
    }

    @Override
    public void completed(LobbyDto lobby) {
        this.lobby = lobby;
        callback.completed(lobby);
    }

    @Override
    public void failed(String reason) {
        callback.failed(reason);
    }

    public boolean isConnected() {
        return connected;
    }
}
