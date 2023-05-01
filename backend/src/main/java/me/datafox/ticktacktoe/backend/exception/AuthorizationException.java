package me.datafox.ticktacktoe.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author datafox
 */
@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "Not authorized")
public class AuthorizationException extends RuntimeException {
}
