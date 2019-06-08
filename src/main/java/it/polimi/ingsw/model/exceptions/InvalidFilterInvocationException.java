package it.polimi.ingsw.model.exceptions;

/**
 * This exception is thrown when attempting to verify a constraint on an object on
 * which such constraint has no definition. It is declared as a runtime exception because
 * attempting to do so implies flaws in the game logic and in normal conditions such scenario
 * should never happen anyway.
 * @see it.polimi.ingsw.model.weaponry.constraints.Constraint
 */
public class InvalidFilterInvocationException extends RuntimeException {
    public InvalidFilterInvocationException(String message) {
        super(message);
    }
}
