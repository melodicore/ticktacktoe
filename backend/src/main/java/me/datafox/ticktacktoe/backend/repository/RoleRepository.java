package me.datafox.ticktacktoe.backend.repository;

import me.datafox.ticktacktoe.backend.model.Role;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author datafox
 */
public interface RoleRepository extends MongoRepository<Role, String> {
}
