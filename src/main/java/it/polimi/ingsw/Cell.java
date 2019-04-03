package it.polimi.ingsw;

import java.util.List;
import java.util.ArrayList;

// the cell is the unit of space on the board
public abstract class Cell {
    protected List<Cell> adjacentCells;
    protected Room room;
    protected int xCoord;
    protected int yCoord;

    public Cell(int xCoord, int yCoord) {
        this.adjacentCells = new ArrayList<>();
        this.xCoord = xCoord;
        this.yCoord = yCoord;
    }

    // each cell belongs to a room -- this is used when determining visibility
    public void setRoom(Room room) {
        this.room = room;
    }

    public Room getRoom() {
        return room;
    }

    public void setAdjacent(Cell cell) throws Exception {
        if(cell != this) {
            this.adjacentCells.add(cell);
            cell.adjacentCells.add(this);
        }
        else
            throw new Exception();
    }

    public List<Cell> getAdjacentCells() {
        return this.adjacentCells;
    }

    // the distance between two cells is the length of shortest cardinal walk between them -- walls must not be traversed
    // the distance between a cell and itself is 0
    public int distance(Cell cell) {
        // trivial case -- the cell is distant 0 from itself
        if(this == cell)
            return 0;

        int distance = 1;
        boolean found = false;
        List<Cell> alreadyVisited = new ArrayList<>();
        List<Cell> visiting = new ArrayList<>();

        // this has already been visited (in the trivial case check)
        alreadyVisited.add(this);

        // start a Breadth-First Search (BFS) algorithm from this
        while(!found) {
            for(Cell av : alreadyVisited) // for each already visited cell
                for(Cell adj : av.getAdjacentCells()) // visit all of its adjacent cells
                    if(!alreadyVisited.contains(adj)) // avoid re-visiting already visited cells
                        visiting.add(adj);
            if(visiting.contains(cell))
                found = true;
            else
                distance ++;

            // move all the currently visited to already visited
            alreadyVisited.addAll(visiting);
        }
        return distance;
    }

    // two cells are adjacent if and only if the distance between them is 1
    // a cell is NOT adjacent to itself
    public boolean isAdjacent(Cell cell) {
        return this.adjacentCells.contains(cell);
    }

    // a cell can see any cell that belongs to any room that contains any cell that is adjacent to it
    // a cell can see itself
    public boolean canSee(Cell cell) {
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

    // two cells are aligned if they share either the x or the y coordinate
    // a cell is aligned to itself
    public boolean isAligned(Cell cell) {
        return cell.xCoord == this.xCoord || cell.yCoord == this.yCoord;
    }
}
