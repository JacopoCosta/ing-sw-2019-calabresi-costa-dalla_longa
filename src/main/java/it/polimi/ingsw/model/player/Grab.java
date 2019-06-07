package it.polimi.ingsw.model.player;

/**
 * The grab activity is used to collect ammo from an ammo crate or buy a weapon from a spawn point's weapon shop.
 */
class Grab extends Activity {
    /**
     * This is the only constructor.
     */
    Grab() {
        this.type = ActivityType.GRAB;
    }
}
