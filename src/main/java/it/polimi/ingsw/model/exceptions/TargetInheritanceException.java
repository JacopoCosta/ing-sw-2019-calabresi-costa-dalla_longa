package it.polimi.ingsw.model.exceptions;

import it.polimi.ingsw.model.board.Room;
import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.weaponry.constraints.Constraint;
import it.polimi.ingsw.model.weaponry.targets.Target;

/**
 * This exception is thrown when attempting to get a more specific property from a {@link Constraint}, than the one
 * it was defined on. For example: distance is a property defined on {@link Cell}s, but can be extended to {@link Player}s,
 * since each {@link Player} uniquely identifies a {@link Cell} with their position. However it cannot be extended to {@link Room}s, since
 * it is not true that each {@link Room} uniquely identifies a {@link Cell}, and in such case this exception would be thrown.
 * @see Target
 */
public class TargetInheritanceException extends RuntimeException {
    public TargetInheritanceException(String message) {
        super(message);
    }
}
