package it.polimi.ingsw.model.board;

import it.polimi.ingsw.model.cell.Cell;

import java.util.List;

/**
 * A room is a collection of cells grouped by the property of always being visible to one another.
 */
public class Room {
    /**
     * The room's colour, only used for representation.
     */
    private String color;

    /**
     * The list of cells included in the room.
     */
    private List<Cell> cells;

    /**
     * This is the only constructor.
     * @param color The colour of the room.
     * @param cells The list of cells to be included in the room.
     */
    public Room(String color, List<Cell> cells) {
        this.color = color;
        this.cells = cells;

        for(Cell c : cells)
            c.setRoom(this);
    }

    /**
     * Tells whether or not the room contains the cell passed as argument.
     * @param cell the cell of interest.
     * @return whether or not the room contains the cell.
     */
    public boolean contains(Cell cell) {
        return this.cells.contains(cell);
    }

    /**
     * Returns the list of cells that make up the room.
     * @return the list of cells that make up the room.
     */
    public List<Cell> getCells() {
        return cells;
    }

    @Override
    public String toString() {
        return color + " room";
    }
}
