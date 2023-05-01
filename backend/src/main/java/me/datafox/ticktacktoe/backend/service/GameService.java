package me.datafox.ticktacktoe.backend.service;

import me.datafox.ticktacktoe.api.GameDto;
import me.datafox.ticktacktoe.api.MoveDto;
import me.datafox.ticktacktoe.api.Symbols;
import me.datafox.ticktacktoe.backend.exception.GameConflictException;
import me.datafox.ticktacktoe.backend.exception.GameNotFoundException;
import me.datafox.ticktacktoe.backend.exception.PlayerNotFoundException;
import me.datafox.ticktacktoe.backend.model.Game;
import me.datafox.ticktacktoe.backend.model.Lobby;
import me.datafox.ticktacktoe.backend.model.Player;
import me.datafox.ticktacktoe.backend.repository.GameRepository;
import me.datafox.ticktacktoe.backend.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * @author datafox
 */
@Service
public class GameService {
    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private SimpMessagingTemplate template;

    public void createGame(Lobby lobby) {
        //Create game from lobby
        Game game = Game
                .builder()
                .id(lobby.getId())
                .timestamp(System.currentTimeMillis())
                .name(lobby.getName())
                .width(lobby.getWidth())
                .height(lobby.getHeight())
                .winCondition(lobby.getWinCondition())
                .fallMode(lobby.isFallMode())
                .board(new String[lobby.getWidth() * lobby.getHeight()])
                .players(lobby.getPlayers())
                .currentPlayer(lobby.getPlayers().get(Symbols.X))
                .build();
        gameRepository.save(game);
    }

    public GameDto getGame(String gameId) {
        return mapToGameDto(gameRepository.findById(gameId).orElseThrow(GameNotFoundException::new));
    }

    public void removeGame(GameDto game) {
        gameRepository.delete(gameRepository.findById(game.getId()).orElseThrow(GameNotFoundException::new));
    }

    public GameDto addMove(String gameId, String username, MoveDto move) {
        Game game = gameRepository.findById(gameId).orElseThrow(GameNotFoundException::new);
        Player player = playerRepository.findByUsername(username).orElseThrow(PlayerNotFoundException::new);
        //Check if game is not finished
        if(game.isFinished()) throw new GameConflictException();
        //Check if it is current player's turn
        if(!game.getCurrentPlayer().equals(player)) throw new GameConflictException();
        //Check if the symbol determined in the move is current player's symbol
        if(!game.getCurrentPlayer().equals(game.getPlayers().get(move.getSymbol()))) throw new GameConflictException();
        //Set the move to the largest free y position if fall mode is on
        if(game.isFallMode()) move = toFallMove(game, move);
        //Get the array index from move's x and y coordinates
        int index = toIndex(move, game);
        //Register the move
        game.getMoves().add(move);
        game.getBoard()[index] = move.getSymbol();
        //Check if the game is finished with a win
        if(checkIfGameIsFinished(game)) game.setFinished(true);
        //Check if the game is finished with a draw
        else if(checkDraw(game.getBoard())) {
            game.setCurrentPlayer(null);
            game.setFinished(true);
        }
        //Change the current player if the game was not finished
        else setNextPlayer(game);
        gameRepository.save(game);
        return mapToGameDto(game);
    }

    public List<GameDto> getGames(int page) {
        Pageable paging = PageRequest.of(page, 6);
        Pageable paging2 = PageRequest.of(page + 1, 6);
        Stream<Game> games = Stream.concat(
                gameRepository.findAll(paging).stream(),
                gameRepository.findAll(paging2).stream());
        return games.map(this::mapToGameDto).toList();
    }

    public List<GameDto> getGamesByFinished(boolean finished, int page) {
        Pageable paging = PageRequest.of(page, 6);
        Pageable paging2 = PageRequest.of(page + 1, 6);
        Stream<Game> games = Stream.concat(
                gameRepository.findByFinished(finished, paging).stream(),
                gameRepository.findByFinished(finished, paging2).stream());
        return games.map(this::mapToGameDto).toList();
    }

    public GameDto abandon(String gameId, String username) {
        Game game = gameRepository.findById(gameId).orElseThrow(GameNotFoundException::new);
        return abandon(game, username);
    }

    public GameDto abandon(Game game, String username) {
        game.setCurrentPlayer(game.getPlayers().values()
                .stream()
                .filter(player -> !player.getUsername().equals(username))
                .findFirst()
                .orElseThrow(PlayerNotFoundException::new));
        game.setFinished(true);
        gameRepository.save(game);
        return mapToGameDto(game);
    }

    public void disconnectIfConnected(String username) {
        Player player = playerRepository.findByUsername(username).orElseThrow(PlayerNotFoundException::new);
        gameRepository.findByFinished(false)
                .stream()
                .filter(g -> g.getPlayers().containsValue(player))
                .forEach(game ->
                        template.convertAndSend("/out/game/" + game.getId(), abandon(game, username)));
    }

