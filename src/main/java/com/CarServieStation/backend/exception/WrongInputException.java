package com.CarServieStation.backend.exception;

public class WrongInputException extends IllegalStateException{

    public WrongInputException(String message) {
        super(message);
    }
}