package it.polimi.ingsw.model.cell;

import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.board.Room;
import it.polimi.ingsw.model.exceptions.NullCellOperationException;
import it.polimi.ingsw.model.exceptions.SelfAdjacentCellException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * The cell is the unit of space on the board. Cells are organized in a 2-dimensional
 * lattice space, forming a grid of 10 ~ 12 units that fits in a bounding box spanning
 * 4 units horizontally and 3 units vertically. The coordinate system used to identify cells
 * is a clockwise pair of axes with the `x` coordinate increasing to the right and the `y`
 * coordinate increasing downwards.
 * Cells are grouped into rooms, this is important when evaluating visibility between two cells.
 *
 * @see Room
 * @see it.polimi.ingsw.model.board.Board
 */

public abstract class Cell {

    /**
     * A list of all the cells considered to be adjacent to the current cell.
     */
    protected List<Cell> adjacentCells;

    /**
     * The board this cell belongs to.
     */
    protected Board board;

    /**
     * The room this cell belongs to.
     */
    protected Room room;

    /**
     * The horizontal coordinate of the cell on the board.
     */
    protected int xCoord;

    /**
     * The vertical coordinate of the cell on the board.
     */
    protected int yCoord;

    /**
     * Indicates if a cell is instance of {@code SpawnCell} (the alternative being {@code AmmoCell}).
     * @see SpawnCell
     * @see AmmoCell
     */
    protected boolean spawnPoint;

    /**
     * This is the only constructor.
     * @param xCoord the `x` (horizontal) coordinate in the 2-dimensional discrete space this cell will be put at
     * @param yCoord the `y` (vertical) coordinate in the 2-dimensional discrete space this cell will be put at
     */
    public Cell(int xCoord, int yCoord) {
        this.adjacentCells = new ArrayList<>();
        this.xCoord = xCoord;
        this.yCoord = yCoord;
    }

    /**
     *
     */
    public int getId() {
        return board.getCells().indexOf(this) + 1;
    }

