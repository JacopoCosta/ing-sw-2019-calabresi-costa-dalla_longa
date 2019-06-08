package it.polimi.ingsw.model.exceptions;

/**
 * This exception is thrown when attempting to draw from a deck without cards.
 * @see it.polimi.ingsw.model.board.Deck
 */
public class EmptyDeckException extends Exception {
    public EmptyDeckException(String message) {
        super(message);
    }
}
