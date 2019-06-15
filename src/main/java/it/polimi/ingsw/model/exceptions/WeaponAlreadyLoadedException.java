package it.polimi.ingsw.model.exceptions;

import it.polimi.ingsw.model.player.Reload;
import it.polimi.ingsw.model.weaponry.Weapon;

/**
 * This exception is thrown when attempting to {@link Reload} a {@link Weapon} that is already reloaded.
 * Since attempting such action can only be linked with flaws in the game logic, it would
 * be unwise to keep the program running after such event, hence this is a runtime exception.
 * @see Weapon#reload()
 */
public class WeaponAlreadyLoadedException extends RuntimeException {
    public WeaponAlreadyLoadedException(String message) {
        super(message);
    }
}
