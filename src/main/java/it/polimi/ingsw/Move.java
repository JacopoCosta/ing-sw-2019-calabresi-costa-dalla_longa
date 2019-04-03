package it.polimi.ingsw;

public class Move extends Activity {

    private int maxDistance;
    private Cell destination;

    public Move(int maxDistance) {
        this.maxDistance = maxDistance;
    }

    public void setDestination(Cell destination) {
        this.destination = destination;
    }

    @Override
    public void perform(Player player) {

    }
}
