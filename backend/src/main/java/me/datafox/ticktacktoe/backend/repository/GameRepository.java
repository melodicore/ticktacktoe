package me.datafox.ticktacktoe.backend.repository;

import me.datafox.ticktacktoe.backend.model.Game;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.util.Streamable;

/**
 * @author datafox
 */
public interface GameRepository extends MongoRepository<Game, String> {
    Page<Game> findByFinished(boolean finished, Pageable pageable);

    Streamable<Game> findByFinished(boolean finished);
}
