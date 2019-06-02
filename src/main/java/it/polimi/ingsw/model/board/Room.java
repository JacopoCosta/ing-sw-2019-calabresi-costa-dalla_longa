package it.polimi.ingsw.model.board;

import it.polimi.ingsw.model.cell.Cell;

import java.util.List;

// a room is a collection of cells -- used when determining visibility
public class Room {
    private String color;
    private List<Cell> cells;

    public Room(String color, List<Cell> cells) {
        this.color = color;
        this.cells = cells;

        for(Cell c : cells)
            c.setRoom(this);
    }

    public boolean contains(Cell cell) {
        return this.cells.contains(cell);
    }

    public List<Cell> getCells() {
        return cells;
    }

    @Override
    public String toString() {
        return color + " room";
    }
}
