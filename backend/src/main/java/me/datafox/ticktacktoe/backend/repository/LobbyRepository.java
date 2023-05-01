package me.datafox.ticktacktoe.backend.repository;

import me.datafox.ticktacktoe.backend.model.Lobby;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author datafox
 */
public interface LobbyRepository extends MongoRepository<Lobby, String> {
    Page<Lobby> findByFull(boolean full, Pageable paging);
}
