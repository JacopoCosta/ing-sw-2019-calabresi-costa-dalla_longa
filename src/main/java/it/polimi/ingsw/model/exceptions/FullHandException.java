package it.polimi.ingsw.model.exceptions;

import it.polimi.ingsw.model.powerups.PowerUp;
import it.polimi.ingsw.model.weaponry.Weapon;

/**
 * This exception is thrown after giving a player a card such that the total number of cards of that type
 * in the player's hand exceeds a set threshold.
 * @see it.polimi.ingsw.model.player.Player#givePowerUp(PowerUp)
 * @see it.polimi.ingsw.model.player.Player#giveWeapon(Weapon)
 */
public class FullHandException extends Exception {
    public FullHandException(String message) {
        super(message);
    }
}
