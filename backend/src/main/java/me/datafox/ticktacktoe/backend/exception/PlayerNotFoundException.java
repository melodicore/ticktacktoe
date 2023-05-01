package me.datafox.ticktacktoe.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author datafox
 */
@ResponseStatus(value = HttpStatus.CONFLICT, reason = "Player not found")
public class PlayerNotFoundException extends ConflictException {
}
