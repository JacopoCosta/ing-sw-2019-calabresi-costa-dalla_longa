package it.polimi.ingsw.model.exceptions;

import it.polimi.ingsw.model.cell.Cell;

/**
 * This method is thrown when attempting to evaluate any binary predicative or functional method in the class {@link Cell},
 * when passing {@code null} as argument.
 * @see it.polimi.ingsw.model.cell.Cell
 */
public class NullCellOperationException extends Exception {
    public NullCellOperationException(String message) {
        super(message);
    }
}
