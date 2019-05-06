package it.polimi.ingsw.network.common.exceptions;

public class ConnectionLostException extends Exception {
    public ConnectionLostException(Exception e) {
        super(e);
    }
}
