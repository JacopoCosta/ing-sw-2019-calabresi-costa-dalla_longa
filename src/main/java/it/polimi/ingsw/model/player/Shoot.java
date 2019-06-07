package it.polimi.ingsw.model.player;

/**
 * The shoot activity allows a player to use a loaded weapon in their possession to damage, mark and/or move one or more of their opponents.
 */
class Shoot extends Activity {

    /**
     * This is the only constructor.
     */
    Shoot() {
        this.type = ActivityType.SHOOT;
    }
}
