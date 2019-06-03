package it.polimi.ingsw.model.weaponry.targets;

import it.polimi.ingsw.model.board.Room;
import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.exceptions.TargetInheritanceException;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.weaponry.constraints.Constraint;

import java.util.List;

public class TargetCell extends Target {
    private Cell cell;

    public TargetCell(String message, List<Constraint> constraints) {
        this.message = message;
        this.constraints = constraints;
        this.type = TargetType.CELL;
    }

    public void setCell(Cell cell) {
        if(cell == null)
            throw new NullPointerException("Attempted to set target cell to null.");
        this.cell = cell;
    }

    @Override
    public Player getPlayer() {
        throw new TargetInheritanceException("Constraints on players cannot be applied to rooms.");
    }

    @Override
    public Cell getCell() {
        return cell;
    }

    @Override
    public Room getRoom() {
        return cell.getRoom();
    }

    public List<Cell> filter() {
        return Constraint.filterCells(context, constraints);
    }
}
