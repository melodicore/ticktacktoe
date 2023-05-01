package me.datafox.ticktacktoe.backend.configuration;

import me.datafox.ticktacktoe.backend.service.GameService;
import me.datafox.ticktacktoe.backend.service.LobbyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

/**
 * @author datafox
 */
@Component
public class DisconnectConfig {
    @Autowired
    private LobbyService lobbyService;

    @Autowired
    private GameService gameService;

    @EventListener
    public void handleSessionDisconnectEvent(SessionDisconnectEvent event) {
        if(event.getUser() == null) return;
        handleDisconnect(event.getUser().getName());
    }

    private void handleDisconnect(String username) {
        //Disconnect player from all current lobbies and games
        lobbyService.disconnectIfConnected(username);
        gameService.disconnectIfConnected(username);
    }
}
