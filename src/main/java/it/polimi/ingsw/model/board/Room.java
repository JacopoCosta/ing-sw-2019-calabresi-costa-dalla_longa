package it.polimi.ingsw.model.board;

import it.polimi.ingsw.model.cell.Cell;

import java.util.List;

// a room is a collection of cells -- used when determining visibility
public class Room {
    private List<Cell> cells;

    public Room(List<Cell> cells) {
        this.cells = cells;

        for(Cell c : cells)
            c.setRoom(this);
    }

    public boolean contains(Cell cell) {
        return this.cells.contains(cell);
    }
}
