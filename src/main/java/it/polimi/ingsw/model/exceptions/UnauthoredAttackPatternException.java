package it.polimi.ingsw.model.exceptions;

/**
 * This exception is thrown when attempting to use an attack pattern without setting the author player first.
 * It is declared as runtime exception since the main cause of such scenario would be a flaw in the game logic,
 * making it pointless to keep the app running in these unstable conditions.
 * @see it.polimi.ingsw.model.weaponry.AttackPattern
 */
public class UnauthoredAttackPatternException extends RuntimeException {
    public UnauthoredAttackPatternException(String message) {
        super(message);
    }
}
