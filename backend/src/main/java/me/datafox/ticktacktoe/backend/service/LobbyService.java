package me.datafox.ticktacktoe.backend.service;

import me.datafox.ticktacktoe.api.LobbyDto;
import me.datafox.ticktacktoe.api.Symbols;
import me.datafox.ticktacktoe.backend.exception.AuthorizationException;
import me.datafox.ticktacktoe.backend.exception.LobbyConflictException;
import me.datafox.ticktacktoe.backend.exception.LobbyNotFoundException;
import me.datafox.ticktacktoe.backend.exception.PlayerNotFoundException;
import me.datafox.ticktacktoe.backend.model.Lobby;
import me.datafox.ticktacktoe.backend.model.Player;
import me.datafox.ticktacktoe.backend.repository.LobbyRepository;
import me.datafox.ticktacktoe.backend.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * @author datafox
 */
@Service
public class LobbyService {
    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private LobbyRepository lobbyRepository;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private GameService gameService;

    @Autowired
    private MapService mapService;

    @Autowired
    private SimpMessagingTemplate template;

    public LobbyDto createLobby(String hostUsername) {
        Player host = playerRepository.findByUsername(hostUsername).orElseThrow(PlayerNotFoundException::new);
        Lobby lobby = Lobby
                .builder()
                .id(UUID.randomUUID().toString())
                .name(host.getNickname() + "'s game")
                .host(host)
                .players(new TreeMap<>(Map.of(Symbols.X, host)))
                .build();
        lobbyRepository.save(lobby);
        return mapToLobbyDto(lobby);
    }

    public LobbyDto addPlayerToLobby(String lobbyId, String username) {
        Lobby lobby = lobbyRepository.findById(lobbyId).orElseThrow(LobbyNotFoundException::new);
        if(lobby.isStarted()) throw new LobbyConflictException();
        if(lobby.getPlayers().size() == lobby.getPlayerCount()) throw new LobbyConflictException();
        Player player = playerRepository.findByUsername(username).orElseThrow(PlayerNotFoundException::new);
        lobby.getPlayers().put(getFirstFreeSymbol(lobby), player);
        if(lobby.getPlayers().size() == lobby.getPlayerCount()) lobby.setFull(true);
        lobbyRepository.save(lobby);
        LobbyDto lobbyDto = mapToLobbyDto(lobby);
        template.convertAndSend("/out/lobby/" + lobby.getId(), lobbyDto);
        return lobbyDto;
    }

    public LobbyDto changePlayerSymbol(String lobbyId, String username, String symbol) {
        Lobby lobby = lobbyRepository.findById(lobbyId).orElseThrow(LobbyNotFoundException::new);
        if(lobby.isStarted()) throw new LobbyConflictException();
        Map<Player, String> reverse = mapService.reverseMap(lobby.getPlayers());
        Player player = playerRepository.findByUsername(username).orElseThrow(PlayerNotFoundException::new);
        if(!reverse.containsKey(player)) throw new LobbyConflictException();
        String current = reverse.get(player);
        if(symbol.equals(current)) return mapToLobbyDto(lobby);
        if(lobby.getPlayers().get(symbol) == null) {
            lobby.getPlayers().remove(current);
            lobby.getPlayers().put(symbol, player);
            lobbyRepository.save(lobby);
            return mapToLobbyDto(lobby);
        }
        if(!player.getId().equals(lobby.getHost().getId())) throw new AuthorizationException();
        Player other = lobby.getPlayers().get(symbol);
        lobby.getPlayers().remove(symbol);
        lobby.getPlayers().remove(current);
        lobby.getPlayers().put(symbol, player);
        lobby.getPlayers().put(current, other);
        lobbyRepository.save(lobby);
        return mapToLobbyDto(lobby);
    }

    public LobbyDto changeName(String lobbyId, String username, String name) {
        if(name.length() > 16) throw new LobbyConflictException();
        Lobby lobby = lobbyRepository.findById(lobbyId).orElseThrow(LobbyNotFoundException::new);
        if(lobby.isStarted()) throw new LobbyConflictException();
        Player player = playerRepository.findByUsername(username).orElseThrow(PlayerNotFoundException::new);
        if(!player.getId().equals(lobby.getHost().getId())) throw new AuthorizationException();
        lobby.setName(name);
        lobbyRepository.save(lobby);
        return mapToLobbyDto(lobby);
    }

    public LobbyDto changeWidth(String lobbyId, String username, Integer width) {
        if(width < 3 || width > 10) throw new LobbyConflictException();
        Lobby lobby = lobbyRepository.findById(lobbyId).orElseThrow(LobbyNotFoundException::new);
        if(lobby.isStarted()) throw new LobbyConflictException();
        Player player = playerRepository.findByUsername(username).orElseThrow(PlayerNotFoundException::new);
        if(!player.equals(lobby.getHost())) throw new AuthorizationException();
        lobby.setWidth(width);
        lobbyRepository.save(lobby);
        return mapToLobbyDto(lobby);
    }

