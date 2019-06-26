package it.polimi.ingsw.model.player;

import it.polimi.ingsw.model.weaponry.Weapon;
import it.polimi.ingsw.model.weaponry.effects.Damage;
import it.polimi.ingsw.model.weaponry.effects.Mark;

/**
 * The {@code Shoot} {@link Activity} allows a {@link Player} to use a loaded {@link Weapon} in their possession
 * to {@link Damage}, {@link Mark} and/or {@link Move} one or more of their opponents.
 */
class Shoot extends Activity {
    /**
     * This is the only constructor.
     */
    Shoot() {
        this.type = ActivityType.SHOOT;
    }
}