    /**
     * This method is used to distinguish between the two possible implementations of this class.
     * @return whether or not this cell acts as a spawn point.
     * @see SpawnCell
     * @see AmmoCell
     */
    public boolean isSpawnPoint() {
        return spawnPoint;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    /**
     * This method tells which board a cell belongs to.
     * @return the board containing the cell this method was called upon.
     */
    public Board getBoard() {
        return board;
    }

    /**
     * When constructing a new room, this method is called on each cell to set its membership in the room.
     * @param room the room needed to contain the cell this method was called upon.
     */
    public void setRoom(Room room) {
        this.room = room;
    }

    /**
     * This method tells which room a cell belongs to.
     * @return the room containing the cell this method was called upon.
     */
    public Room getRoom() {
        return room;
    }

    /**
     * This method tells the horizontal coordinate of a cell on the board.
     * @return the horizontal coordinate of the cell.
     */
    public int getXCoord() {
        return this.xCoord;
    }

    /**
     * This method tells the vertical coordinate of a cell on the board.
     * @return the vertical coordinate of the cell.
     */
    public int getYCoord() {
        return this.yCoord;
    }

    /**
     * This method sets a cell adjacent to another cell.
     * @param cell the cell to be declared as adjacent to the cell this method was called upon.
     * @throws SelfAdjacentCellException on an attempt to set a cell adjacent to itself.
     */
    public void setAdjacent(Cell cell) throws SelfAdjacentCellException {
        if(cell != this) {
            this.adjacentCells.add(cell);
            cell.adjacentCells.add(this);
        }
        else
            throw new SelfAdjacentCellException("Attempted to set a cell adjacent to itself.");
    }

    /**
     * This method is used to get the cells of a cell's neighbourhood.
     * @return a list containing all of the cells adjacent to the cell this method is called upon.
     * @see Cell#isAdjacent(Cell)
     */
    public List<Cell> getAdjacentCells() {
        return this.adjacentCells;
    }

    /**
     * This method tells the distance between two cells.
     * The distance between two cells is defined as the length of the shortest possible walk between them.
     * A walk between cells is a sequence of steps, where every step connects a cell to its neighbour,
     * and two consecutive steps must have a cell in common (i.e. the walk must be unbroken).
     * The distance between a cell and itself overrides the previous definition and is defined to be 0.
     * @param cell the target cell.
     * @return the distance between the cell this method is called upon and the cell passed as argument.
     */
    public int distance(Cell cell) throws NullCellOperationException {
        if(cell == null)
            throw new NullCellOperationException("Attempted to measure distance from a null cell.");

        List<Cell> visited = new ArrayList<>();
        // consider the starting cell already visited
        visited.add(this);

        int distance;
        for(distance = 0; !visited.contains(cell); distance ++) {
            // any cell that is adjacent to already visited cells (without repetitions) excluding the already visited ones
            visited.addAll(visited.stream()
                    .map(c -> c.getAdjacentCells().stream())
                    .flatMap(Function.identity())
                    .sorted(Comparator.comparingInt(Cell::getId))
                    .distinct()
                    .filter(c -> !visited.contains(c))
                    .collect(Collectors.toList())
            );
        }
        return distance;
    }

    /**
     * This method tells if a cell is adjacent to another cell.
     * A cell is adjacent to another cell if and only if the distance separating them is equal to 1.
     * This implies a cell is not adjacent to itself.
     * An adjacent cell may also be referred to as a neighbour.
     * Note that this relationship is symmetrical (i.e. if cell A is adjacent to cell B, then also cell B
     * is adjacent to cell A).
     * @param cell the comparison cell.
     * @return whether or not the cell this method is called upon is adjacent to the cell passed as argument.
     * @see Cell#distance(Cell)
     */
    public boolean isAdjacent(Cell cell) throws NullCellOperationException {
        if(cell == null)
            throw new NullCellOperationException("Attempted to measure adjacency with a null cell.");
        return this.adjacentCells.contains(cell);
    }

    /**
     * This method tells if a cell could be adjacent to another cell, if there were no walls between them.
     * This implies a cell is not {@code ghostlyAdjacent} to itself, and two adjacent cells are also {@code ghostlyAdjacent}
     * (while the vice versa may not be true).
     * Note that, just like {@code isAdjacent} method, this relationship is symmetrical as well.
     * @see Cell#isAdjacent(Cell)
     * @param cell te comparison cell
     * @return whether or not the cell is ghostlyAdjacent to the given cell
     */
    public boolean isGhostlyAdjacent(Cell cell) throws NullCellOperationException {
        if(cell == null)
            throw new NullCellOperationException("Attempted to measure ghostly adjacency with a null cell.");
        return ((xCoord - cell.getXCoord() == 1) != (yCoord - cell.getYCoord() == 1));
    }

    /**
     * This method tells if a cell is visible from another cell.
     * A cell can see another cell if and only if it belongs to a room containing any of the first cell's neighbours.
     * A cell can always see itself.
     * @param cell the target cell.
     * @return whether or not the cell this method is called upon is able to see the cell passed as argument.
     */
    public boolean canSee(Cell cell) throws NullCellOperationException {
        if(cell == null)
            throw new NullCellOperationException("Attempted to measure visibility with a null cell.");

        List<Room> adjacentRooms = new ArrayList<>();
        // for each adjacent cell, get the room containing it
        for(Cell c : this.adjacentCells) {
            Room r = c.getRoom();
            if(!adjacentRooms.contains(r))
                adjacentRooms.add(r);
        }
        // for each room found above, if any contains the cell, then the cell is visible
        for(Room r : adjacentRooms)
            if(r.contains(cell))
                return true;
        // otherwise
        return false;
    }

    /**
     * This method tells if a cell is aligned with another cell.
     * Two cells are aligned if there exists a straight path (ignoring walls) that includes both cells.
     * In order for the path to be straight, either of its coordinates must remain constant along its span,
     * meaning that any two cells sharing a coordinate are aligned.
     * This implies a cell is aligned to itself.
     * @param cell the comparison cell.
     * @return whether or not the cell this method is called upon is aligned with the cell passed as argument.
     */
    public boolean isAligned(Cell cell) throws NullCellOperationException {
        if(cell == null)
            throw new NullCellOperationException("Attempted to measure alignment with a null cell.");
        return cell.xCoord == this.xCoord || cell.yCoord == this.yCoord;
    }

    /**
     * This method tells if a cell is between two other cells.
     * A cell is between two other cells if there exists a straight path (ignoring walls) including all three cells,
     * such that the cell is the second one encountered on this path, when run across in either direction.
     * A cell is considered between itself and another cell.
     * A cell is also considered between itself and itself.
     *
     * @param cell1 the first comparison cell
     * @param cell2 the second comparison cell
     * @return whether or not the cell this method is called upon is between the two cells passed as arguments.
     * @see Cell#isAligned(Cell)
     */
    public boolean isBetween(Cell cell1, Cell cell2) throws NullCellOperationException {
        if(cell1 == null || cell2 == null)
            throw new NullCellOperationException("Tried to verify alignment with null cells.");

        if(cell1.xCoord == this.xCoord && this.xCoord == cell2.xCoord)
            return (cell1.yCoord - this.yCoord) * (this.yCoord - cell2.yCoord) >= 0;

        if(cell1.yCoord == this.yCoord && this.yCoord == cell2.yCoord)
            return (cell1.xCoord - this.xCoord) * (this.xCoord - cell2.xCoord) >= 0;

        return false;
    }

    @Override
    public String toString() {
        return "Cell #" + getId() + " (" + xCoord + "," + yCoord + ")";
    }
}
