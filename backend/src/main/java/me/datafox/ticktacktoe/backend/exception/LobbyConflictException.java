package me.datafox.ticktacktoe.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author datafox
 */
@ResponseStatus(value = HttpStatus.CONFLICT, reason = "Lobby conflict")
public class LobbyConflictException extends ConflictException {
}
