package org.example.exception;

public class InvalidRouteException extends RuntimeException {
    public InvalidRouteException(String message) {
        super(message);
    }
}
