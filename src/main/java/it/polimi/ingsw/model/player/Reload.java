package it.polimi.ingsw.model.player;

/**
 * The reload activity allows a player to reload as many weapons as they can afford to.
 */
class Reload extends Activity {

    /**
     * This is the only constructor.
     */
    Reload() {
        this.type = ActivityType.RELOAD;
    }
}
