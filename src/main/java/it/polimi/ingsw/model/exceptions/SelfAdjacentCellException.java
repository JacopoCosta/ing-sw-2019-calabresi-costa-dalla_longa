package it.polimi.ingsw.model.exceptions;

import it.polimi.ingsw.model.cell.Cell;

/**
 * This exception is thrown when attempting to set a cell adjacent to itself. Since the adjacency structure
 * is generated right at the beginning of the game, and since its main cause is a flaw in the game logic, this
 * exception is declared as runtime, since it would be pointless to continue the execution with an obviously incomplete
 * or plain wrong network of adjacencies between cells.
 * @see it.polimi.ingsw.model.cell.Cell#setAdjacent(Cell)
 */
public class SelfAdjacentCellException extends RuntimeException {
    public SelfAdjacentCellException(String message) {
        super(message);
    }
}
