package me.datafox.ticktacktoe.backend.controller;

import me.datafox.ticktacktoe.api.Constants;
import me.datafox.ticktacktoe.api.PlayerDto;
import me.datafox.ticktacktoe.api.PlayerRankDto;
import me.datafox.ticktacktoe.backend.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

/**
 * @author datafox
 */
@RestController
public class PlayerController {
    @Autowired
    private PlayerService playerService;

    @RequestMapping("/")
    public ResponseEntity<String> index() {
        return ResponseEntity.ok("The server is online");
    }

    @RequestMapping("/version")
    public ResponseEntity<String> version() {
        return ResponseEntity.ok(Constants.API_VERSION);
    }

    @RequestMapping("/who")
    public ResponseEntity<PlayerDto> who(Principal principal) {
        return ResponseEntity.ok(playerService.getPlayer(principal.getName()));
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody PlayerDto player) {
        if(!playerService.checkUsernameAvailability(player)) return new ResponseEntity<>(HttpStatusCode.valueOf(409));
        playerService.savePlayer(player);
        return ResponseEntity.ok("success");
    }

    @PostMapping("/modify")
    public ResponseEntity<PlayerDto> modify(@RequestBody PlayerDto player, Principal principal) {
        return ResponseEntity.ok(playerService.modifyPlayer(principal.getName(), player));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login() {
        return ResponseEntity.ok("login");
    }

    @GetMapping("/login/failed")
    public ResponseEntity<String> loginFail() {
        return new ResponseEntity<>(HttpStatusCode.valueOf(401));
    }

    @GetMapping("/player/{username}/rank")
    public ResponseEntity<PlayerRankDto> getRank(@PathVariable String username) {
        return ResponseEntity.ok(playerService.getPlayerRank(username));
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<List<PlayerRankDto>> getLeaderboard(@RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(playerService.getLeaderboard(page));
    }
}
