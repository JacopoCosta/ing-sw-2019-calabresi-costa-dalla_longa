package it.polimi.ingsw.model.player;

import it.polimi.ingsw.model.cell.Cell;

public class Grab extends Activity {

    public Grab() {
        this.type = ActivityType.GRAB;
    }

    @Override
    public void perform(Player player) {
        Cell cell = player.getPosition();


    }
}