    public GameDto mapToGameDto(Game game) {
        return GameDto
                .builder()
                .id(game.getId())
                .timestamp(game.getTimestamp())
                .name(game.getName())
                .finished(game.isFinished())
                .width(game.getWidth())
                .height(game.getHeight())
                .winCondition(game.getWinCondition())
                .fallMode(game.isFallMode())
                .board(game.getBoard())
                .moves(game.getMoves())
                .players(playerService.mapToPlayerDto(game.getPlayers()))
                .currentPlayer(playerService.mapToPlayerDto(game.getCurrentPlayer()))
                .build();
    }

    public List<String> getSymbols() {
        return new ArrayList<>(List.of(Symbols.ALL));
    }

    private MoveDto toFallMove(Game game, MoveDto move) {
        int y = move.getY();
        while(y < game.getHeight() - 1) {
            if(game.getBoard()[toIndex(move.getX(), y + 1, game.getWidth())] == null) y++;
            else break;
        }
        return MoveDto
                .builder()
                .x(move.getX())
                .y(y)
                .symbol(move.getSymbol())
                .build();
    }

    private boolean checkIfGameIsFinished(Game game) {
        return checkRows(game.getBoard(), game.getWidth(), game.getHeight(), game.getWinCondition()) ||
                checkColumns(game.getBoard(), game.getWidth(), game.getHeight(), game.getWinCondition()) ||
                checkDiagonals(game.getBoard(), game.getWidth(), game.getHeight(), game.getWinCondition());
    }

    private boolean checkRows(String[] array, int width, int height, int condition) {
        for(int x = 0; x <= width - condition; x++) {
            next: for(int y = 0; y < height; y++) {
                //System.out.println("Starting check at x: " + x + ", y: " + y);
                String c = array[toIndex(x, y, width)];
                if(c == null) continue;
                //System.out.println("Found mark: " + c);
                for(int i = 1; i < condition; i++) {
                    //System.out.println("Checking next position x: " + x + ", y: " + y);
                    String c2 = array[toIndex(x + i, y, width)];
                    if(!c.equals(c2)) continue next;
                    //System.out.println("Found matching mark");
                }
                //System.out.println("Game finished!");
                return true;
            }
        }
        return false;
    }

    private boolean checkColumns(String[] array, int width, int height, int condition) {
        for(int x = 0; x < width; x++) {
            next: for(int y = 0; y <= height - condition; y++) {
                //System.out.println("Starting check at x: " + x + ", y: " + y);
                String c = array[toIndex(x, y, width)];
                if(c == null) continue;
                //System.out.println("Found mark: " + c);
                for(int i = 1; i < condition; i++) {
                    //System.out.println("Checking next position x: " + x + ", y: " + y);
                    String c2 = array[toIndex(x, y + i, width)];
                    if(!c.equals(c2)) continue next;
                    //System.out.println("Found matching mark");
                }
                //System.out.println("Game finished!");
                return true;
            }
        }
        return false;
    }

    private boolean checkDiagonals(String[] array, int width, int height, int condition) {
        for(int x = 0; x <= width - condition; x++) {
            next: for(int y = 0; y <= height - condition; y++) {
                //System.out.println("Starting check at x: " + x + ", y: " + y);
                String c = array[toIndex(x, y, width)];
                if(c == null) continue;
                //System.out.println("Found mark: " + c);
                for(int i = 1; i < condition; i++) {
                    //System.out.println("Checking next position x: " + x + ", y: " + y);
                    String c2 = array[toIndex(x + i, y + i, width)];
                    if(!c.equals(c2)) continue next;
                    //System.out.println("Found matching mark");
                }
                //System.out.println("Game finished!");
                return true;
            }
        }
        for(int x = width - 1; x >= condition - 1; x--) {
            next: for(int y = 0; y <= height - condition; y++) {
                //System.out.println("Starting check at x: " + x + ", y: " + y);
                String c = array[toIndex(x, y, width)];
                if(c == null) continue;
                //System.out.println("Found mark: " + c);
                for(int i = 1; i < condition; i++) {
                    //System.out.println("Checking next position x: " + x + ", y: " + y);
                    String c2 = array[toIndex(x - i, y + i, width)];
                    if(!c.equals(c2)) continue next;
                    //System.out.println("Found matching mark");
                }
                //System.out.println("Game finished!");
                return true;
            }
        }
        return false;
    }

    private boolean checkDraw(String[] array) {
        return Arrays.stream(array).noneMatch(Objects::isNull);
    }

    private void setNextPlayer(Game game) {
        Player player = game.getCurrentPlayer();
        List<Player> list = new ArrayList<>(game.getPlayers().values());
        int index = (list.indexOf(player) + 1) % list.size();
        game.setCurrentPlayer(list.get(index));
    }

    private int toIndex(MoveDto move, Game game) {
        return toIndex(move.getX(), move.getY(), game.getWidth());
    }

    private int toIndex(int x, int y, int width) {
        return x + y * width;
    }
}
