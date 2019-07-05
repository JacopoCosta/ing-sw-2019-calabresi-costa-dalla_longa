package it.polimi.ingsw.model.player;

import it.polimi.ingsw.model.cell.Cell;

/**
 * The {@code Move} {@link Activity} is used to change a {@link Player}'s position to a new position,
 * so long as the distance between the two does not exceed a fixed limit.
 *
 * @see Cell#distance(Cell)
 */
public class Move extends Activity {
    /**
     * The maximum allowed distance between source {@link Cell} and destination {@link Cell}.
     */
    private int maxDistance;

    /**
     * This is the only constructor. Instances of this class are not mutable and the distance is set once and cannot be changed.
     *
     * @param maxDistance The maximum allowed distance between source {@link Cell} and destination {@link Cell}.
     */
    Move(int maxDistance) {
        this.maxDistance = maxDistance;
        this.type = ActivityType.MOVE;
    }

    /**
     * Returns the {@code Move}'s {@link Move#maxDistance}.
     *
     * @return The maximum allowed distance between source {@link Cell} and destination {@link Cell}.
     */
    public int getMaxDistance() {
        return maxDistance;
    }
}
