package me.datafox.ticktacktoe.backend.controller;

import me.datafox.ticktacktoe.api.LobbyDto;
import me.datafox.ticktacktoe.backend.service.LobbyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

/**
 * @author datafox
 */
@Controller
public class LobbyController {
    @Autowired
    private LobbyService lobbyService;

    @GetMapping("/lobby/create")
    public ResponseEntity<LobbyDto> createLobby(Principal principal) {
        return ResponseEntity.ok(lobbyService.createLobby(principal.getName()));
    }

    @GetMapping("/lobby/get")
    public ResponseEntity<List<LobbyDto>> getLobbies(@RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(lobbyService.getFreeLobbies(page));
    }

    @GetMapping("/lobby/{lobbyId}/join")
    public ResponseEntity<LobbyDto> joinToLobby(@PathVariable String lobbyId, Principal principal) {
        return ResponseEntity.ok(lobbyService.addPlayerToLobby(lobbyId, principal.getName()));
    }

    @MessageMapping("/lobby/{lobbyId}/symbol")
    @SendTo("/out/lobby/{lobbyId}")
    public LobbyDto changeSymbol(@DestinationVariable String lobbyId, @RequestBody String symbol, Principal principal) {
        return lobbyService.changePlayerSymbol(lobbyId, principal.getName(), symbol);
    }

    @MessageMapping("/lobby/{lobbyId}/name")
    @SendTo("/out/lobby/{lobbyId}")
    public LobbyDto changeName(@DestinationVariable String lobbyId, @RequestBody String name, Principal principal) {
        return lobbyService.changeName(lobbyId, principal.getName(), name);
    }

    @MessageMapping("/lobby/{lobbyId}/width")
    @SendTo("/out/lobby/{lobbyId}")
    public LobbyDto changeWidth(@DestinationVariable String lobbyId, @RequestBody Integer width, Principal principal) {
        return lobbyService.changeWidth(lobbyId, principal.getName(), width);
    }

    @MessageMapping("/lobby/{lobbyId}/height")
    @SendTo("/out/lobby/{lobbyId}")
    public LobbyDto changeHeight(@DestinationVariable String lobbyId, @RequestBody Integer height, Principal principal) {
        return lobbyService.changeHeight(lobbyId, principal.getName(), height);
    }

    @MessageMapping("/lobby/{lobbyId}/condition")
    @SendTo("/out/lobby/{lobbyId}")
    public LobbyDto changeCondition(@DestinationVariable String lobbyId, @RequestBody Integer condition, Principal principal) {
        return lobbyService.changeCondition(lobbyId, principal.getName(), condition);
    }

    @MessageMapping("/lobby/{lobbyId}/fall")
    @SendTo("/out/lobby/{lobbyId}")
    public LobbyDto changeCondition(@DestinationVariable String lobbyId, @RequestBody Boolean fall, Principal principal) {
        return lobbyService.changeFallMode(lobbyId, principal.getName(), fall);
    }

    @MessageMapping("/lobby/{lobbyId}/start")
    @SendTo("/out/lobby/{lobbyId}")
    public LobbyDto startGame(@DestinationVariable String lobbyId, Principal principal) {
        return lobbyService.startGame(lobbyId, principal.getName());
    }
}
