package it.polimi.ingsw.model.exceptions;

public class CorruptedDeckException extends RuntimeException {
    public CorruptedDeckException(String message) {
        super(message);
    }
}
