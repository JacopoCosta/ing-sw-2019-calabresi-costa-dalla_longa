package it.polimi.ingsw.model.exceptions;

/**
 * This exception is thrown when attempting to get a more specific property from a constraint, than the one
 * it was defined on. For example: distance is a property defined on cells, but can be extended to players,
 * since each player uniquely identifies a cell with their position. However it cannot be extended to rooms, since
 * it is not true that each room uniquely identifies a cell, and in such case this exception would be thrown.
 * @see it.polimi.ingsw.model.weaponry.targets.Target
 * @see it.polimi.ingsw.model.weaponry.constraints.Constraint
 */
public class TargetInheritanceException extends RuntimeException {
    public TargetInheritanceException(String message) {
        super(message);
    }
}
