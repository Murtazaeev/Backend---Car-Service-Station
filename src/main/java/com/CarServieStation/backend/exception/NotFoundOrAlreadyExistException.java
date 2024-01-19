package com.CarServieStation.backend.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotFoundOrAlreadyExistException extends RuntimeException {

    public NotFoundOrAlreadyExistException(String message) {
        super(message);
    }
}