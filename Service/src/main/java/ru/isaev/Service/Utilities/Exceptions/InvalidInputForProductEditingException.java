package ru.isaev.Service.Utilities.Exceptions;

public class InvalidInputForProductEditingException extends RuntimeException {
    public InvalidInputForProductEditingException(String message) {
        super(message);
    }
}
