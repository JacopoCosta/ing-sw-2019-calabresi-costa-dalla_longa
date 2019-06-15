package it.polimi.ingsw.model.exceptions;

import it.polimi.ingsw.model.board.Deck;

/**
 * This exception is thrown when attempting to draw from a {@link Deck} without cards.
 */
public class EmptyDeckException extends Exception {
    public EmptyDeckException(String message) {
        super(message);
    }
}
