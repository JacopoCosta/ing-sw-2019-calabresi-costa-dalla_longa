package it.polimi.ingsw.model.exceptions;

public class NullCellOperationException extends RuntimeException { // TODO this will no longer be Runtime
    public NullCellOperationException(String message) {
        super(message);
    }
}
