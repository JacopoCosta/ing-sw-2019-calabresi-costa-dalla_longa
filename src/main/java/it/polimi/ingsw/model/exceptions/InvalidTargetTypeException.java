package it.polimi.ingsw.model.exceptions;

import it.polimi.ingsw.model.weaponry.targets.Target;
import it.polimi.ingsw.model.weaponry.targets.TargetType;

/**
 * This exception is thrown when attempting to instantiate a new {@link Target} whose type is not in the
 * enumeration of possible {@link TargetType}s. It is declared as a runtime exception because its main
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
