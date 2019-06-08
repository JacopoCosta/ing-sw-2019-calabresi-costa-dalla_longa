package it.polimi.ingsw.model.exceptions;

/**
 * This exception is thrown when attempting to load a saved game whose "valid" flag is unset.
 */
public class InvalidSaveStateException extends Exception {
    public InvalidSaveStateException(String message) {
        super(message);
    }
}
