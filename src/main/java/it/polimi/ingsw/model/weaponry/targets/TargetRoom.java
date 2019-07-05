package it.polimi.ingsw.model.weaponry.targets;

import it.polimi.ingsw.model.board.Room;
import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.exceptions.TargetInheritanceException;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.weaponry.constraints.Constraint;

import java.util.List;

public class TargetRoom extends Target {
    /**
     * The chosen {@link Room}.
     */
    private Room room;

    /**
     * This is the only constructor.
     *
     * @param message     The message to be presented to the user when they need to choose their target.
     * @param constraints The rules that a target needs to satisfy in order to be eligible.
     */
    TargetRoom(String message, List<Constraint> constraints) {
        this.message = message;
        this.constraints = constraints;
        this.type = TargetType.ROOM;
    }

    /**
     * Sets the {@link #room} attribute.
     *
     * @param room the {@link Room} to set as the new value.
     */
    public void setRoom(Room room) {
        if(room == null)
            throw new NullPointerException("Attempted to set target room to null.");
        this.room = room;
    }

    /**
     * Throws {@link TargetInheritanceException} because it is impossible to uniquely identify
     * a {@link Player} given a {@link Room}.
     *
     * @return nothing.
     */
    @Override
    public Player getPlayer() {
        throw new TargetInheritanceException("Constraints on players cannot be applied to rooms.");
    }

    /**
     * Throws {@link TargetInheritanceException} because it is impossible to uniquely identify
     * a {@link Cell} given a {@link Room}.
     *
     * @return nothing.
     */
    @Override
    public Cell getCell() {
        throw new TargetInheritanceException("Constraints on cells cannot be applied to rooms.");
    }

    /**
     * Returns the chosen {@link TargetRoom#room}.
     *
     * @return the {@link Room}.
     */
    @Override
    public Room getRoom() {
        return room;
    }

    /**
     * Creates a list containing all the {@link Room}s available to choose as {@code TargetRoom}s
     * that respect all of the {@link Target#constraints}.
     *
     * @return the list.
     */
    public List<Room> filter() {
        return Constraint.filterRooms(context, constraints);
    }
}
