package it.polimi.ingsw.model.exceptions;

import it.polimi.ingsw.model.board.Deck;

/**
 * This exception is thrown when attempting to discard to a {@link Deck} from which the first card has not been drawn yet.
 *
 * @see Deck
 */
public class CannotDiscardFirstCardOfDeckException extends Exception {
    public CannotDiscardFirstCardOfDeckException(String message) {
        super(message);
    }
}
