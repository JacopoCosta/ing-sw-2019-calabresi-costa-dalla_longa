package it.polimi.ingsw.model.exceptions;

/**
 * This exception is thrown when attempting to discard to a deck from which the first card has not been drawn yet.
 * @see it.polimi.ingsw.model.board.Deck
 */
public class CannotDiscardFirstCardOfDeckException extends Exception {
    public CannotDiscardFirstCardOfDeckException(String message) {
        super(message);
    }
}
