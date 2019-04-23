package it.polimi.ingsw.player;

import it.polimi.ingsw.board.cell.Cell;

public class Move extends Activity {

    private int maxDistance;
    private Cell destination;

    public Move(int maxDistance) {
        this.type = ActivityType.MOVE;
        this.maxDistance = maxDistance;
    }

    public void setDestination(Cell destination) {
        this.destination = destination;
    }

    @Override
    public void perform(Player player) {
        player.setPosition(this.destination);
    }
}
