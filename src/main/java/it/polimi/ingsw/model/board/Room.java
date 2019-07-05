package it.polimi.ingsw.model.board;

import it.polimi.ingsw.model.cell.Cell;

import java.util.List;

/**
 * A {@link Room} is a collection of {@link Cell}s grouped by the property of always being visible to one another.
 */
public class Room {
    /**
     * The {@link Room}'s colour, only used for representation.
     */
    private String color;

    /**
     * The list of {@link Cell}s included in the {@link Room}.
     */
    private List<Cell> cells;

    /**
     * This is the only constructor.
     *
     * @param color The colour of the {@link Room}.
     * @param cells The list of {@link Cell}s to be included in the {@link Room}.
     */
    public Room(String color, List<Cell> cells) {
        this.color = color;
        this.cells = cells;

        for(Cell c : cells)
            c.setRoom(this);
    }

    /**
     * Tells whether or not the {@link Room} contains the {@link Cell} passed as argument.
     *
     * @param cell the {@link Cell} of interest.
     * @return whether or not the {@link Room} contains the {@link Cell}.
     */
    public boolean contains(Cell cell) {
        return this.cells.contains(cell);
    }

    /**
     * Returns the name of the {@code Room}'s {@link #color}.
     *
     * @return the name of the {@code Room}'s {@link #color}.
     */
    public String getColor() {
        return color;
    }

    /**
     * Returns the list of {@link Cell}s that make up the {@link Room}.
     *
     * @return the list.
     */
    public List<Cell> getCells() {
        return cells;
    }

    /**
     * Creates a string with a short description of the room.
     *
     * @return the string.
     */
    @Override
    public String toString() {
        return color + " room";
    }
}
