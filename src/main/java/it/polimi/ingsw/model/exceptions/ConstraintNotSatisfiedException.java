package it.polimi.ingsw.model.exceptions;

public class ConstraintNotSatisfiedException extends Exception {
    public ConstraintNotSatisfiedException(String message) {
        super(message);
    }
}
