package it.polimi.ingsw.model.exceptions;

import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.weaponry.targets.Target;
import it.polimi.ingsw.view.virtual.VirtualView;

/**
 * This exception is thrown (and caught) internally in the {@link VirtualView}, when it is impossible to provide
 * the {@link Player} with a valid list of {@link Target}s to choose for an attack.
 *
 * @see VirtualView
 */
public class NoValidTargetsException extends Exception {
    public NoValidTargetsException(String message) {
        super(message);
    }
}
