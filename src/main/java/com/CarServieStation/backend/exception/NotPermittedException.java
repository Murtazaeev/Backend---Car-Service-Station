package com.CarServieStation.backend.exception;

public class NotPermittedException extends IllegalStateException {

    public NotPermittedException(String message) {
        super(message);
    }
}
