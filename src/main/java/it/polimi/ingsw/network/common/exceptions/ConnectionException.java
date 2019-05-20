package it.polimi.ingsw.network.common.exceptions;

public class ConnectionException extends Exception {
    public ConnectionException(String s, Exception e) {
        super(s, e);
    }
    public ConnectionException(String s){
        super(s);
    }
}
