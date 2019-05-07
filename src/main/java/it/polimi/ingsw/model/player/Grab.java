package it.polimi.ingsw.model.player;

import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.exceptions.CannotGrabException;

public class Grab extends Activity {

    public Grab() {
        this.type = ActivityType.GRAB;
    }

    @Override
    public void perform(Player player) throws CannotGrabException {
        Cell cell = player.getPosition();
    }
}
