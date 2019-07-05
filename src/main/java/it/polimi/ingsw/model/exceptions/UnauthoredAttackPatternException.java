package it.polimi.ingsw.model.exceptions;

import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.weaponry.AttackPattern;

/**
 * This exception is thrown when attempting to use an {@link AttackPattern} without setting the author {@link Player} first.
 * It is declared as runtime exception since the main cause of such scenario would be a flaw in the game logic,
 * making it pointless to keep the app running in these unstable conditions.
 *
 * @see AttackPattern
 */
public class UnauthoredAttackPatternException extends RuntimeException {
    public UnauthoredAttackPatternException(String message) {
        super(message);
    }
}
