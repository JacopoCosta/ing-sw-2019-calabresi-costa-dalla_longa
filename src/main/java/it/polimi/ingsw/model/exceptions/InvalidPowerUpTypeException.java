package it.polimi.ingsw.model.exceptions;

/**
 * This exception is thrown when attempting to instantiate a new power-up whose type is not in the
 * enumeration of possible power-up types. It is declared as a runtime exception because its main
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
