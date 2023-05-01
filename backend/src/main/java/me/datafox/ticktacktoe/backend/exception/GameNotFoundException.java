package me.datafox.ticktacktoe.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author datafox
 */
@ResponseStatus(value = HttpStatus.CONFLICT, reason = "Game not found")
public class GameNotFoundException extends GameConflictException {
}
