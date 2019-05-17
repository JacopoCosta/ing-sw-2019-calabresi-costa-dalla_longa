package it.polimi.ingsw.model.player;

public class Move extends Activity {
    private int maxDistance;

    public Move(int maxDistance) {
        this.maxDistance = maxDistance;
        this.type = ActivityType.MOVE;
    }

    public int getMaxDistance() {
        return maxDistance;
    }
}
