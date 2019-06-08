package it.polimi.ingsw.model.exceptions;

/**
 * This exception is thrown (and caught) internally in the {@code VirtualView}, when it is impossible to provide
 * the player with a valid list of targets to choose for an attack.
 * @see it.polimi.ingsw.view.virtual.VirtualView
 */
public class NoValidTargetsException extends Exception {
    public NoValidTargetsException(String message) {
        super(message);
    }
}
