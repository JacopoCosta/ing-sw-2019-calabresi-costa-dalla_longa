package it.polimi.ingsw.model.exceptions;

/**
 * This exception is thrown when attempting to perform a transaction in which the buyer does not have enough
 * ammo cubes of each colour to afford the cost of the transaction.
 * @see it.polimi.ingsw.model.ammo.AmmoCubes
 */
public class CannotAffordException extends Exception {
    public CannotAffordException(String message) {
        super(message);
    }
}
