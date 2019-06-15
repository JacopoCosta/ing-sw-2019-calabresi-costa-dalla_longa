package it.polimi.ingsw.model.player;

import it.polimi.ingsw.model.weaponry.Weapon;

/**
 * The {@link Reload} {@link Activity} allows a {@link Player} to reload as many {@link Weapon}s as they can afford to.
 */
public class Reload extends Activity {

    /**
     * This is the only constructor.
     */
    Reload() {
        this.type = ActivityType.RELOAD;
    }
}
