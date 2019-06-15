package it.polimi.ingsw.model.exceptions;

import it.polimi.ingsw.model.ammo.AmmoCubes;

/**
 * This exception is thrown when attempting to perform a transaction in which the buyer does not have enough
 * {@link AmmoCubes} of each colour to afford the cost of the transaction.
 */
public class CannotAffordException extends Exception {
    public CannotAffordException(String message) {
        super(message);
    }
}
