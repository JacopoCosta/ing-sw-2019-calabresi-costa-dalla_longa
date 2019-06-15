package it.polimi.ingsw.model.exceptions;

import it.polimi.ingsw.model.powerups.PowerUp;

/**
 * This exception is thrown when attempting to instantiate a new {@link PowerUp} whose type is not in the
 * enumeration of possible {@link PowerUp} types. It is declared as a runtime exception because its main
 * cause are spelling mistakes in the JSON files, and attempting to run an application in such conditions
 * makes no sense.
 * @see it.polimi.ingsw.model.powerups.PowerUp
 * @see it.polimi.ingsw.model.powerups.PowerUpType
 */
public class InvalidPowerUpTypeException extends RuntimeException {
    public InvalidPowerUpTypeException(String message) {
        super(message);
    }
}
