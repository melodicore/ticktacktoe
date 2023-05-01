package me.datafox.ticktacktoe.backend.controller;

import me.datafox.ticktacktoe.api.GameDto;
import me.datafox.ticktacktoe.api.MoveDto;
import me.datafox.ticktacktoe.backend.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

/**
 * @author datafox
 */
@Controller
public class GameController {
    @Autowired
    private GameService gameService;

    @GetMapping("/game/{gameId}/get")
    public ResponseEntity<GameDto> getGame(@PathVariable String gameId) {
        return ResponseEntity.ok(gameService.getGame(gameId));
    }

    @GetMapping("/game/get")
    public ResponseEntity<List<GameDto>> getGames(@RequestParam(defaultValue = "0") int mode, @RequestParam(defaultValue = "0") int page) {
        List<GameDto> list;
        switch(mode) {
            case 1 -> list = gameService.getGamesByFinished(true, page);
            case 2 -> list = gameService.getGamesByFinished(false, page);
            default -> list = gameService.getGames(page);
        }
        return ResponseEntity.ok(list);
    }

    @PostMapping("/game/remove")
    public ResponseEntity<String> removeGame(@RequestBody GameDto game) {
        gameService.removeGame(game);
        return ResponseEntity.ok("success");
    }

    @MessageMapping("/game/{gameId}/move")
    @SendTo("/out/game/{gameId}")
    public GameDto addMove(@DestinationVariable String gameId, @RequestBody MoveDto move, Principal principal) {
        return gameService.addMove(gameId, principal.getName(), move);
    }

    @MessageMapping("/game/{gameId}/abandon")
    @SendTo("/out/game/{gameId}")
    public GameDto abandon(@DestinationVariable String gameId, Principal principal) {
        return gameService.abandon(gameId, principal.getName());
    }
}
