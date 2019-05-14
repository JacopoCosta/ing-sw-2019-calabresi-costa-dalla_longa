package it.polimi.ingsw.network.common.exceptions;

public class ConfigurationException extends Exception {
    public ConfigurationException(String s) {
        super(s);
    }

    public ConfigurationException(String s, Exception e) {
        super(s, e);
    }
}