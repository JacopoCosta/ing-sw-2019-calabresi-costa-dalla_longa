package it.polimi.ingsw.model.exceptions;

/**
 * This exception is thrown when attempting to instantiate a new target whose type is not in the
 * enumeration of possible target types. It is declared as a runtime exception because its main
 * cause are spelling mistakes in the JSON files, and attempting to run an application in such conditions
 * makes no sense.
 * @see it.polimi.ingsw.model.weaponry.targets.Target
 * @see it.polimi.ingsw.model.weaponry.targets.TargetType
 */
public class InvalidTargetTypeException extends RuntimeException {
    public InvalidTargetTypeException(String message) {
        super(message);
    }
}
