package me.datafox.ticktacktoe.frontend.connection;

import lombok.Getter;
import me.datafox.ticktacktoe.api.Constants;

/**
 * @author datafox
 */
public class ConnectionManager {
    private final RestHandler rest;
    private final SocketHandler socket;
    @Getter private String address;
    @Getter private String sessionId;

    public ConnectionManager() {
        rest = new RestHandler();
        socket = new SocketHandler();
    }

    public void connect(String address, ConnectionCallback<String> callback) {
        if(address.replaceAll(":[0-9]+", "").equals(address)) address += ":" + Constants.PORT;
        this.address = address;
        rest.connect(address, callback);
    }

    public void login(String username, String password, EmptyCallback callback) {
        rest.login(username, password, RestHandler
                .loginCallback(t -> sessionId = t)
                .completed(s -> callback.completed(""))
                .failed(callback::failed).build());
    }

    public <T> void get(String endpoint, ConnectionCallback<T> callback) {
        rest.get(endpoint, callback);
    }

    public <T> void get(String endpoint, int page, ConnectionCallback<T> callback) {
        rest.get(endpoint + "?page=" + page, callback);
    }

    public <T> void get(String endpoint, int page, String params, ConnectionCallback<T> callback) {
        rest.get(endpoint + "?page=" + page + "&" + params, callback);
    }

    public <S,R> void post(String endpoint, S data, ConnectionCallback<R> callback) {
        rest.post(endpoint, data, callback);
    }

    public <T> void subscribe(String endpoint, ConnectionCallback<T> callback) {
        if(!socket.isConnected()) {
            socket.connect(sessionId,
                    EmptyCallback
                            .builder()
                            .completed(() -> subscribe(endpoint, callback))
                            .failed(callback::failed)
                            .build());
            return;
        }
        socket.subscribe(endpoint, callback);
    }

    public void unsubscribe(String endpoint) {
        if(!socket.isConnected()) return;
        socket.unsubscribe(endpoint);
    }


    public <T> void send(String endpoint, T data) {
        if(socket.isConnected()) socket.send(endpoint, data, EmptyCallback.builder().build());
    }

    public void disconnect() {
        socket.disconnect();
    }
}
