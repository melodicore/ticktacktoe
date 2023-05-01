package me.datafox.ticktacktoe.backend.repository;

import me.datafox.ticktacktoe.backend.model.Player;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

/**
 * @author datafox
 */
public interface PlayerRepository extends MongoRepository<Player, String> {
    boolean existsByUsername(String username);
    Optional<Player> findByUsername(String username);
}
