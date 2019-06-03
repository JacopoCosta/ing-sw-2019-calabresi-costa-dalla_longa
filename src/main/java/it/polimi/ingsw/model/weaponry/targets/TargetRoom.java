package it.polimi.ingsw.model.weaponry.targets;

import it.polimi.ingsw.model.board.Room;
import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.exceptions.TargetInheritanceException;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.weaponry.constraints.Constraint;

import java.util.List;

public class TargetRoom extends Target {
    private Room room;

    TargetRoom(String message, List<Constraint> constraints) {
        this.message = message;
        this.constraints = constraints;
        this.type = TargetType.ROOM;
    }

    public void setRoom(Room room) {
        if(room == null)
            throw new NullPointerException("Attempted to set target room to null.");
        this.room = room;
    }

    @Override
    public Player getPlayer() {
        throw new TargetInheritanceException("Constraints on players cannot be applied to rooms.");
    }

    @Override
    public Cell getCell() {
        throw new TargetInheritanceException("Constraints on cells cannot be applied to rooms.");
    }

    @Override
    public Room getRoom() {
        return room;
    }

    public List<Room> filter() {
        return Constraint.filterRooms(context, constraints);
    }
}
