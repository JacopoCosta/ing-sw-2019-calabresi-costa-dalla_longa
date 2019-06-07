package it.polimi.ingsw.model.player;

import it.polimi.ingsw.model.cell.Cell;

/**
 * The move activity is used to change a player's position to a new position,
 * so long as the distance between the two does not exceed a fixed limit.
 * @see it.polimi.ingsw.model.cell.Cell#distance(Cell)
 */
public class Move extends Activity {
    /**
     * The maximum allowed distance between source cell and destination cell.
     */
    private int maxDistance;

    /**
     * This is the only constructor. Instances of this class are not mutable and the distance is set once and cannot be changed.
     * @param maxDistance The maximum allowed distance between source cell and destination cell.
     */
    Move(int maxDistance) {
        this.maxDistance = maxDistance;
        this.type = ActivityType.MOVE;
    }

    /**
     * Returns the move's maximum distance.
     * @return The maximum allowed distance between source cell and destination cell.
     */
    public int getMaxDistance() {
        return maxDistance;
    }
}
