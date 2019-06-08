package it.polimi.ingsw.model.exceptions;

/**
 * This exception is thrown when attempting to instantiate a new constraint whose type is not in the
 * enumeration of possible constraint types. It is declared as a runtime exception because its main
 * cause are spelling mistakes in the JSON files, and attempting to run an application in such conditions
 * makes no sense.
 * @see it.polimi.ingsw.model.weaponry.constraints.Constraint
 * @see it.polimi.ingsw.model.weaponry.constraints.ConstraintType
 */
public class InvalidConstraintTypeException extends RuntimeException {
    public InvalidConstraintTypeException(String message) {
        super(message);
    }
}
