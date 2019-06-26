package it.polimi.ingsw.model.player;

import it.polimi.ingsw.model.ammo.AmmoCubes;
import it.polimi.ingsw.model.cell.AmmoCell;
import it.polimi.ingsw.model.cell.SpawnCell;
import it.polimi.ingsw.model.weaponry.Weapon;

/**
 * The {@code Grab} {@link Activity} is used to collect {@link AmmoCubes} from an {@link AmmoCell}
 * or buy a {@link Weapon} from a {@link SpawnCell}'s {@link Weapon} shop.
 */
public class Grab extends Activity {
    /**
     * This is the only constructor.
     */
    Grab() {
        this.type = ActivityType.GRAB;
    }
}