    public LobbyDto changeHeight(String lobbyId, String username, Integer height) {
        if(height < 3 || height > 10) throw new LobbyConflictException();
        Lobby lobby = lobbyRepository.findById(lobbyId).orElseThrow(LobbyNotFoundException::new);
        if(lobby.isStarted()) throw new LobbyConflictException();
        Player player = playerRepository.findByUsername(username).orElseThrow(PlayerNotFoundException::new);
        if(!player.getId().equals(lobby.getHost().getId())) throw new AuthorizationException();
        lobby.setHeight(height);
        lobbyRepository.save(lobby);
        return mapToLobbyDto(lobby);
    }

    public LobbyDto changeCondition(String lobbyId, String username, Integer condition) {
        if(condition < 3 || condition > 10) throw new LobbyConflictException();
        Lobby lobby = lobbyRepository.findById(lobbyId).orElseThrow(LobbyNotFoundException::new);
        if(lobby.isStarted()) throw new LobbyConflictException();
        if(condition > Math.min(lobby.getWidth(), lobby.getHeight())) throw new LobbyConflictException();
        Player player = playerRepository.findByUsername(username).orElseThrow(PlayerNotFoundException::new);
        if(!player.getId().equals(lobby.getHost().getId())) throw new AuthorizationException();
        lobby.setWinCondition(condition);
        lobbyRepository.save(lobby);
        return mapToLobbyDto(lobby);
    }

    public LobbyDto changeFallMode(String lobbyId, String username, Boolean fall) {
        Lobby lobby = lobbyRepository.findById(lobbyId).orElseThrow(LobbyNotFoundException::new);
        if(lobby.isStarted()) throw new LobbyConflictException();
        Player player = playerRepository.findByUsername(username).orElseThrow(PlayerNotFoundException::new);
        if(!player.getId().equals(lobby.getHost().getId())) throw new AuthorizationException();
        lobby.setFallMode(fall);
        lobbyRepository.save(lobby);
        return mapToLobbyDto(lobby);

    }

    public LobbyDto startGame(String lobbyId, String username) {
        Lobby lobby = lobbyRepository.findById(lobbyId).orElseThrow(LobbyNotFoundException::new);
        if(lobby.isStarted()) throw new LobbyConflictException();
        if(lobby.getPlayers().size() != lobby.getPlayerCount()) throw new LobbyConflictException();
        Player player = playerRepository.findByUsername(username).orElseThrow(PlayerNotFoundException::new);
        if(!player.getId().equals(lobby.getHost().getId())) throw new AuthorizationException();
        gameService.createGame(lobby);
        lobby.setStarted(true);
        lobbyRepository.delete(lobby);
        return mapToLobbyDto(lobby);
    }

    public List<LobbyDto> getFreeLobbies(int page) {
        Pageable paging = PageRequest.of(page, 6);
        Pageable paging2 = PageRequest.of(page + 1, 6);
        Stream<Lobby> lobbies = Stream.concat(
                lobbyRepository.findByFull(false, paging).stream(),
                lobbyRepository.findByFull(false, paging2).stream());
        return lobbies.map(this::mapToLobbyDto).toList();
    }

    public void disconnectIfConnected(String username) {
        Player player = playerRepository.findByUsername(username).orElseThrow(PlayerNotFoundException::new);
        List<Lobby> lobbies = lobbyRepository.findAll();
        lobbies.stream().filter(l -> l.getPlayers().containsValue(player)).forEach(new Consumer<Lobby>() {
            @Override
            public void accept(Lobby lobby) {
                lobby.getPlayers()
                        .entrySet()
                        .stream()
                        .filter(e -> e.getValue().equals(player))
                        .map(Map.Entry::getKey)
                        .toList()
                        .forEach(lobby.getPlayers()::remove);
                if(lobby.getPlayers().isEmpty()) lobbyRepository.delete(lobby);
                else {
                    lobby.setFull(false);
                    if(lobby.getHost().getUsername().equals(username)) lobby.setHost(new ArrayList<>(lobby.getPlayers().values()).get(0));
                    lobbyRepository.save(lobby);
                }
                template.convertAndSend("/out/lobby/" + lobby.getId(), mapToLobbyDto(lobby));
            }
        });
    }

    public LobbyDto mapToLobbyDto(Lobby lobby) {
        return LobbyDto
                .builder()
                .id(lobby.getId())
                .name(lobby.getName())
                .width(lobby.getWidth())
                .height(lobby.getHeight())
                .winCondition(lobby.getWinCondition())
                .playerCount(lobby.getPlayerCount())
                .fallMode(lobby.isFallMode())
                .host(playerService.mapToPlayerDto(lobby.getHost()))
                .players(playerService.mapToPlayerDto(lobby.getPlayers()))
                .started(lobby.isStarted()).build();
    }

    private String getFirstFreeSymbol(Lobby lobby) {
        List<String> symbols = gameService.getSymbols();
        symbols.removeAll(lobby.getPlayers().keySet());
        if(symbols.isEmpty()) return null;
        return symbols.get(0);
    }
}