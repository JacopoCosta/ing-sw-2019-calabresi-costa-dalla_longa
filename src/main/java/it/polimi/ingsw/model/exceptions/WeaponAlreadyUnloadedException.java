package it.polimi.ingsw.model.exceptions;

import it.polimi.ingsw.model.weaponry.Weapon;

/**
 * This exception is thrown when attempting to unload a {@link Weapon} that is already not loaded.
 * Since attempting such action can only be linked with flaws in the game logic, it would
 * be unwise to keep the program running after such event, hence this is a runtime exception.
 * @see Weapon#unload()
 */
public class WeaponAlreadyUnloadedException extends RuntimeException {
    public WeaponAlreadyUnloadedException(String message) {
        super(message);
    }
}
