package it.polimi.ingsw.model.weaponry.targets;

import it.polimi.ingsw.model.board.Room;
import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.exceptions.TargetInheritanceException;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.weaponry.constraints.Constraint;

import java.util.List;

public class TargetCell extends Target {
    /**
     * The chosen {@link Cell}.
     */
    private Cell cell;

    public TargetCell(String message, List<Constraint> constraints) {
        this.message = message;
        this.constraints = constraints;
        this.type = TargetType.CELL;
    }

    /**
     * Sets the {@link Target}'s {@link TargetCell#cell}.
     *
     * @param cell the user's choice.
     */
    public void setCell(Cell cell) {
        if(cell == null)
            throw new NullPointerException("Attempted to set target cell to null.");
        this.cell = cell;
    }

    /**
     * Throws {@link TargetInheritanceException} because it is impossible to uniquely identify
     * a {@link Player} given a {@link Cell}.
     *
     * @return nothing.
     */
    @Override
    public Player getPlayer() {
        throw new TargetInheritanceException("Constraints on players cannot be applied to rooms.");
    }

    /**
     * Returns the chosen {@link TargetCell#cell}.
     *
     * @return the {@link Cell}
     */
    @Override
    public Cell getCell() {
        return cell;
    }

    /**
     * Returns the room containing the chosen {@link TargetCell#cell}.
     *
     * @return the {@link Room}
     */
    @Override
    public Room getRoom() {
        return cell.getRoom();
    }

    /**
     * Creates a list containing all the {@link Cell}s available to choose as {@code TargetCell}s
     * that respect all of the {@link Target#constraints}.
     *
     * @return the list.
     */
    public List<Cell> filter() {
        return Constraint.filterCells(context, constraints);
    }
}
