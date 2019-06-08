package it.polimi.ingsw.model.exceptions;

/**
 * This method is thrown when attempting to evaluate any binary predicative or functional method in the class {@code Cell},
 * when passing {@code null} as argument.
 * @see it.polimi.ingsw.model.cell.Cell
 */
public class NullCellOperationException extends Exception {
    public NullCellOperationException(String message) {
        super(message);
    }
}
