package me.datafox.ticktacktoe.frontend.connection;

import me.datafox.ticktacktoe.frontend.Game;
import me.datafox.ticktacktoe.frontend.data.JsonStringMessageConverter;
import org.springframework.messaging.converter.CompositeMessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.scheduling.concurrent.DefaultManagedTaskScheduler;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author datafox
 */
public class SocketHandler {
    private final WebSocketStompClient stomp;
    private final StompSessionHandler handler;
    private final Map<String,StompSession.Subscription> subscriptions;
    private StompSession session;
    private EmptyCallback connectCallback;

    public SocketHandler() {
        stomp = new WebSocketStompClient(new StandardWebSocketClient());
        stomp.setMessageConverter(new CompositeMessageConverter(List.of(new JsonStringMessageConverter(), new StringMessageConverter())));
        stomp.setTaskScheduler(new DefaultManagedTaskScheduler());
        handler = new SessionHandler();
        subscriptions = new HashMap<>();
    }

    public void connect(String sessionId, EmptyCallback callback) {
        WebSocketHttpHeaders handshakeHeaders = new WebSocketHttpHeaders();
        handshakeHeaders.put(WebSocketHttpHeaders.COOKIE, List.of("JSESSIONID=\"" + sessionId + "\""));
        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.put(WebSocketHttpHeaders.COOKIE, List.of("JSESSIONID=\"" + sessionId + "\""));
        connectCallback = callback;
        stomp.connectAsync("ws://" + Game.con().getAddress() + "/ws", handshakeHeaders, stompHeaders, handler);
    }

    public boolean isConnected() {
        return session != null && session.isConnected();
    }

    public <T> void subscribe(String endpoint, ConnectionCallback<T> callback) {
        if(!isConnected()) {
            callback.failed("Not connected");
            return;
        }
        subscriptions.put(endpoint, session.subscribe(endpoint, new CallbackFrameHandler<>(callback)));
    }

    public void unsubscribe(String endpoint) {
        if(subscriptions.containsKey(endpoint)) subscriptions.get(endpoint).unsubscribe();
    }

    public <T> void send(String endpoint, T data, EmptyCallback callback) {
        StompSession.Receiptable receipt = session.send(endpoint, Game.json().toJson(data));
        receipt.addReceiptTask(() -> callback.completed("success"));
        receipt.addReceiptLostTask(() -> callback.failed("Receipt lost"));
    }

    public void disconnect() {
        subscriptions.clear();
        if(isConnected()) {
            session.disconnect();
        }
        stomp.stop();
        session = null;
        connectCallback = null;
    }

    private class SessionHandler extends StompSessionHandlerAdapter {
        @Override
        public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
            SocketHandler.this.session = session;
            session.setAutoReceipt(true);
            connectCallback.completed("success");
        }

        @Override
        public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
            connectCallback.failed(exception.getMessage());
        }

        @Override
        public void handleTransportError(StompSession session, Throwable exception) {
            connectCallback.failed(exception.getMessage());
        }
    }

    private static class CallbackFrameHandler<T> implements StompFrameHandler {
        private final ConnectionCallback<T> callback;

        public CallbackFrameHandler(ConnectionCallback<T> callback) {
            this.callback = callback;
        }

        @Override
        public Type getPayloadType(StompHeaders headers) {
            return String.class;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            try {
                callback.completed(Game.json().fromJson(callback.getType(), (String) payload));
            } catch(Exception e) {
                callback.failed(e.getMessage());
            }
        }
    }
}
