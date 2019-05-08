package it.polimi.ingsw.model.player;

import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.exceptions.InvalidMoveException;

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
    public void perform(Player player) throws InvalidMoveException {
        if(player.getPosition().distance(destination) > maxDistance)
            throw new InvalidMoveException("The selected cell is too far away.");
        player.setPosition(this.destination);
    }
}
