package it.polimi.ingsw.model.exceptions;

import it.polimi.ingsw.model.Game;

/**
 * This exception is thrown when attempting to load a saved {@link Game} whose "valid" flag is unset.
 */
public class InvalidSaveStateException extends Exception {
    public InvalidSaveStateException(String message) {
        super(message);
    }
}
