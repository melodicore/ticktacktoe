package me.datafox.ticktacktoe.backend.service;

import me.datafox.ticktacktoe.api.PlayerDto;
import me.datafox.ticktacktoe.api.PlayerRankDto;
import me.datafox.ticktacktoe.backend.exception.PlayerNotFoundException;
import me.datafox.ticktacktoe.backend.model.Game;
import me.datafox.ticktacktoe.backend.model.Player;
import me.datafox.ticktacktoe.backend.model.Role;
import me.datafox.ticktacktoe.backend.repository.GameRepository;
import me.datafox.ticktacktoe.backend.repository.PlayerRepository;
import me.datafox.ticktacktoe.backend.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author datafox
 */
@Service
public class PlayerService {
    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean checkUsernameAvailability(PlayerDto playerDto) {
        return !playerRepository.existsByUsername(playerDto.getUsername());
    }

    public void savePlayer(PlayerDto playerDto) {
        Player player = Player
                .builder()
                .id(UUID.randomUUID().toString())
                .username(playerDto.getUsername())
                .nickname(playerDto.getNickname())
                .password(passwordEncoder.encode(playerDto.getPassword()))
                .color(playerDto.getColor())
                .build();
        Role role = createRoleIfNotFound("ROLE_USER");
        player.getRoles().add(role);
        role.getPlayers().add(player);
        roleRepository.save(role);
        playerRepository.save(player);
    }

    public PlayerRankDto getPlayerRank(String username) {
        return getPlayerRank(playerRepository.findByUsername(username).orElseThrow(PlayerNotFoundException::new));
    }

    public PlayerRankDto getPlayerRank(Player player) {
        List<Game> games = gameRepository.findByFinished(true).filter(g -> g.getPlayers().containsValue(player)).toList();
        long wins = games.stream().filter(g -> g.getCurrentPlayer() != null && g.getCurrentPlayer().equals(player)).count();
        long losses = games.stream().filter(g -> g.getCurrentPlayer() != null && !g.getCurrentPlayer().equals(player)).count();
        long draws = games.stream().filter(g -> g.getCurrentPlayer() == null).count();
        float ratio = (float) wins / losses;
        return PlayerRankDto
                .builder()
                .player(mapToPlayerDto(player))
                .wins(wins)
                .losses(losses)
                .draws(draws)
                .ratio(ratio)
                .build();
    }

    public List<PlayerRankDto> getLeaderboard(int page) {
        final long[] rankCounter = {1};
        List<PlayerRankDto> list = playerRepository
                .findAll()
                .stream()
                .map(this::getPlayerRank)
                .sorted(Comparator.comparing(PlayerRankDto::getRatio).reversed())
                .peek(rank -> rank.setRank(rankCounter[0]++))
                .toList();
        int size = list.size();
        int firstIndex = page * 6;
        if(firstIndex >= size) return List.of();
        if(firstIndex == 0 && size <= 12) return list;
        return IntStream.range(firstIndex, firstIndex + 12).filter(n -> n < size).mapToObj(list::get).toList();
    }

    public PlayerDto mapToPlayerDto(Player player) {
        if(player == null) return null;
        return PlayerDto
                .builder()
                .username(player.getUsername())
                .nickname(player.getNickname())
                .color(player.getColor())
                .build();
    }

    public TreeMap<String,PlayerDto> mapToPlayerDto(TreeMap<String, Player> map) {
        return new TreeMap<>(map.entrySet().stream()
                .map(entry -> Map.entry(entry.getKey(), mapToPlayerDto(entry.getValue())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }

    private Role createRoleIfNotFound(String id) {
        return roleRepository.findById(id).orElseGet(() -> createRole(id));
    }

    private Role createRole(String id) {
        Role role = Role.builder().id(id).build();
        roleRepository.save(role);
        return role;
    }

    public PlayerDto getPlayer(String username) {
        return mapToPlayerDto(playerRepository.findByUsername(username).orElseThrow(PlayerNotFoundException::new));
    }

    public PlayerDto modifyPlayer(String username, PlayerDto playerDto) {
        Player player = playerRepository.findByUsername(username).orElseThrow(PlayerNotFoundException::new);
        player.setNickname(playerDto.getNickname());
        if(playerDto.getPassword() != null) player.setPassword(passwordEncoder.encode(playerDto.getPassword()));
        player.setColor(playerDto.getColor());
        playerRepository.save(player);
        return mapToPlayerDto(player);
    }
}
