package it.polimi.ingsw.network.common.exceptions;

public class ConnectionException extends Exception {
    public ConnectionException(Exception e) {
        super(e);
    }
    public ConnectionException(String s){
        super(s);
    }
}
