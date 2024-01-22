package com.CarServieStation.backend.exception;

public class AlreadyAssignedException extends IllegalStateException {
    public AlreadyAssignedException(String message) {
        super(message);
    }
}